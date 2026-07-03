import java.util.Scanner;
import java.util.Random;

public class GuessNumberGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        int numberToGuess = random.nextInt(100) + 1;
        int numberOfTries = 0;
        int guess;
        boolean win = false;

        System.out.println("欢迎来到猜数字游戏！");
        System.out.println("我已经想好了一个1到100之间的数字。");
        System.out.println("请猜猜这个数字是什么：");

        while (!win) {
            System.out.print("请输入你的猜测：");
            guess = scanner.nextInt();
            numberOfTries++;

            if (guess == numberToGuess) {
                win = true;
                System.out.println("恭喜你猜对了！数字是 " + numberToGuess);
                System.out.println("你用了 " + numberOfTries + " 次尝试。");
            } else if (guess < numberToGuess) {
                System.out.println("太小了，再试试！");
            } else {
                System.out.println("太大了，再试试！");
            }
        }

        scanner.close();
    }
}