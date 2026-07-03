import java.util.Scanner;

public class SwitchExample  {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
         System.out.println("今天星期几？");
        String s = scanner.nextLine();
       
        switch(s){
            case "1":
                System.out.println("跑步");
                break;
            case "2":
                System.out.println("游泳");
                break;
            case "3":
                System.out.println("打篮球");
                break;
            case "4":
                System.out.println("踢足球");
                break;
            default:
                System.out.println("休息");
                break;
        }
        scanner.close();
    } 
}
