package demo.calcite;

import demo.calcite.utils.CalciteUtils;
import lombok.Data;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelDistributionTraitDef;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.rules.FilterJoinRule;
import org.apache.calcite.rel.rules.PruneEmptyRules;
import org.apache.calcite.rel.rules.ReduceExpressionsRule;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql2rel.RelDecorrelator;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.RelBuilder;

import java.util.Properties;

/**
 * @Description CBOTest.
 * @Author lishoupeng
 * @Date 2022/12/28 16:25
 */
@Data
public class CBOTest {

    private static final SchemaPlus schemaPlus = CalciteUtils.registerRootSchema();
    private static final SqlTypeFactoryImpl factory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
    private static final CalciteCatalogReader calciteCatalogReader = new CalciteCatalogReader(
            CalciteSchema.from(schemaPlus),
            CalciteSchema.from(schemaPlus).path(null),
            factory,
            new CalciteConnectionConfigImpl(new Properties())
    );
    private static final FrameworkConfig frameworkConfig = Frameworks.newConfigBuilder()
            .parserConfig(SqlParser.Config.DEFAULT)
            .defaultSchema(schemaPlus)
            .traitDefs(ConventionTraitDef.INSTANCE, RelDistributionTraitDef.INSTANCE)
            .build();

    public static VolcanoPlanner createPlanner() {
        // use VolcanoPlanner
        VolcanoPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        planner.addRelTraitDef(RelDistributionTraitDef.INSTANCE);
        // add rules
        planner.addRule(FilterJoinRule.FILTER_ON_JOIN);
        planner.addRule(ReduceExpressionsRule.PROJECT_INSTANCE);
        planner.addRule(PruneEmptyRules.PROJECT_INSTANCE);
        // add ConverterRule
        planner.addRule(EnumerableRules.ENUMERABLE_MERGE_JOIN_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_SORT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_VALUES_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_PROJECT_RULE);
        planner.addRule(EnumerableRules.ENUMERABLE_FILTER_RULE);
        return planner;
    }

    public static SqlValidator createValidator() {
        /**
         * https://javadoc.io/doc/org.apache.calcite/calcite-core/1.18.0/org/apache/calcite/sql/validate/SqlValidatorUtil.html
         *
         * SqlOperatorTable defines a directory interface for enumerating and looking up SQL operators and functions. --查找 SQL 运算符和函数
         * presenting the repository information of interest to the validator. --向验证者展示感兴趣的存储库信息
         * RelDataTypeFactory is a factory for datatype descriptors. --数据类型描述符的工厂
         * Enumeration of valid SQL compatibility modes. --枚举有效的 SQL 兼容模式
         */
        return SqlValidatorUtil.newValidator(
                SqlStdOperatorTable.instance(),
                calciteCatalogReader,
                new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT),
                SqlConformanceEnum.DEFAULT
        );
    }

    public static SqlNode parse(String sql) throws SqlParseException {
        SqlParser parser = SqlParser.create(sql, SqlParser.Config.DEFAULT);
        return parser.parseStmt();
    }

    public static SqlNode validate(SqlNode sqlNode, SqlValidator validator) {
        return validator.validate(sqlNode);
    }

    public static RelNode sQLNode2RelNode(SqlNode sqlNode, SqlValidator sqlValidator, VolcanoPlanner planner) {
        final RexBuilder rexBuilder = CalciteUtils.createRexBuilder(factory);
        final RelOptCluster cluster = RelOptCluster.create(planner, rexBuilder);

        // init SqlToRelConverter config
        final SqlToRelConverter.Config config = SqlToRelConverter.configBuilder()
                .withConfig(frameworkConfig.getSqlToRelConverterConfig())
                .withTrimUnusedFields(false)
                .withConvertTableAccess(false)
                .build();
        // SqlNode toRelNode
        final SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(
                new CalciteUtils.ViewExpanderImpl(),
                sqlValidator,
                calciteCatalogReader,
                cluster,
                frameworkConfig.getConvertletTable(),
                config
        );
        RelRoot relRoot = sqlToRelConverter.convertQuery(sqlNode, false, true);

        relRoot = relRoot.withRel(sqlToRelConverter.flattenTypes(relRoot.rel, true));
        final RelBuilder relBuilder = config.getRelBuilderFactory().create(cluster, null);
        relRoot = relRoot.withRel(RelDecorrelator.decorrelateQuery(relRoot.rel, relBuilder));
        return relRoot.rel;
    }

    public static RelNode relNodeFindBestExp(RelNode relNode, VolcanoPlanner planner) {
        RelTraitSet desiredTraits = relNode.getCluster().traitSet().replace(EnumerableConvention.INSTANCE);
        relNode = planner.changeTraits(relNode, desiredTraits);
        planner.setRoot(relNode);
        return planner.findBestExp();
    }

    public static void main(String[] args) throws SqlParseException {
        // users 表的字段叫 id, 这里特意写成 ids, 看下验证的逻辑
        String sql = "select u.id as user_id, u.name as user_name, j.company as user_company, u.age as user_age " +
                "from users u join jobs j on u.id=j.id " +
                "where u.age > 30 and j.id > 10 " +
                "order by user_id";

        SqlNode parsedSqlNode = parse(sql);
        Visitor visitor = new Visitor();
        parsedSqlNode.accept(visitor);
        System.out.println("The SqlNode after parsed is:" + parsedSqlNode);
        System.out.println("The raw sql: " + sql);
        System.out.println("------------------------");
        System.out.println("orderBy: " + visitor.getOrderByColumnNames());
        System.out.println("select: " + visitor.getSelectColumnNames());
        System.out.println("tables: " + visitor.getSelectTableNames());
        System.out.println("where: " + visitor.getWhereColumnNames());

        SqlValidator sqlValidator = createValidator();
        SqlNode validatedSqlNode = validate(parsedSqlNode, sqlValidator);
        System.out.println("The SqlNode after validated is:" + validatedSqlNode.toString());

        VolcanoPlanner planner = createPlanner();
        RelNode relNode = sQLNode2RelNode(validatedSqlNode, sqlValidator, planner);
        System.out.println("The relational expression string before optimized is:" + RelOptUtil.toString(relNode));

        RelNode bestExpRelNode = relNodeFindBestExp(relNode, planner);
        System.out.println("The Best relational expression string:" + RelOptUtil.toString(bestExpRelNode));
    }
}
