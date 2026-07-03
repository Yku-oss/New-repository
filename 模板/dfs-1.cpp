#include<iostream>
#include<algorithm>
#include<vector>
#include<string>
using namespace std;

vector<string> s;
bool vis[101][101];
int dx[] = {-1, 1, 0, 0};  // 上下左右
int dy[] = {0, 0, -1, 1};
int n, m;

void dfs(int x, int y) {
    // 1. 越界检查
    if (x < 0 || x >= n || y < 0 || y >= m) return;
    // 2. 不是细胞（'0'）或已访问
    if (s[x][y] == '0' || vis[x][y]) return;
    
    // 3. 标记为已访问
    vis[x][y] = true;
    
    // 4. 向四个方向递归
    for (int i = 0; i < 4; i++) {
        dfs(x + dx[i], y + dy[i]);
    }
}

void solve() {
    cin >> n >> m;
    s.resize(n);
    for (int i = 0; i < n; i++) {
        cin >> s[i];
    }
    
    // 初始化 vis 为 false
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            vis[i][j] = false;
        }
    }
    
    int ans = 0;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (s[i][j] != '0' && !vis[i][j]) {
                dfs(i, j);
                ans++;
            }
        }
    }
    cout << ans << "\n";
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(0);
    solve();
    return 0;
}