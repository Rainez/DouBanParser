package DouBanParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;



public class Book {
    private String name;
    private List<String> authors=new ArrayList<>();
    private List<String> translators; // in most case it is null;

    private String abstracts;
    private double grades=0;
    private double five_star=0;

    public List<String> getTranslators() {
        return translators;
    }

    public double getGrades() {
        return grades;
    }

    public double getFive_star() {
        return five_star;
    }

    public double getFour_star() {
        return four_star;
    }

    public double getThree_star() {
        return three_star;
    }

    public double getTwo_star() {
        return two_star;
    }

    public double getOne_star() {
        return one_star;
    }

    private double four_star=0;
    private double three_star=0;
    private double two_star=0;
    private double one_star=0;

    public String getAbstracts() {
        return abstracts;
    }
    private Semaphore semaphore=new Semaphore(1);
    public void refuse() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void release() {
         semaphore.release();
    }
    public Path getCover_path() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        semaphore.release();
        return cover_path;
    }

    private Path  cover_path;

    public String getName() {
        return name;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getPrice() {
        return price;
    }

    public String getLayoutBinding() {
        return layoutBinding;
    }

    public String getISBN() {
        return ISBN;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Book)
        {
            Book another=(Book)obj;
            if(ISBN==another.ISBN&&publisher==another.publisher&&layoutBinding==another.layoutBinding) {
                return true;
            }
            else
                return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
       return ISBN.hashCode()+publisher.hashCode()+layoutBinding.hashCode();
    }

    private String  publisher;
    private String  publishYear;
    private int pageCount;
    private String price;
    private String layoutBinding;
    private String ISBN;
  private Book(){};
public static class Builder {
     private Book book=new Book();
     public Builder(String name) {
         book.name=name;

     }
     public Builder refuse() {
          book.refuse();
          return this;
     }
     public Builder accept() {
          book.release();
          return this;
     }
     public Builder setPubYear(String year) {
          book.publishYear=year;
          return this;
     }
     public Builder setAuthor(String... authors) {
         book.authors.addAll(Arrays.asList(authors));
         return this;
     }
     public Builder setpublisher(String pub) {
         book.publisher=pub;
         return this;
     }
     public Builder setPageCount(int page) {
         book. pageCount=page;
          return this;
     }
     public Builder setPrice(String pri) {
         book. price=pri;
          return this;
     }
     public Builder setLayoutBinding(String layoutBinding) {
          book.layoutBinding=layoutBinding;
          return this;
     }
     public Builder setISBN(String isbn) {
         book.ISBN=isbn;
         return this;
     }
     public Builder setCover(Path cover) {
         book.cover_path=cover;
         return this;
     }
     public Builder setAbstracts(String abstracts1) {
         book.abstracts=abstracts1;
         return this;
     }
     public Builder setGrade(double grade) {
         book.grades=grade;
         return this;
     }
     public Builder setFiveStar(double fiveStar) {
         book.five_star=fiveStar;
         return this;
     }
     public Builder setFourStar(double fourStar) {
          book.four_star=fourStar;
          return this;
     }
     public Builder setThreeStar(double threeStar) {
         book.three_star=threeStar;
         return this;
     }
     public Builder setTwoStar(double twoStar) {
          book.two_star=twoStar;
          return this;
     }

     public Builder setOneStar(double oneStar) {
          book.one_star=oneStar;
          return this;
     }
     public Builder setTranslators(List<String> translators) {
         book.translators=translators;
         return this;
     }
     public Builder forDouBanBuilder(String who,String arg) {
           switch (who) {
               case "出版社":
                   setpublisher(arg);
                   break;
               case "译者":
                  List<String> authors=new ArrayList<>();
                   String[] splits=arg.split("/");
                 if(splits==null)
                    authors.add(arg);
                 else
                     authors.addAll(Arrays.asList(splits));
                 setTranslators(authors);
                 break;
               case "出版年":
                   setPubYear(arg);
                   break;
               case "页数":
                   setPageCount(Integer.parseInt(arg));
                   break;
               case "定价":

                   setPrice(arg);
                   break;
               case "装帧":
                   setLayoutBinding(arg);
                   break;
               case "ISBN":
                   setISBN(arg);
                   break;
           }
           return this;
     }
     public Book build() {
         return book;
     }


}
    @Override
    public String toString() {
        return "   "+getISBN()+" "+getName()+" ";
    }
}
