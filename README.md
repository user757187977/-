# 算法.数据结构

* * *

## 线性结构
### 数组 
`特点: 连续, 同类型的内存空间`
* 受限: 固定长度
* 操作: 高效的查询，低效的插入和删除
### 链表
...
### 队列
队列中的元素先进先出（FIFO=first in first out）

* 受限: 只能操作队首和队尾
* 操作: 队首删除-出队，队尾添加-入队

### 栈
后进先出（LIFO=last in, first out）

* 受限: 只能操作栈顶
* 操作: 添加元素到栈顶部-push，移除最顶元素-pop

## 非线性结构
### 树
一种递归的数据结构，基本算法有：前中后 和 层 序遍历，所谓的前中后都是指的根节点，如：前-根左右，中-左根右...

* 树的遍历算法常用的就是递归
* 如果使用迭代式去遍历的话，可以借助 栈 来进行
* 性质:
    * 如果树有 n 个顶点，那么就有 n-1 条边，这说明了树的定点数和边数是同阶的
    * 任何一个节点到根节点存在唯一路径，路径的长度为节点所处的深度

### 二叉树
节点度数不超过二的树
### 堆
堆其实是一种优先级队列
### 二叉查找树(二叉排序树)

* 性质:
    * 若左子树不空，则左子树上所有节点的值均小于它的根节点的值
    * 若右子树不空，则右子树上所有节点的值均大于它的根节点的值
    * 左、右子数也分别为二叉排序树
* 重要性质: 二叉查找树的中序遍历是一个有序数组
* 操作: 插入，查找，删除，找父节点，求最大值，求最小值
### 二叉平衡树
二叉查找树的查询复杂度取决于目标接地那到树根的距离(即深度)，因此当节点的深度普遍较大时，查询的均摊复杂度会上升。为了高效查询，产生平衡树。
* 平衡: 指所有叶子的深度趋于平衡, 更广义的是指在树上所有可能查找的均摊复杂度偏低.
* 操作: 旋转、插入、删除、查询前驱、查询后继
#### AVL
# 链表专题

