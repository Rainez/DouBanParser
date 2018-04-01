package Network;

import java.util.Scanner;

import static net.mindview.util.Print.print;

public class SystemInTest {
    public static  void main(String[] args){
        print("fuck");
        Scanner scanner=new Scanner(System.in);
        if(scanner.hasNextLine())
            print(scanner.nextLine());
        print("hello world");
    }
}
