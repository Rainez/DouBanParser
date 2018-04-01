package Network;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.*;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.net.*;
import java.util.Scanner;
import org.htmlcleaner.*;
import static net.mindview.util.Print.print;
/* This is just a experient */
// In complete version.There do not exits username and password;
public class TestProperties {
    private static String JSESSIONLOGID="";
    private static String JSESSIONLOGINID="";
    private static String encoding="UTF-8";
    private static URL login_url;
    private static URL captcha_url;
    private static URL caslogin_url=null;
    private static URL finalStageToGetData=null;
    private static String Referer="https://cas.sysu.edu.cn/cas/login?service=https%3A%2F%2Fuems.sysu.edu.cn%2Felect%2FcasLogin";
    private static URL classPageURL=null;
    static {
        try{
            login_url=new URL("https://cas.sysu.edu.cn/cas/login?service=https%3A%2F%2Fuems.sysu.edu.cn%2Felect%2FcasLogin");
            captcha_url=new URL("https://cas.sysu.edu.cn/cas/captcha.jsp");
            InputStream inputStreamFuck=Files.newInputStream(Paths.get("/Users/mac/IdeaProjects/FileLearn/src/Network/JSESSIONID"));
            Properties properties=new Properties();
            properties.load(inputStreamFuck);
            String temp=null;
            temp=properties.getProperty("JSESSIONLOGID");
            if(temp!=null)
//                JSESSIONLOGINID=temp;
            temp=properties.getProperty("JSESSIONLOGiNID");
               JSESSIONLOGINID=temp;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    private static String UM_distinctid="16001df5656c9e-09a7739e55138c-173f6d56-100200-16001df56579e9";
    private static URL fromGetValidTicket=null;
    private static String username="liuwq23";
    private static String password="curiousDe4r";
    private static  String captcha="";
    private static Desktop desktop= Desktop.getDesktop();
    private static String execution;
    private static String _eventId="submit";
    private static String geolocation="";
    private static String submit="submit";
    private static String TGC="";
    public static  void main(String[] args) throws  Exception{

            FullLogin();
            int code=getResultPage();
            if(code==200)
                print("get Data Okay");
           saveJSESSIONID();

           }

    public static void FullLogin() throws Exception{
        Properties properties=new Properties();
        Path path= Paths.get("/Users/mac/IdeaProjects/FileLearn/src/Network/test.txt");
        try(InputStream inputStream= Files.newInputStream(path)){
            properties.load(inputStream);
        }
        URL url=new URL("https://cas.sysu.edu.cn/cas/login?service=https%3A%2F%2Fuems.sysu.edu.cn%2Felect%2FcasLogin");
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        connection.setInstanceFollowRedirects(false);
        for(Map.Entry<Object,Object> pair:properties.entrySet()){
            String name=pair.getKey().toString();
            String value=pair.getValue().toString();
            connection.setRequestProperty(name,value);
        }
        if(JSESSIONLOGID!=null &&JSESSIONLOGID.length()!=0)
            connection.setRequestProperty("Cookie",JSESSIONLOGID);
        String encoding=connection.getContentEncoding();
        PrintWriter writer=new PrintWriter(new FileWriter(new File("/Users/mac/Desktop/test1.html")),true);
        if(encoding==null)
            encoding="UTF-8";
        InputStream inputStream=connection.getInputStream();
        Scanner scanner=new Scanner(inputStream,encoding);
      /*  while(scanner.hasNextLine()){
            String line=scanner.nextLine();
            writer.write(line);
            writer.write("\n");
          }  */
        int c;
        ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
        while((c=inputStream.read())!=-1){
            arrayOutputStream.write(c);
        }
        byte[] fullBytes=arrayOutputStream.toByteArray();

        writer.flush();
        writer.write(new String(fullBytes,"UTF-8"));
        arrayOutputStream.close();
        writer.close();;
        print(getExecutionString().trim().length());
        connection.disconnect();
        getSessionIDAndSetCookies();
        System.out.println("Please enter the captcha");
//        Scanner scanner1=new Scanner(System.in);
//        int c;
        StringBuilder stringBuilder=new StringBuilder();

        while((c=System.in.read())!='\n'){
            stringBuilder.append((char)c);
        }
        captcha=stringBuilder.toString().trim();
        print("captcha is "+captcha);
        print("finished");
        //        print(JSESSIONLOGID);
        int postStageCode= postData(createProperConnection(true,login_url));
        if(postStageCode==302)
            print("First Stage okay");
        int postAnotherCode=getValidTicket();
        if(postAnotherCode==302)
            print("Second Stage okay");
        int casLoginCode=casLoginStageOne();
        if(casLoginCode==302)
            print("Cas login stage one finished");
        int lastStepBeforeFinalStageCode=casLoginStageTwo();
        if(casLoginCode==302)
            print("Okay,Just one step more!");
        int resultCode=getFinalData();
        if(resultCode==200)
            print("Now you should start get the data you want");
    }
    public static HttpURLConnection createProperConnection(boolean isPost,URL fromUrl) throws Exception{
        HttpURLConnection connection=(HttpURLConnection)fromUrl.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setDoOutput(isPost);
        Properties properties=new Properties();
        Path path= Paths.get("/Users/mac/IdeaProjects/FileLearn/src/Network/test.txt");
        try(InputStream inputStream= Files.newInputStream(path)){
            properties.load(inputStream);
        }
        for(Map.Entry<Object,Object> pair:properties.entrySet()){
            String name=pair.getKey().toString();
            String value=pair.getValue().toString();
            connection.setRequestProperty(name,value);
        }
        return connection;
    }
    public static int postData(HttpURLConnection connection) throws Exception{
        connection.setRequestProperty("Referer",Referer);
        connection.setDoOutput(true);
        connection.setRequestProperty("Cookie",JSESSIONLOGID);
        connection.setRequestMethod("POST");
        // be trust the connection has been properly construct.
        PrintWriter writer=new PrintWriter(connection.getOutputStream());
        writer.write("username="+username);
        writer.write('&');
        writer.write("password="+password);
        writer.write('&');
        writer.write("captcha="+captcha);
        writer.write('&');
        writer.write("execution=");
        writer.write(execution);
        writer.write('&');
        writer.write("_eventId=");
        writer.write(_eventId);
        writer.write('&');
        writer.write("geolocation=");
        writer.write(geolocation);
        writer.write('&');
//        writer.write("submit=");
//        String submitEncodingUsingForm=URLEncoder.encode(submit,"UTF-8");
//        writer.write(submitEncodingUsingForm);
        writer.flush();
        int responseCode=connection.getResponseCode();

        File file=new File("/Users/mac/Desktop/test2.html");
        if(!file.exists())
            file.createNewFile();
        PrintWriter writer1=new PrintWriter(new FileWriter(file));
        Scanner scanner=new Scanner(connection.getInputStream());
        while(scanner.hasNextLine()){
            String s=scanner.nextLine();
            writer.write(s);
            writer.write("\n");
        }
        writer.close();
         if(responseCode==302){
             // get TCG and print it
                String getCookie=connection.getHeaderField("Set-Cookie");
                TGC=getCookie.split(";")[0];
                String Location=connection.getHeaderField("Location");
                fromGetValidTicket=new URL(Location);
                }
        return responseCode;
        }
    public static int getValidTicket() throws Exception{
        if(fromGetValidTicket==null){
            print("I must tell you this is empty...");
            return 0;
        }
        HttpURLConnection connection=createProperConnection(false,fromGetValidTicket);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cookie",JSESSIONLOGID);
        connection.setRequestProperty("Cache-Control","max-age=0");
        connection.setRequestProperty("Host","uems.sysu.edu.cn");
        connection.setRequestProperty("Referer",Referer);
        String midStr=connection.getHeaderField("Location");
        caslogin_url=new URL(midStr);
        JSESSIONLOGINID=connection.getHeaderField("Set-Cookie").split(";")[0];
        return connection.getResponseCode();
    }
    public static int casLoginStageOne() throws Exception{
        if(caslogin_url==null){
            print("You know you want to login? emm......");
            return 0;
            }
        HttpURLConnection connection=createProperConnection(false,caslogin_url);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cookie",JSESSIONLOGINID);
        connection.setRequestProperty("Cache-Control","max-age=0");
        connection.setRequestProperty("Host","uems.sysu.edu.cn");
        connection.setRequestProperty("Referer",Referer);
        finalStageToGetData=new URL(connection.getHeaderField("Location"));
        return connection.getResponseCode();
    }
    public static int casLoginStageTwo() throws  Exception {
        if(finalStageToGetData==null){
            print("Fuck it! The last step failed!");
            }
        HttpURLConnection connection=createProperConnection(false,finalStageToGetData);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cookie",JSESSIONLOGINID);
        connection.setRequestProperty("Cache-Control","max-age=0");
        connection.setRequestProperty("Host","uems.sysu.edu.cn");
        connection.setRequestProperty("Referer",Referer);
        finalStageToGetData=new URL(connection.getHeaderField("Location"));
        return connection.getResponseCode();
    }
    public static int getFinalData() throws  Exception {
        HttpURLConnection connection=createProperConnection(false,finalStageToGetData);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cookie",JSESSIONLOGINID);
        connection.setRequestProperty("Cache-Control","max-age=0");
        connection.setRequestProperty("Host","uems.sysu.edu.cn");
        connection.setRequestProperty("Referer",Referer);
        BufferedInputStream bufferedInputStream=new BufferedInputStream(connection.getInputStream());
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        int c;
        while((c=bufferedInputStream.read())!=-1){
             byteArrayOutputStream.write(c);
        }
        String contents=new String(byteArrayOutputStream.toByteArray(),"UTF-8");
        try(FileWriter fileWriter=new FileWriter(new File("/Users/mac/Desktop/result.html"))) {
            fileWriter.write(contents);
        }
        return connection.getResponseCode();

    }
    public static String getExecutionString() throws Exception{
         //using XPATH to have a try;
        HtmlCleaner cleaner=new HtmlCleaner();
        TagNode node=cleaner.clean(new File("/Users/mac/Desktop/test1.html"));

        node.traverse(new TagNodeVisitor() {
            public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
                if (htmlNode instanceof TagNode) {
                    TagNode tag = (TagNode) htmlNode;
                    String tagName = tag.getName();
                    String src=null;
                    if ("input".equals(tagName)) {
                         if(tag.hasAttribute("name")&&"execution".equals(tag.getAttributeByName("name")))
                         { src = tag.getAttributeByName("value");}
                        if (src != null) {

//                          print(src);

                          execution=src;

                        }
                    }
                } else if (htmlNode instanceof CommentNode) {
                    CommentNode comment = ((CommentNode) htmlNode);

                }
                // tells visitor to continue traversing the DOM tree
                return true;
            }
        });
        return execution;
    }
    public static void saveJSESSIONID() throws Exception{
        Path path=Paths.get("/Users/mac/IdeaProjects/FileLearn/src/Network/JSESSIONID");
        Properties properties=new Properties();
        if(JSESSIONLOGID!=null && JSESSIONLOGID.length()!=0)
            properties.setProperty("JSESSIONLOGID",JSESSIONLOGID);
        if(JSESSIONLOGINID!=null && JSESSIONLOGINID.length()!=0)
            properties.setProperty("JSESSIONLOGINID",JSESSIONLOGID);
        if(finalStageToGetData!=null)
            properties.setProperty("finalStageToGetData",finalStageToGetData.toString());
        properties.store(new PrintWriter(path.toFile()),null);

    }
    public static void getSessionIDAndSetCookies() throws Exception{
        HttpURLConnection captchaConnection=(HttpURLConnection)captcha_url.openConnection();
        Properties properties=new Properties();

        Path path= Paths.get("/Users/mac/IdeaProjects/FileLearn/src/Network/test.txt");
        try(InputStream inputStream= Files.newInputStream(path)){
            properties.load(inputStream);
        }
        for(Map.Entry<Object,Object> pair:properties.entrySet()){
            String name=pair.getKey().toString();
            String value=pair.getValue().toString();
            captchaConnection.setRequestProperty(name,value);
        }
        if(JSESSIONLOGID.length()==0)
            captchaConnection.setRequestProperty("Cookie","UM_distinctid"+"="+UM_distinctid);
        else
            captchaConnection.setRequestProperty("Cookie",JSESSIONLOGID);
        path=Paths.get("/Users/mac/IdeaProjects/FileLearn/src/Network/spcialToken");
        properties.load((Files.newInputStream(path)));
        captchaConnection.setRequestProperty("Accept",properties.getProperty("Accept"));
        Path fuck=Paths.get("/Users/mac/IdeaProjects/FileLearn/src/Network","captcha.jpeg");
        if(!Files.exists(fuck))
            Files.createFile(fuck);
        BufferedOutputStream outputStream=new BufferedOutputStream(new FileOutputStream(fuck.toFile()));
        captchaConnection.connect();
        InputStream inputStreamForPic=captchaConnection.getInputStream();
        int c=0;

        while((c=inputStreamForPic.read())!=-1){
            outputStream.write(c);
        }
        String temp=captchaConnection.getHeaderField("Set-Cookie");
        if(temp!=null)
        { String[] pieces=temp.split(";");
        JSESSIONLOGID=pieces[0];}

        outputStream.flush();
        outputStream.flush();
       desktop.open(fuck.toFile());

        }
     public static int getResultPage() throws Exception{
         HtmlCleaner cleaner=new HtmlCleaner();
         File resultFile=new File("/Users/mac/Desktop/result.html");
         TagNode root=cleaner.clean(resultFile);
         String xpathExpression="//a//[text()=\"我的选课结果\"]";
         Object[] nodes=root.evaluateXPath(xpathExpression);
         TagNode o=null;
         if(nodes.length>0) {
              o = (TagNode) nodes[0];
//
         }
         String lastPath=o.getAttributeByName("href");
         String fatherPath=finalStageToGetData.toString().replaceFirst("type.*$","").trim();
         classPageURL=new URL(fatherPath+lastPath);
         HttpURLConnection connection=createProperConnection(false,classPageURL);
         connection.setRequestMethod("GET");
         connection.setRequestProperty("Cookie",JSESSIONLOGINID);
         connection.setRequestProperty("Cache-Control","max-age=0");
         connection.setRequestProperty("Host","uems.sysu.edu.cn");
         connection.setRequestProperty("Referer",finalStageToGetData.toString());
         BufferedInputStream bufferedInputStream=new BufferedInputStream(connection.getInputStream());
         ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
         int c=0;
         while((c=bufferedInputStream.read())!=-1){
              byteArrayOutputStream.write(c);
         }
         PrintWriter writer=new PrintWriter(new File("/Users/mac/Desktop/classes.html"));
         writer.write(new String(byteArrayOutputStream.toByteArray(),"UTF-8"));
         connection.disconnect();
         writer.close();
         bufferedInputStream.close();
         byteArrayOutputStream.close();
         int responseCode=connection.getResponseCode();
         return responseCode;
     }

}