* 各种数据结构，不管是队列，栈等线性结构还是数，图等非线性数据结构，从根本上底层都是数组和链表。
* 但是数组和链表对物理内存的使用是不一样的：![21c71f1c61e0fff412f4ebb397c4fd82.jpeg](evernotecid://8067429B-C134-4C7A-8F2E-C1B16E3516AD/appyinxiangcom/16336262/ENResource/p104)
* 数组是连续空间，并且每个单位的大小也是固定的，因此可以按下标随机访问。而链表则不一定连续，因此查找只能依靠别的方式，一般我们通过一个叫 next 指针来遍历查找。链表是一个结构体，data 存放数组，next 指向下一个节点的指针。
## 链表的基本操作
### 插入
插入只需要考虑要插入位置前驱节点和后继节点(双向链表下需要更新后继节点)即可，其他节点不受影响，因此在跟定指针的情况下操作时间复杂度为 O(1)，这里说的指针是插入位置的前驱节点。

伪代码:
```
temp = 待插入位置前节点.next
待插入位置前节点.next = 待插入节点
待插入节点.next = temp
```
如果没有给出待插入位置, 我们要先遍历找到节点, 因此最坏情况下时间复杂度为 0(N)
### 删除
只需要将需要删除的节点的前驱.next 修正为下个节点即可, 注意**边界条件**.
伪代码:
```
待删除位置的前驱节点.next = 待删除位置的前驱节点.next.next
```
>考虑头尾指针.

### 遍历
伪代码:
```
当前节点 = 头节点
while 当前节点不为空 {
    print(当前接地那)
    当前节点 = 当前节点.next
}
```

### 数组与链表的区别
二者逻辑基本一致, 区别是一些细微的操作, 比如遍历条件:
* 数组是索引++
* 链表是 当前节点 = 当前节点.next

如果向链表的尾部插入一个节点, 伪代码:
```
tail.next = new ListNode('新的尾节点')
tail = tail.next
```
这有什么用? 比如有的题目要求复制一个新的链表, 首先你需要一个新的链表头, 然后不断拼接尾节点, 那上面的伪代码就是拼接尾节点的过程.

## 链表题的解法
### 一个原则
先画图, 链表过程中我们很容易遇到 环 边界 的问题, 这些问题都可以画图思考.
### 两个考点
无外乎就两个考点:
* 指针的修改
* 链表的拼接
#### 指针的修改
对于数组型: 0, 1, 2, 3, 4,
其思路就是 0 和 4 换, 1和3换,
那么就定义两个指针, 一个指针从头到尾, 另外那个指针从尾到头,
```
int letft = 0;
int right = arr.length - 1;
while(left < right){
    String temp = arr[left];
    arr[left] = arr[right];
    arr[right] = temp;
    left += 1;
    right -= 1;
}
```
但是!!! 对于链表来说, 并不简单, 记住公式:
```
reverse(self, head: ListNode, tail: ListNode);
// head 是头
// tail 是尾
```
before:
![b3a1048ff029dc64b5dd18fc4431b021.jpeg](evernotecid://8067429B-C134-4C7A-8F2E-C1B16E3516AD/appyinxiangcom/16336262/ENResource/p105)
after:
![1b0abf0364ae28d42d6de26d32746fc4.jpeg](evernotecid://8067429B-C134-4C7A-8F2E-C1B16E3516AD/appyinxiangcom/16336262/ENResource/p106)
最后我们只需要返回 tail 即可(一个完整的链表只要给出头结点即可, 后续通过 next 获取)
![ae46f422668be5a371e5cd75e775c919.jpeg](evernotecid://8067429B-C134-4C7A-8F2E-C1B16E3516AD/appyinxiangcom/16336262/ENResource/p107)
反转
```
cur.next = pre
```
之后, 出现环了:
![46793c9dc62c5743fef215d93ce131b3.jpeg](evernotecid://8067429B-C134-4C7A-8F2E-C1B16E3516AD/appyinxiangcom/16336262/ENResource/p108)
解决:
```
next = cur.next;
cur.next = pre;
cur = next;
```
至此, 完整的伪代码:
```
void fanzhuan(head, tail){
    cur = head;
    pre = null;
    while (head < tail){
        next = cur.next;
        cur.next = pre;
        pre = cur;
        cur = next;
    }
}
```
### 三个技巧
#### 虚拟头
#### 快慢指针
#### 先整体再局部
先把基本代码写出来, 到底哪行在前哪行在后, a=b 还是 b=a, 什么时候判空... 这些先不用管, debug + 画图 自然就搞定了.

# 树专题
## 前言
### 基本概念
* 树的基本单位是节点, 节点之间的链接称为分支.
* 节点与分支形成树, 结构的开端称为根(root). 根节点之外的节点称为子节点. 没有链接到其他子节点的节点称为叶子节点.
* 树的高度: 节点到叶子结点的最大值就是其高度.
* 树的深度: 节点到根节点的最大值就是深度.
* 树的层: 根节点为第一层, 根的子节点称为第二层...

二叉树的表示一般使用:
* 链表
* 数组
### 技巧
#### 一个中心
树的遍历, 与树相关的题目都是跟遍历有关. 不会树的遍历后面都是白搭.
* 树的遍历只能从根节点开始.
* 左右子节点的访问顺序通常不重要.
* 树的遍历分为两种类型, 深度优先和广度优先.
#### 两个基本点
* 深度优先 DFS(Deep), 如: 前中后序遍历.
* 广度优先 BFS, 横向搜索, 细分为带层和不带层的.
##### 深度优先
算法流程

1. 首先将根节点放入 stack 中
2. 从 stack 取出, 验证取出的是否为目标. 如果找到目标, 结束搜搜返回. 否则将这个节点某一个尚未检查过的直接子节点放入 stack 中.
3. 重复步骤 2
4. 如果不存在未检测过的直接子节点. 将上一级节点放入 stack 中.
5. 重复步骤 4
6. 若 stack 为空, 表示整个树都已经遍历过了.

算法模板
```
public Object dfs(root){
    if(满足条件){
        返回
    }
    for(子节点: root.children){
        dfs(子节点)
    }
}
```
而我们目前见到的树类型算法就是二叉树, 因此我们将算法改良
```
public Object dfs(root){
    if(满足条件){
        返回
    }
    dfs(root.left)
    dfs(root.right)
}
```

##### 广度优先
BFS 采用横向搜索的方式, 在数据结构上通常采用队列结构.
注意: DFS 我们借助的是栈(不管是思路还是实现)来完成, 而这里借助的是队列.
BFS 适合找最短距离/路径和某一个距离的目标. 比如给定一个二叉树, 查找树的最后一行的最左边的值.
算法流程

1. 根节点入队列.
2. 从队列汇总取出第一个节点, 验证其是否为目标.
    1. 找到目标, 返回
    2. 否则将尚未检查过的直接子节点放入队列中
3. 若队列为空, 则整个树都没有目标.
4. 重复步骤 2.

算法模板
```
Map visited = new Map;
public Object BFS(root){
    Queue q = new Queue;
    q.push(root);
    while(q.length){
        Object o = q.pop();
        if(visited(o)) continue
        if(o 符合目标) return o;
        for(o 可达状态 j){
            if(j 符合目标){
                q.push(j)
            }
        }
    }
    return 没找到;
}
```

#### 三种题型
##### 搜索类
占比非常大, 而且搜索类只有两种解法, DFS 和 BFS.
几乎所有的搜索类题目都可以使用递归去实现. 但是还有一部分不好实现, 我们可以使用 BFS, 借助队列实现, 比如求二叉树任意两点的举例, 树的举例其实就是最短举例, 因此可以使用 BFS 解决.
所有的搜索的题目只要把握三个核心点, 即 **开始点**, **结束点**, **目标点**.
###### DFS
在 DFS 中, 这个结束点通常是 **叶子节点**或者**空节点**. 如果要求你这个算法的返回值是个基本值(比如节点的值)那么直接返回或者使用一个全局变量, 如果是一个数组, 则可以通过拓展参数的技巧来完成.
###### BFS
题型较少
##### 构建类
一般就是前中后层序转二叉树, 以原题做解法吧
##### 修改类
题型较少
#### 四个重要概念
##### 二叉搜索树

* 若左子树不为空, 则左子树上所有节点的值都小于它的根节点的值.
*

##### 完全二叉树
##### 路径
##### 距离
#### 七个技巧
##### DFS
##### 单/双递归
##### 前后遍历
##### 虚拟节点
##### 边界
##### 参数拓展
##### 返回数组/链表
### 总结
树的题目就是一个核心, **遍历**
而遍历有分为**深度优先**和**广度优先**
二叉树的题型就三种:
* 搜索类: 把握**开始点** **结束点** **目标**
* 构建类: 根据一种遍历结果确定根节点位置, 根据另外一种遍历结果确定左右子树. 这种提醒也不算太常见.
* 修改类: 题目不多
  二叉树有四个重要的概念, 对做题很有帮助:
* 完全二叉树
* 二叉搜索树
* 路径
* 距离
### 字符串
#### 滑动窗口
如果在一串字符串里找到子串, 类似这种, 滑动窗口比较合适
比如: 找无重复子串, 回文串