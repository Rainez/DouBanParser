package Network;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.mindview.util.Print.print;

public class SimpleEchoServer {
    public static  void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(23345);
        final List<Socket> sockets=new ArrayList<>();
        ExecutorService service=Executors.newCachedThreadPool();
        while(true) {
             Socket incoming = serverSocket.accept();
           service.execute(()->{
             try{
               InputStream inputStream = incoming.getInputStream();
            OutputStream outputStream = incoming.getOutputStream();
            Scanner scanner = new Scanner(inputStream);
            PrintWriter writer = new PrintWriter((new OutputStreamWriter(outputStream)), true);
            writer.println("Hello welcome to connect to me");
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                writer.println(s);
                writer.flush();
                if (s.trim().equals("BYE"))
                    break;

            }
               incoming.close();
             }
            catch (Exception e){
                 System.exit(1);
            }
           });
        }
//            print("finished");
//            incoming.close();
//
    }
}
