package DouBanParser;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * 这个类的功能不包括桦分大类，反正我不想加啦！
 */

public class TagParser  {
    private static String currentTagsPlace="https://book.douban.com/tag/?view=type&icn=index-sorttags-all";
    private static String baseUrl="https://book.douban.com";
    private static String startTagSearching="https://book.douban.com/tag/";
    private static TagBookStoreManager manager=TagBookStoreManager.getManager();
    private static CountDownLatch countDownLatch;
    private static AtomicInteger integer=new AtomicInteger(0);
    public static Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress("122.114.31.177",808));

    private static String start="start";
    private static String type="type=T";
    private static BlockingQueue<Pair> bookUris=new LinkedBlockingQueue<>();
    private static BlockingQueue<Pair> detailBookUri=new LinkedBlockingQueue<>();
    public static void changeBaseURL(String base) {
         baseUrl=base;
    }
    public static void changeTagPos(String pos) {
         currentTagsPlace=pos;
    }
    public static List<TagBookStore> findAllTagsWithURL() throws Exception{
        List<TagBookStore> tagBookStoreList=new ArrayList<>();
        URL url=new URL(currentTagsPlace);
//        URL url=new URL("file:///Users/mac/Desktop/DouBanStudy/%E8%B1%86%E7%93%A3%E5%9B%BE%E4%B9%A6%E6%A0%87%E7%AD%BE.html");
//        HttpURLConnection connection=(HttpURLConnection)url.openConnection();

        BufferedInputStream inputStream=new BufferedInputStream(url.openConnection().getInputStream());
        byte[] contents=new byte[1024];
        StringBuilder stringBuilder=new StringBuilder();
        int counts=0;
        while((counts=inputStream.read(contents))>0) {
           if(counts==1024)
               stringBuilder.append(new String(contents,"UTF-8"));
           else
               stringBuilder.append(new String(Arrays.copyOfRange(contents,0,counts),"UTF-8"));

           }
           String htmlCountents=stringBuilder.toString();
        Document document=Jsoup.parse(htmlCountents,baseUrl);
        Element body=document.body();
//        print(body);
        Elements urlTags=body.select("td>a[href^='/Tag']");
       for(Element element:urlTags) {
//            print(element.absUrl("href"));
           String text=element.text();
           TagBookStore tagBookStore=new TagBookStore(text);
           tagBookStoreList.add(tagBookStore);
           System.out.println("finding tag about "+tagBookStore.getTag());
           manager.addTagSet(tagBookStore);
       }
        countDownLatch=new CountDownLatch(2);
        return tagBookStoreList;

    }

    /**
     *  tag we need is 980本书 ，线程创建7个
     *
     * @throws Exception
     */
    public static void loadBookUris(List<TagBookStore> tagBookStoreList ) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
       for(TagBookStore store:tagBookStoreList ) {
//           TagBookStore store=tagBookStoreList.get(0);
           System.out.println(store.getTag());
            buildPagesBookURI(store.getTag());
           System.out.println("Task about "+store.getTag()+" start");
              executorService.execute(new MyTask());
                Thread.sleep(1000);
//            new MyTask().run();
           disposePageSecondStage();
            }

           executorService.shutdown();
        countDownLatch.countDown();
        }
    public static void disposeSinglePage() {

    }
    public static void disposeSinglePage(TagBookStore bookStore,String uri) {

        try {
            Book book=(SinglePageParser.setUPString(uri,new Book.Builder(bookStore.getTag())));
            bookStore.addBook(book);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static class MyTask implements Runnable {
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted() &&!bookUris.isEmpty()) {

               try{ Pair pair = bookUris.poll(1,TimeUnit.SECONDS);
                   if(pair==null)
                       return;
                   String uri=pair.uri;
                   String tag=pair.tag;
                   String[] pieces=tag.split("@");
                   disposePage(uri,pieces[0]);
             }
                catch (InterruptedException ex) {
                   ex.printStackTrace();
                }
            }


        }
    }
    private static void disposePage(String uri,String tag) {
        URL page= null;

        try {
//           print("current uri is "+uri);
            page = new URL(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(1);
        }
//        HttpURLConnection connection= null;
        URLConnection connection=null;
        try {
//            connection = (HttpURLConnection)page.openConnection();
            connection=page.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Document document= null;
        try {
            document = Jsoup.parse(connection.getInputStream(),"UTF-8",baseUrl);
        } catch (IOException e) {
            e.printStackTrace();
      /*      try {
                if (connection.getResponseCode() == 403) {
                    return;
                }
            ;
            }
            catch (IOException ex) {
                return;
            }  */
        }
       /* finally {
            connection.disconnect();
        } */
        Elements elements=document.select("h2>a");
        String baseURL="file:///Users/mac/Desktop/DouBanStudy/subject/";
//        print("The size of elements is "+elements.size());
        for(Element ele:elements) {
             String bookdetail=ele.attr("href");
               String[] pieces=bookdetail.split("/");
               bookdetail=baseURL+pieces[pieces.length-1];


             String bookName=ele.text().trim();

//             print("Bookdetail is "+bookName+" "+integer.addAndGet(1));
       //     print("First Stage is "+bookName);
             detailBookUri.add(new Pair(bookdetail,tag+"@"+bookName));
        }

    }
    public static void disposePageSecondStage() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        while(true) {
           final  Pair pair = detailBookUri.poll(10, TimeUnit.SECONDS);
//           print("Bookdetail is "+pair.tag+pair.uri);
            if(pair==null)
            {
                executorService.shutdown();
                countDownLatch.countDown();
                return;
            }
//       executorService.execute(new parseBookDetailTask(pair));
          new parseBookDetailTask(pair).run();
        }
        }
    private static class parseBookDetailTask implements  Runnable {
        private Pair pair;
        public parseBookDetailTask(Pair pair) {
            this.pair=pair;
        }
        @Override
        public void run() {
            String name=pair.tag;
            String[] pieces=name.split("@");
//            Book.Builder builder=new Book.Builder(pieces[1]);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Book book= null;
            try {
                book = SinglePageParser.completeParse(pair.uri);

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            String tag=URLDecoder.decode(pieces[0]);
            System.out.println("the tag is " +tag);
            TagBookStore store=manager.getTagBookStore(tag);
            System.out.println("The book is "+book);
            try {
               store.addBook(book);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private static void buildPagesBookURI(String tag) {
//        startTagSearching="file:///Users/mac/Desktop/DouBanStudy/";
        StringBuilder stringBuilder=new StringBuilder(startTagSearching);

        stringBuilder.append(URLEncoder.encode(tag));
        stringBuilder.append("");
        stringBuilder.append("?");
//        stringBuilder.append(URLEncoder.encode("?"));
        stringBuilder.append(start);
        stringBuilder.append("=");
        int[] range=Range.rangeClosedWithStep(0,980,20);
//        int[] range=Range.rangeClosedWithStep(0,0,20);
        int beforeplace=stringBuilder.length();
        stringBuilder.append(range[0]);
        int afterplace=stringBuilder.length()+1;
        stringBuilder.append("&");
        stringBuilder.append(type);
        bookUris.add(new Pair(stringBuilder.toString(),tag));
        System.out.println("Building urls is "+stringBuilder.toString());
        for(int i=1;i<range.length;i++) {
             stringBuilder.replace(beforeplace,afterplace,Integer.valueOf(range[i]).toString()+"&");
             afterplace=stringBuilder.indexOf("type");
             System.out.println("Building urls is "+stringBuilder.toString());
             bookUris.add(new Pair(stringBuilder.toString(),tag));

        }

        // 这里个人认为某一个种类的书最多只有980本,我认为是绝对不止的。
        // 使用二分法来找到最大没用的页数
        // 看来是不需要了


    }
    public static  void main(String[] args) throws Exception{
        Properties systemProperties = System.getProperties();
//        systemProperties.setProperty("http.proxyHost","122.114.31.177");
//        systemProperties.setProperty("http.proxyPort","808");
        findAllTagsWithURL();
        loadBookUris(findAllTagsWithURL());

        countDownLatch.await();
        System.out.println("Waiting for some time before image downloads");
           System.out.println("Threads sleep ten seconds before image downloaded");
           Thread.sleep(10000);
        manager.writaRemainingToDataBase();
        BatchImageDownloader.shutDown();
    }
}
