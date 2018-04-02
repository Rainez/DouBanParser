package DouBanParser;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Exchanger;
import java.sql.Date;
//import static net.mindview.util.Print.print;

/**
 * 这个类的任务是创建连接，执行插入
 * 以及创建新表
 * 另外的一个表格主要是对评分数据以及简介的描述，只存在这一个表格，可以提前创建，这里可以减少我们的代码量。
 */
public class DatabaseUtil {
   private  static ThreadLocal<Connection> connection=new ThreadLocal<>();
   private static Path mysql_safe_dir=Paths.get("/tmp/mysql_file");
   private static String mediapath="MetaDes";
//   private static ThreadLocal<Connection> metaDesconnection=new ThreadLocal<>();
   private static void initConnection() throws Exception{
       Properties properties=new Properties();
       Path path=Paths.get("./src/DouBanParser/doubanDatabase.prop");
       properties.load(Files.newInputStream(path));
       String jdbc=properties.getProperty("jdbc");
       String username=properties.getProperty("username");
       String password=properties.getProperty("password");
       String locateURL=properties.getProperty("path");
       locateURL=completeURL(locateURL,username,password);
       Class.forName("com.mysql.jdbc.Driver");
       Class.forName(jdbc);
       String desPath=properties.getProperty("despath");
       desPath=completeURL(desPath,username,password);
       Connection conn= DriverManager.getConnection(locateURL);
//           print(completeURL(locateURL,username,password));
       connection=new ThreadLocal<Connection>();
       connection.set(conn);
//       metaDesconnection.set(DriverManager.getConnection(desPath));

       }
    static {
       try{
       initConnection();}
       catch (Exception e) {
           e.printStackTrace();
       }
    }

    /**
     *

     * @param tagName
     */
    public static void createTagBookStore(String tagName) throws Exception{
       String sql="create  table "+"Douban."+tagName+"(\n" +
               "     isbn CHAR(13) NOT NULL UNIQUE,\n" +
               "     name VARCHAR(100) NULL DEFAULT NULL,\n" +
               "     publisher VARCHAR(45) NULL DEFAULT NULL,\n" +
               "     pubyear DATE NULL DEFAULT NULL,\n" +
               "     pagecount INT NULL DEFAULT 0,\n" +
               "     prices VARCHAR(20) NULL DEFAULT '0',\n" +
               "     bindinglayout VARCHAR(45) NULL DEFAULT NULL,PRIMARY KEY(isbn));";

        PreparedStatement preparedStatement= null;
        try {
            preparedStatement = connection.get().prepareStatement(sql);
            preparedStatement.executeUpdate();
            System.out.println(preparedStatement);
//            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            preparedStatement.close();
        }

        }
    public static void updateTagBookStore(String tagName,List<Book> bookList) throws Exception{

        String sql="insert ignore into "+tagName+" (`isbn`,`name`,`publisher`,`pubyear`,`pagecount`,`prices`,`bindinglayout`)  \n" +
                "values ( ?,?,?,?,?,?,?);";


        try (PreparedStatement preparedStatement = connection.get().prepareStatement(sql)) {
            for (Book book : bookList) {
                try {
                    preparedStatement.setString(1, book.getISBN());
                    preparedStatement.setString(2, book.getName());
                    preparedStatement.setString(3, book.getPublisher());
                    if(book.getPublishYear()!=null)
                        preparedStatement.setDate(4, new Date(parseDate(book.getPublishYear()).toEpochDay()));
                    else
                        preparedStatement.setDate(4,null);
                    preparedStatement.setInt(5,book.getPageCount());
                    preparedStatement.setString(6,book.getPrice());
                    preparedStatement.setString(7,book.getLayoutBinding());
                    System.out.println(preparedStatement);
                    preparedStatement.executeUpdate();

                } catch (SQLException e) {
                    preparedStatement.close();
                    e.printStackTrace();
                }



            }
        }

    }
    public static void updateMetaDes(List<Book> bookList) throws Exception{
         String sql="insert ignore into "+"MetaDes" + " values (?,?,?,?,?,?,?,?,load_file(?));";
         PreparedStatement preparedStatement=connection.get().prepareStatement(sql);
         for(Book book :bookList) {
              preparedStatement.setString(1,book.getISBN());
              preparedStatement.setDouble(2,book.getGrades());
              preparedStatement.setDouble(3,book.getFive_star());
              preparedStatement.setDouble(4,book.getFour_star());
              preparedStatement.setDouble(5,book.getThree_star());
              preparedStatement.setDouble(6,book.getTwo_star());
              preparedStatement.setDouble(7,book.getOne_star());
             preparedStatement.setString(8,book.getAbstracts());
             preparedStatement.setString(9,mysql_safe_dir.resolve(book.getCover_path()).toString());
             System.out.println(preparedStatement);
             try{
             preparedStatement.executeUpdate();
             }
             catch (Exception e) {
                 e.printStackTrace();
                 }

         }
         preparedStatement.close();
         }


    private static String completeURL(String locateURL,String username,String password) {
           String s=locateURL.replaceFirst("!u",username).replaceFirst("!p",password);
           System.out.println(s);
           return s;

    }
    public static  void main(String[] args) throws Exception{
        String tagName="文学";
        try {
       //    createTagBookStore("自由");
       //     updateTagBookStore("自由",TagBookStore.forTestBook());
            updateMetaDes(TagBookStore.forTestBook());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            connection.get().close();
        }
    }

    public static LocalDate parseDate(String date) {
         String[] pieces=date.split("-");
         int length=pieces.length;
         // 这里假定时间一定包含月
        int year=Integer.parseInt(pieces[0]);
        int month=Integer.parseInt(pieces[1]);
        int day=1;
        if(length==3)
            day=Integer.parseInt(pieces[2]);
        return LocalDate.of(year,month,day);
    }

}
