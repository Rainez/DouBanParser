package Network;
import  java.io.*;
import java.nio.charset.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static net.mindview.util.Print.print;

public class TestUrlConnection {
    public static  void main(String[] args) throws Exception{
            URL url=new URL("https://www.baidu.com");
            URLConnection connection=url.openConnection();
            connection.connect();
        Map<String,List<String>> headers=connection.getHeaderFields();
        for(Map.Entry<String,List<String>> entry: headers.entrySet()){
             String key=entry.getKey();
             for(String value:entry.getValue())
                 print(key+": "+value);
        }
        System.out.println("--------------");
        System.out.println("getContentType "+connection.getContentType());
        System.out.println("---------------");
        String encoding=connection.getContentEncoding();
        if(encoding==null)
            encoding="UTF-8";
        try (Scanner scanner = new Scanner(connection.getInputStream(), encoding)) {
            for(int i=0;i<10;i++){
                if(scanner.hasNextLine())
                    print(scanner.nextLine());

            }
            if(scanner.hasNextLine())
                print("------");
        }

    }
}
