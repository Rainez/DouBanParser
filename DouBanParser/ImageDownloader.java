package DouBanParser;

import com.sun.xml.internal.ws.util.CompletedFuture;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static DouBanParser.TagParser.proxy;


/**
 * 这是一个多线程的图片下载工具
 */
public class ImageDownloader {
  public  static class ImageWithName {
        public byte[] contents;
        public String fileName;
        public ImageWithName(String fileName,byte[] contents) {
             this.fileName=fileName;
             this.contents=contents;
        }
    }
   private static String pic_folder="/tmp/mysql_file";
//    private static  String pic_folder="/Users/mac/Desktop";
//    private static ExecutorService executorService;

    public void setPic_folder(String folder) {
         pic_folder=folder;

    }

  /*  private void start() {
        executorService= Executors.newFixedThreadPool(40);
    }
*/
  /*  public void shutDown() {
        executorService.shutdown();

    }
*/
    public static  ImageWithName startDownloadImg(String uri) {

        try {
            URL url=new URL(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        String[] pieces=uri.split("/");
           String name=pieces[pieces.length-1];
           System.out.println("last piece is "+pieces[pieces.length-1]);
       return    downloadImage(uri,name);


    }

    private static  ImageWithName downloadImage(String uri, String name) {
        URL url= null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        BufferedInputStream bufferedInputStream=null;
        try {
        bufferedInputStream = new BufferedInputStream(url.openConnection().getInputStream());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] contents = new byte[1024];
            int count = 0;
            while ((count = bufferedInputStream.read(contents)) > 0) {
    //            print("count is "+count);
                byteArrayOutputStream.write(contents, 0, count);
            }

            byte[] vlaues= byteArrayOutputStream.toByteArray();
            return new ImageWithName(name,vlaues);
        }
        catch (IOException ex) {
            return null;
        }
        finally {
            if(bufferedInputStream!=null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static  void writeImageToFile(ImageWithName contents,Book.Builder builder) {
        if (contents == null)
            return;
        try {
            System.out.println("file name is"+contents.fileName);
            Path path = Paths.get(pic_folder, contents.fileName);
            System.out.println("file full path is "+path.toString());
            if(Files.exists(path))
                path.toFile().delete();
         boolean iscreated=   path.toFile().createNewFile();
         if(!iscreated)
         {  System.out.println("any way it failed!");
             return;
         }


            writeSelf(contents.contents,path.toFile());
         builder.accept();
        } catch (IOException ex) {

        }
    }
    public static  void writeSelf(byte[] bytes,File file) {
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                outputStream.write(bytes);
                outputStream.flush();
                outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static  void main(String[] args){

    }
}
