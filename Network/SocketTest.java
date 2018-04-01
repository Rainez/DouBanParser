package Network;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static net.mindview.util.Print.print;

public class SocketTest {
    public static  void main(String[] args) throws IOException{
        try(Socket socket=new Socket("time-a.nist.gov",13); Scanner in=new Scanner(socket.getInputStream(),"UTF-8")){
            while(in.hasNextLine()){
                String line=in.nextLine();
                print(line);
            }
        }
    }
}
