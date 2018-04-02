package DouBanParser;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Exchanger;



public class TagBookStore {
    private String tag;
    private static final int LIMIT=1000;
    private int totalCount=0;
    private  volatile    boolean isFirst=true;
    private Exchanger<CopyOnWriteArrayList<Book>> exchanger=new Exchanger<>();
    public TagBookStore(String tag) {
        this.tag=tag;
    }
    private CopyOnWriteArrayList<Book> bookStoreWithTag=new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Book> backupStore=new CopyOnWriteArrayList<>();
    public void addBook(Book book) throws Exception {
        if(isFirst) {
            try {

                DatabaseUtil.createTagBookStore(tag);
                isFirst=false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        bookStoreWithTag.add(book);
        synchronized (this) {
            System.out.println("===========================");
            System.out.println("hey============totalCount is "+totalCount);
            if (++totalCount >= LIMIT) {
                try{
                bookStoreWithTag = exchanger.exchange(backupStore);
                addToDatabase();

            }
            catch (InterruptedException ex) {
                    ex.printStackTrace();
                    throw new Exception(ex.getCause());

            }
        }
        }
    }
    public boolean removeBook(Book book) {

        return  bookStoreWithTag.remove(book);

    }

    public List<Book> getBookStoreWithTag() {
        return Collections.unmodifiableList(bookStoreWithTag);
    }
    // 等待实现
    private void addToDatabase() {
        System.out.println("================================");
        System.out.println("starting adding to database");
        System.out.println("=================================");


            try
            {  DatabaseUtil.updateTagBookStore(tag,backupStore);
                DatabaseUtil.updateMetaDes(backupStore);
                totalCount=0;
                backupStore.clear();}
            catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public int hashCode() {
       return tag.hashCode();
    }
   public String getTag() {
        return tag;
   }
   public int size() {
        return bookStoreWithTag.size();
   }
    @Override
    public String toString() {
        return tag;
    }
    /**
     *  下面这个方法只是为了测试使用
     */
    public static List<Book> forTestBook(){
       Book book= new Book.Builder("自由").setISBN("1234567890123").setLayoutBinding("fuck").setPageCount(0).setPrice("CNY 0元").setpublisher("FuckPub").setGrade(10).setFiveStar(100).setFourStar(0).setThreeStar(0).setTwoStar(0).setOneStar(0)
               .setCover(Paths.get("s28342615.jpg")).build();
        return Arrays.asList(new Book[]{book});


    }
}
