

# 笛卡尔树

$O(log N)$
$2 ^ N$


```cpp{.line-numbers}
/**笛卡尔树就是平衡搜索二叉树与堆的构建过程，目的在于解决区间内最值查询与LCA（最近公共祖先）的查询
*笛卡尔树本是静态的，但是加上Treap之后就会成为动态，Treap是随机产生优先级然后让笛卡尔树去维护
*笛卡尔树是以下标为构建的BST，正常的BST是以值为构建的
*值是上下关系（堆）：小的在上，大的在下 → 决定谁是父、谁是子
下标是左右关系（BST）：小的在左，大的在右 → 决定子节点放在父的左边还是右边
*/

/**
 *  能解决的问题
 *  查询区间最小值	            小根堆	        根是最小值，LCA 就是区间最小值
    查询区间最大值	            大根堆	        根是最大值，LCA 就是区间最大值
    BST 插入顺序还原	        小根堆（按值）	 根是最先插入的（值最小）
    “笛卡尔树 +大根堆” 变体	     大根堆	         取决于你对“优先级”的定义
 */
#include <iostream>
#include <vector>
#include <functional>
#include <stack>
using namespace std;

// 构建笛卡尔树（小根堆）,建树为O（n）,构建的树与BST一样（一个是下标，一个是值），但速度更快
// arr: 输入数组，存储每个位置的值
// n: 数组长度
// ls, rs: 分别存储每个节点的左孩子和右孩子下标，-1表示没有
// 返回值: 根节点下标
int buildCartesianTree(vector<int>& arr, int n, vector<int>& ls, vector<int>& rs) {
    stack<int> stk;  // 单调栈，存下标  
    vector<int> parent(n, -1);//父亲节点，最后用来找根
    
    for (int i = 0; i < n; i++) {
        int last = -1;
        // 维护栈的单调性（小根堆：栈内值递增，就是下标递增）
        // 大根堆就是比较时反着来
        while (!stk.empty() && arr[stk.top()] > arr[i]) {
            last = stk.top();
            stk.pop();
        }
        // 当前节点的左孩子是最后一个被弹出的节点
        if (last != -1) {
            ls[i] = last;
            parent[last] = i;
        }
        // 如果栈不空，栈顶节点是当前节点的父节点，当前节点是它的右孩子
        if (!stk.empty()) {
            rs[stk.top()] = i;
            parent[i] = stk.top();
        }
        stk.push(i);
    }
     
    // 找根节点
    int root = -1;
    for (int i = 0; i < n; i++) {
        if (parent[i] == -1) {
            root = i;
            break;
        }
    }
    return root;
}

int main() {
    vector<int> arr = {3, 2, 1, 4, 5};
    int n = arr.size();
    
    vector<int> ls(n, -1);   // left child
    vector<int> rs(n, -1);   // right child
    
    int root = buildCartesianTree(arr, n, ls, rs);
    
    // 中序遍历验证，因为中序遍历就是BST的插入顺序
    function<void(int)> inorder = [&](int u) {
        if (u == -1) return;
        inorder(ls[u]);
        cout << arr[u] << " ";
        inorder(rs[u]);
    };
    
    cout << "中序遍历: ";
    inorder(root);
    cout << endl;
    
    return 0;
}
 



```
