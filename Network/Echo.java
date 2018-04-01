package Network;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.mindview.util.Print.print;

public class Echo {
    public static  void main(String[] args) throws Exception{
        Socket socket=new Socket("localhost",23345);
        print("build successful");
        ExecutorService service= Executors.newCachedThreadPool();

        InputStream inputStream=socket.getInputStream();
        OutputStream outputStream=socket.getOutputStream();
        String reading;
        BufferedInputStream bufferedInputStream=new BufferedInputStream(System.in);
        Scanner scanner=new Scanner(bufferedInputStream);
        PrintWriter writer=new PrintWriter(new OutputStreamWriter(outputStream));
        String writing;
        BufferedInputStream bufferedInputStream1=new BufferedInputStream(inputStream);
      BufferedOutputStream pipedOutputStream=new BufferedOutputStream(System.out);

        service.execute(()->{
            while(true){
                try {
                    while (bufferedInputStream1.available() > 0) {
                        pipedOutputStream.write(bufferedInputStream1.read());
                        pipedOutputStream.flush();
                    }
                }
                catch (Exception e){
                    System.exit(1);
                }

            }
        });
        while(scanner.hasNextLine()){
            reading=scanner.nextLine();
             print(reading);
            if(reading.trim().equals("BYE"))
                break;
        }
        print("finished");
        if(!socket.isClosed())
        {   socket.close();
            service.shutdownNow();
            inputStream.close();
            outputStream.close();
        }
    }
}
