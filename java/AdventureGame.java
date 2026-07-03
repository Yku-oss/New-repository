import java.util.Scanner;

public class AdventureGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("欢迎来到简单冒险游戏！");
        System.out.println("你是一个探险家，来到一个神秘的森林。");
        System.out.println("你看到两条路：一条通往左边的小径，一条通往右边的河流。");
        System.out.print("请选择（输入数字）：1. 左边小径  2. 右边河流：");

        int choice1 = scanner.nextInt();

        if (choice1 == 1) {
            System.out.println("你选择了左边小径。");
            System.out.println("你遇到了一只熊！");
            System.out.print("请选择：1. 跑开  2. 爬树：");
            int choice2 = scanner.nextInt();
            if (choice2 == 1) {
                System.out.println("你跑开了，但熊追上了你。游戏结束！");
            } else {
                System.out.println("你爬上了树，熊走了。你找到了宝藏！胜利！");
            }
        } else if (choice1 == 2) {
            System.out.println("你选择了右边河流。");
            System.out.println("你看到一条船。");
            System.out.print("请选择：1. 上船  2. 游泳：");
            int choice2 = scanner.nextInt();
            if (choice2 == 1) {
                System.out.println("船上有宝藏！你胜利了！");
            } else {
                System.out.println("你游泳时遇到了鳄鱼。游戏结束！");
            }
        } else {
            System.out.println("无效选择。游戏结束！");
        }

        scanner.close();
    }
}