/**AVL树的核心目的就是保持平衡，快速达到查找，增删 */
#include <iostream>
#include <algorithm>
using namespace std;

/**
 * AVL树类：自平衡二叉搜索树
 * 确保树的高度始终保持在O(log n)，插入、删除、查找的时间复杂度为O(log n)
 */
class AVLTree {
private:
    /**
     * 节点结构体
     */
    struct Node {
        int val;        // 节点值
        Node *left;     // 左子节点
        Node *right;    // 右子节点
        int height;     // 节点高度（叶子节点高度为1）

        Node(int v) : val(v), left(nullptr), right(nullptr), height(1) {}
    };

    Node* root;  // 树的根节点

    /**
     * 获取节点高度（空节点高度为0）
     */
    int getH(Node* node) {
        return node ? node->height : 0;
    }

    /**
     * 计算平衡因子：左子树高度 - 右子树高度
     */
    int getBF(Node* node) {
        return node ? getH(node->left) - getH(node->right) : 0;
    }

    /**
     * 更新节点高度
     */
    void upH(Node* node) {
        if(node)
            node->height = 1 + max(getH(node->left), getH(node->right));
    }

    /**
     * 左旋操作（LL旋转）
     */
    Node* leftRot(Node* x) {
        Node* y = x->right;
        Node* T = y->left;
        y->left = x;
        x->right = T;
        upH(x);  // 先更新下层节点高度
        upH(y);
        return y;
    }

    /**
     * 右旋操作（RR旋转）
     */
    Node* rightRot(Node* x) {
        Node* y = x->left;
        Node* T = y->right;
        y->right = x;
        x->left = T;
        upH(x);
        upH(y);
        return y;
    }

    /**
     * 平衡节点：处理四种失衡情况（LL, RR, LR, RL）
     */
    Node* balance(Node* node) {
        if(!node) return nullptr;
        int bf = getBF(node);
        int bfL = getBF(node->left);
        int bfR = getBF(node->right);

        // LL 左左失衡 → 右旋
        if(bf > 1 && bfL >= 0)
            return rightRot(node);

        // RR 右右失衡 → 左旋
        if(bf < -1 && bfR <= 0)
            return leftRot(node);

        // LR 左右失衡 → 先左旋左子树，再右旋根
        if(bf > 1 && bfL < 0) {
            node->left = leftRot(node->left);
            return rightRot(node);
        }

        // RL 右左失衡 → 先右旋右子树，再左旋根
        if(bf < -1 && bfR > 0) {
            node->right = rightRot(node->right);
            return leftRot(node);
        }

        return node;  // 平衡
    }

    /**
     * 插入节点辅助函数（递归）
     */
    Node* insertHelper(Node* node, int val) {
        // BST 插入
        if(!node) return new Node(val);
        if(val < node->val)
            node->left = insertHelper(node->left, val);
        else if(val > node->val)
            node->right = insertHelper(node->right, val);
        else
            return node;  // 重复值不插入

        // 更新高度并平衡
        upH(node);
        return balance(node);
    }

    /**
     * 查找最小节点（用于删除）
     */
    Node* getMinNode(Node* node) {
        while(node->left) node = node->left;
        return node;
    }

    /**
     * 删除节点辅助函数（递归）
     */
    Node* removeHelper(Node* node, int val) {
        if(!node) return nullptr;

        if(val < node->val)
            node->left = removeHelper(node->left, val);
        else if(val > node->val)
            node->right = removeHelper(node->right, val);
        else {
            // 叶子或单孩子
            if(!node->left || !node->right) {
                Node* tmp = node->left ? node->left : node->right;
                delete node;
                return tmp;
            }
            // 两个孩子：用后继替换
            else {
                Node* suc = getMinNode(node->right);
                node->val = suc->val;
                node->right = removeHelper(node->right, suc->val);
            }
        }

        if(!node) return nullptr;

        // 更新高度并平衡
        upH(node);
        return balance(node);
    }

    /**
     * 查找辅助函数（递归）
     */
    bool searchHelper(Node* node, int val) {
        if(!node) return false;
        if(node->val == val) return true;
        return val < node->val ? searchHelper(node->left, val) : searchHelper(node->right, val);
    }

    /**
     * 中序遍历辅助函数（递归）
     */
    void inOrderHelper(Node* node) {
        if(!node) return;
        inOrderHelper(node->left);
        cout << node->val << " ";
        inOrderHelper(node->right);
    }

    /**
     * 清空树辅助函数（递归）
     */
    void clearTreeHelper(Node* node) {
        if(!node) return;
        clearTreeHelper(node->left);
        clearTreeHelper(node->right);
        delete node;
    }

public:
    /**
     * 构造函数
     */
    AVLTree() : root(nullptr) {}

    /**
     * 析构函数：自动清空树
     */
    ~AVLTree() {
        clearTreeHelper(root);
    }

    /**
     * 插入元素
     */
    void insert(int val) {
        root = insertHelper(root, val);
    }

    /**
     * 删除元素
     */
    void remove(int val) {
        root = removeHelper(root, val);
    }

    /**
     * 查找元素
     */
    bool search(int val) {
        return searchHelper(root, val);
    }

    /**
     * 中序遍历并输出
     */
    void inOrder() {
        inOrderHelper(root);
        cout << endl;
    }
};

/**
 * 主函数：测试 AVL 树
 */
int main() {
    AVLTree tree;
    int n;
    cin >> n;
    for(int i = 0; i < n; i++) {
        int val;
        cin >> val;
        tree.insert(val);
    }
    cout << "中序遍历(有序): ";
    tree.inOrder();
    return 0;
}