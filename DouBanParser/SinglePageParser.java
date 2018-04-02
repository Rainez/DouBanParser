package DouBanParser;

import org.htmlcleaner.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static DouBanParser.TagParser.proxy;



public class SinglePageParser {
      private static String abstractPattern1="div[class='indent']>div>div[class='intro']";
      private static String abstractPattern2="span[class='all hidden']>div>div";
    public static  void main(String[] args) throws IOException{
        File file=new File("/Users/mac/Desktop/Android编程权威指南.html");
        Document doc=Jsoup.parse(file,"UTF-8","https://book.douban.com");
        Element bookName=doc.selectFirst("h1>span");
        String title=bookName.text().trim();
        Book.Builder builder=new Book.Builder(title);
        Element element=doc.getElementById("info");
       String s=element.toString();
      setUPString(s,builder);


    }
     public static Book completeParse(String uri ) throws IOException {
         URL url= null;
         try {
             url = new URL(uri);
         } catch (MalformedURLException e) {
             e.printStackTrace();
         }
         Document doc=Jsoup.parse(url.openConnection().getInputStream(),"UTF-8","https://book.douban.com");
         Element abstracts=doc.selectFirst(abstractPattern2);
         if(abstracts==null)
              abstracts=doc.selectFirst(abstractPattern1);
         System.out.print(abstracts.html());

         Element bookName=doc.selectFirst("h1>span");
         String title=bookName.text().trim();
         Book.Builder builder=new Book.Builder(title);
         builder.setAbstracts(abstracts.html());
         Element element=doc.getElementById("info");
         String s=element.toString();
         String imgUrl=doc.selectFirst("div[id='mainpic']>a>img").attr("src");
         String[] pieces=imgUrl.split("/");
         builder.setCover(Paths.get(pieces[pieces.length-1]));
         /**
          这里这个操作是阻塞的，十分影响性能，请改进
          */
         builder.refuse();
         BatchImageDownloader.addRequests(imgUrl,builder);
         Element grades=doc.selectFirst("strong");
         Elements starts=doc.select("span.rating_per");
         builder.setGrade(Double.parseDouble(grades.text().trim()));
         builder.setFiveStar(Double.parseDouble(starts.get(0).text().substring(0,starts.get(0).text().length()-1)));
         builder.setFourStar(Double.parseDouble(starts.get(1).text().substring(0,starts.get(1).text().length()-1)));
         builder.setThreeStar(Double.parseDouble(starts.get(2).text().substring(0,starts.get(2).text().length()-1)));

         builder.setTwoStar(Double.parseDouble(starts.get(3).text().substring(0,starts.get(3).text().length()-1)));

         builder.setOneStar(Double.parseDouble(starts.get(4).text().substring(0,starts.get(4).text().length()-1)));
         return   setUPString(s,builder);

     }
     public static Book setUPString(String s,Book.Builder builder) {

//        System.out.print(s);
        // 1. </span> 之后如果读到的是<br> 或者</br> 添加闭合

         List<String> authors=new ArrayList<>();
         String firstResult=s.replaceAll("<br>","</span>");
         String secondResult=firstResult.replaceAll("</span>","</span><span>");
//         System.out.print(secondResult);
         Document doc=Jsoup.parseBodyFragment(secondResult);

         // 这里做了一个假定，即只有span的class是pl,这个假定在新的版本中可能是错误的，请注意
         Elements spanElements=doc.getElementsByClass("pl");
         Element firstElement=spanElements.first();
         firstElement=firstElement.nextElementSibling();
         Elements authorElements=firstElement.getElementsByTag("a");
         for(Element element:authorElements)
         {

             authors.add(element.text());
             System.out.print(element.text());
         }
         String[] authorsArray=authors.toArray(new String[1]);
         builder.setAuthor(authorsArray);

         String[] arrays=null;
         for(int i=1;i<spanElements.size();i++) {
             Element element=spanElements.get(i);
             if (element.nextElementSibling() != null) {
                 if (element.nextElementSibling().tagName() == "span") {
//                 System.out.print(element.text());

                     Element sibing = element.nextElementSibling();
//               System.out.print(sibing.tagName());
//                     sibing.prepend("<span>");
                  String name=(element.text().replaceFirst(":","").trim());
                  String arg=sibing.text();
                  builder.forDouBanBuilder(name,arg);

//                     System.out.print(sibing.text());




                 }
             }

         }

        Book book= builder.build();
         System.out.print(book);
         return book;
  //    System.out.print(doc);
         }

}
