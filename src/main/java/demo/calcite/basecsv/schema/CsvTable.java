package demo.calcite.basecsv.schema;

import com.google.common.collect.Lists;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;
import org.apache.calcite.util.Source;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvTable extends AbstractTable implements ScannableTable {

    private final Source source;

    public CsvTable(Source source) {
        this.source = source;
    }

    /**
     * 讲字段名称与类型映射.
     */
    @Override
    public RelDataType getRowType(RelDataTypeFactory relDataTypeFactory) {
        List<String> names = Lists.newLinkedList();
        List<RelDataType> types = Lists.newLinkedList();
        try (BufferedReader reader = new BufferedReader(new FileReader(source.file()))) {
            String line = reader.readLine();
            List<String> lines = Lists.newArrayList(line.split(","));
            lines.forEach(column -> {
                String name = column.split(":")[0];
                String type = column.split(":")[1];
                names.add(name);
                types.add(relDataTypeFactory.createSqlType(SqlTypeName.get(type)));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return relDataTypeFactory.createStructType(Pair.zip(names, types));
    }

    @Override
    public Enumerable<Object[]> scan(DataContext dataContext) {
        return new AbstractEnumerable<Object[]>() {
            @Override
            public Enumerator<Object[]> enumerator() {
                return new CsvEnumerator<>(source);
            }
        };
    }
}