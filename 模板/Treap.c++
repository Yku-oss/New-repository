#include <bits/stdc++.h>
using namespace std;

const int N = 100010;

struct Node {
    int ch[2];   // 左右孩子
    int val;     // 节点值（中序遍历为原序列）
    int pri;     // 随机优先级（堆性质依据）
    int sz;      // 子树大小
    int rev;     // 懒标记（区间翻转）
} tr[N];

int root, idx;

// 更新节点大小
void pushup(int u) {
    tr[u].sz = tr[tr[u].ch[0]].sz + tr[tr[u].ch[1]].sz + 1;
}

// 下传翻转标记
void pushdown(int u) {
    if (tr[u].rev) {
        swap(tr[u].ch[0], tr[u].ch[1]);
        tr[tr[u].ch[0]].rev ^= 1;
        tr[tr[u].ch[1]].rev ^= 1;
        tr[u].rev = 0;
    }
}

// 新建节点
int new_node(int val) {
    tr[++idx].val = val;
    tr[idx].pri = rand();  // 随机优先级
    tr[idx].sz = 1;
    return idx;
}

// 分裂：将树分成左右两棵，左树大小为 k
void split(int u, int k, int &x, int &y) {
    if (!u) { x = y = 0; return; }
    pushdown(u);
    if (tr[tr[u].ch[0]].sz < k) {
        x = u;
        split(tr[u].ch[1], k - tr[tr[u].ch[0]].sz - 1, tr[u].ch[1], y);
    } else {
        y = u;
        split(tr[u].ch[0], k, x, tr[u].ch[0]);
    }
    pushup(u);
}

// 合并：将两棵树合并（保证 x 树所有值 < y 树）
int merge(int x, int y) {
    if (!x || !y) return x + y;
    if (tr[x].pri < tr[y].pri) {
        pushdown(x);
        tr[x].ch[1] = merge(tr[x].ch[1], y);
        pushup(x);
        return x;
    } else {
        pushdown(y);
        tr[y].ch[0] = merge(x, tr[y].ch[0]);
        pushup(y);
        return y;
    }
}

// 区间翻转 [l, r]
void reverse(int l, int r) {
    int x, y, z;
    split(root, l - 1, x, y);
    split(y, r - l + 1, y, z);
    tr[y].rev ^= 1;          // 打上翻转标记
    root = merge(x, merge(y, z));
}

// 中序遍历输出
void inorder(int u) {
    if (!u) return;
    pushdown(u);
    inorder(tr[u].ch[0]);
    cout << tr[u].val << " ";
    inorder(tr[u].ch[1]);
}

int main() {
    srand(time(0));
    int n, m;
    cin >> n >> m;
    // 建树：将 1~n 依次插入
    for (int i = 1; i <= n; i++) {
        root = merge(root, new_node(i));
    }
    while (m--) {
        int l, r;
        cin >> l >> r;
        reverse(l, r);
    }
    inorder(root);
    return 0;
}