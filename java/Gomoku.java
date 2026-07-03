import javax.swing.*; // 导入Swing GUI库，用于创建图形界面
import java.awt.*; // 导入AWT库，用于图形和事件处理
import java.awt.event.ActionEvent; // 导入ActionEvent类，用于按钮点击事件
import java.awt.event.ActionListener; // 导入ActionListener接口，用于监听按钮事件

public class Gomoku extends JFrame implements ActionListener { // 定义Gomoku类，继承JFrame，实现ActionListener接口

     private static final int BOARD_SIZE = 30; // 定义棋盘大小为30x30 
    private char[][] board; // 声明棋盘数组，存储棋子位置
    private char currentPlayer; // 声明当前玩家变量，'X'或'O'
    private JButton[][] buttons; // 声明按钮数组，对应棋盘格子
    private JLabel statusLabel; // 声明状态标签，显示当前玩家
    private boolean gameOver; // 声明游戏结束标志

    public Gomoku() { // 构造函数，初始化游戏
        board = new char[BOARD_SIZE][BOARD_SIZE]; // 初始化棋盘数组
        currentPlayer = 'X'; // 设置初始玩家为'X'（黑棋）
        gameOver = false; // 设置游戏未结束
        initializeBoard(); // 调用初始化棋盘方法
        initializeGUI(); // 调用初始化GUI方法
    }

    private void initializeBoard() { // 初始化棋盘，将所有位置设为空
        for (int i = 0; i < BOARD_SIZE; i++) { // 遍历行
            for (int j = 0; j < BOARD_SIZE; j++) { // 遍历列
                board[i][j] = '.'; // 将每个位置设为'.'
            }
        }
    }

    private void initializeGUI() { // 初始化图形界面
        setTitle("五子棋游戏"); // 设置窗口标题
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭窗口时退出程序
        setLayout(new BorderLayout()); // 设置布局为BorderLayout

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE)); // 创建棋盘面板，使用GridLayout
        buttons = new JButton[BOARD_SIZE][BOARD_SIZE]; // 初始化按钮数组
        for (int i = 0; i < BOARD_SIZE; i++) { // 遍历行
            for (int j = 0; j < BOARD_SIZE; j++) { // 遍历列
                buttons[i][j] = new JButton(); // 创建按钮
                buttons[i][j].setPreferredSize(new Dimension(40, 40)); // 设置按钮大小
                buttons[i][j].setBackground(Color.YELLOW); // 设置按钮背景色为黄色
                buttons[i][j].addActionListener(this); // 添加事件监听器
                boardPanel.add(buttons[i][j]); // 将按钮添加到面板
            }
        }

        statusLabel = new JLabel("当前玩家: " + currentPlayer + " (黑棋)", SwingConstants.CENTER); // 创建状态标签，居中显示
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16)); // 设置字体

        add(boardPanel, BorderLayout.CENTER); // 将棋盘面板添加到中央
        add(statusLabel, BorderLayout.SOUTH); // 将状态标签添加到南部

        pack(); // 调整窗口大小以适应组件
        setVisible(true); // 设置窗口可见
    }

    public boolean makeMove(int row, int col) { // 执行落子操作
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE || board[row][col] != '.') { // 检查位置是否有效
            return false; // 如果无效，返回false
        }
        board[row][col] = currentPlayer; // 在棋盘数组中放置棋子
        buttons[row][col].setText("●"); // 设置按钮文本为棋子符号
        if (currentPlayer == 'X') { // 如果是黑棋
            buttons[row][col].setBackground(Color.BLACK); // 设置背景为黑色
            buttons[row][col].setForeground(Color.WHITE); // 设置前景为白色
        } else { // 如果是白棋
            buttons[row][col].setBackground(Color.WHITE); // 设置背景为白色
            buttons[row][col].setForeground(Color.BLACK); // 设置前景为黑色
        }
        buttons[row][col].setEnabled(false); // 禁用按钮
        return true; // 返回true表示成功
    }

    public boolean checkWin(int row, int col) { // 检查是否获胜
        return checkDirection(row, col, 0, 1) || // 检查水平方向
               checkDirection(row, col, 1, 0) || // 检查垂直方向
               checkDirection(row, col, 1, 1) || // 检查对角线方向 \
               checkDirection(row, col, 1, -1);  // 检查对角线方向 /
    }

    private boolean checkDirection(int row, int col, int dRow, int dCol) { // 检查指定方向是否连成五子
        int count = 1; // 初始化计数为1（当前位置）
        // check positive direction
        int r = row + dRow; // 计算正方向行
        int c = col + dCol; // 计算正方向列
        while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == currentPlayer) { // 循环检查正方向
            count++; // 计数增加
            r += dRow; // 更新行
            c += dCol; // 更新列
        }
        // check negative direction
        r = row - dRow; // 计算负方向行
        c = col - dCol; // 计算负方向列
        while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == currentPlayer) { // 循环检查负方向
            count++; // 计数增加
            r -= dRow; // 更新行
            c -= dCol; // 更新列
        }
        return count >= 5; // 如果计数>=5，返回true
    }

    public void switchPlayer() { // 切换玩家
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X'; // 切换当前玩家
        //玩家名字
        String name = (currentPlayer == 'X') ? "灿大王" : "小屁健 "; // 根据玩家设置名字
        statusLabel.setText("当前玩家: " + name); // 更新状态标签
    }

    @Override
    public void actionPerformed(ActionEvent e) { // 处理按钮点击事件
        if (gameOver) return; // 如果游戏结束，直接返回

        JButton button = (JButton) e.getSource(); // 获取点击的按钮
        for (int i = 0; i < BOARD_SIZE; i++) { // 遍历行
            for (int j = 0; j < BOARD_SIZE; j++) { // 遍历列
                if (buttons[i][j] == button) { // 找到点击的按钮
                    if (makeMove(i, j)) { // 尝试落子
                        if (checkWin(i, j)) { // 检查是否获胜
                            //胜利者名字
                            String winnerName = (currentPlayer == 'X') ? "灿大王" : "小屁健"; // 获取胜利者名字
                            statusLabel.setText(winnerName + " 获胜！"); // 更新状态标签
                            JOptionPane.showMessageDialog(this, winnerName + " 获胜！", "游戏结束", JOptionPane.INFORMATION_MESSAGE); // 显示胜利对话框
                            gameOver = true; // 设置游戏结束
                            disableAllButtons(); // 禁用所有按钮
                        } else {
                            switchPlayer(); // 切换玩家
                        }
                    }
                    return; // 退出循环
                }
            }
        }
    }

    private void disableAllButtons() { // 禁用所有按钮
        for (int i = 0; i < BOARD_SIZE; i++) { // 遍历行
            for (int j = 0; j < BOARD_SIZE; j++) { // 遍历列
                buttons[i][j].setEnabled(false); // 禁用按钮
            }
        }
    }

    public static void main(String[] args) { // 主方法，程序入口
        SwingUtilities.invokeLater(() -> new Gomoku()); // 在事件调度线程中创建Gomoku实例
    }
}