package Network;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Semaphore;
import static Network.ClassDecoder.ClassDefination;
import static Network.ClassDecoder.ClassDefination.getAllClasses;
import static net.mindview.util.Print.print;
//  暂时考虑支持的微辞只有today,tomorrow
//  默认不会列出已经上了的课程
//  除非指定all 选项
// @TODO 明白了从命令行parse 日期的要求，就提供更多的功能
// @TODO 在未来的版本之中应该加入相关防止冲突的方法
// @TODO  增加周课表和月课表？
// @Question 这个类存在某个本质上的问题就是目前只能支持单例的对象
// @BUG  目前不考虑其他多余的课表，只考虑本学期。这是不可能发生冲突的。

public class ClassTimeManager {
  private Map<ClassDefination,List<ClassSpcialDateTime>> classes=new HashMap<>();
  private static  Semaphore semaphore=new Semaphore(1);
  private ClassTimeManager() throws Exception{
      List<ClassDefination> classDefinationList=getAllClasses();

      for(ClassDefination defination:classDefinationList){
             classes.put(defination,defination.getTime());
             }
  }
   public static ClassTimeManager newInstance() throws Exception{
      if(semaphore.tryAcquire())
          return new ClassTimeManager();
      else
          return null;
   }
    public void release(){
      semaphore.release();
    }
    public String preetyOutputClass(ClassDefination classDefination,int startSection,int endSection,Place place){
       StringBuilder sb=new StringBuilder();
        sb.append("时间：");
        sb.append(startSection);
        sb.append("-");
        sb.append(endSection);
        sb.append("节");
        sb.append("(");
        sb.append(ClassSpcialDateTime.allpossibleStartTime[startSection-1]);
        sb.append("~");
        sb.append(ClassSpcialDateTime.allEndpossibleTime[endSection-1]);
        sb.append(")");
        sb.append("\n");
       sb.append("课程名称:");
       sb.append(classDefination.getClassName());
       sb.append("\n");
       sb.append("地点：");
       sb.append(place.getClassRoom());
       sb.append("\n");
       return sb.toString();
   }
    public List<String>  searchNotCompleteClass(LocalDateTime dateTime){
        LocalDate dateParsed=LocalDate.of(dateTime.getYear(),dateTime.getMonth(),dateTime.getDayOfMonth());
        List<String> classResults=new ArrayList<>();
        LocalTime  timeParsed=LocalTime.of(dateTime.getHour(),dateTime.getMinute(),dateTime.getSecond());
        DayOfWeek dayOfWeek=dateParsed.getDayOfWeek();
        for(Map.Entry<ClassDefination,List<ClassSpcialDateTime>> entry: classes.entrySet()){
            List<ClassSpcialDateTime> spcialDateTimes=entry.getValue();
            for(ClassSpcialDateTime classSpcialDateTime:spcialDateTimes){
                if(classSpcialDateTime.getDayOfWeek().equals(dayOfWeek)&&classSpcialDateTime.getEndDate().compareTo(dateParsed)>=0&&classSpcialDateTime.getStartDate().compareTo(dateParsed)<=0&&classSpcialDateTime.getStartTime().compareTo(timeParsed)>=0){
                    classResults.add(preetyOutputClass(entry.getKey(),classSpcialDateTime.getIndexOfAStartTime(),classSpcialDateTime.getIndexOfAEndTime(),classSpcialDateTime.getPlace()));

                }
            }
        }
        return classResults;
    }
    public List<String> searchAllClasses(LocalDateTime dateTime){
        LocalDate dateParsed=LocalDate.of(dateTime.getYear(),dateTime.getMonth(),dateTime.getDayOfMonth());
        List<String> classResults=new ArrayList<>();
        LocalTime  timeParsed=LocalTime.of(dateTime.getHour(),dateTime.getMinute(),dateTime.getSecond());
        DayOfWeek dayOfWeek=dateParsed.getDayOfWeek();
        for(Map.Entry<ClassDefination,List<ClassSpcialDateTime>> entry: classes.entrySet()){
            List<ClassSpcialDateTime> spcialDateTimes=entry.getValue();
            for(ClassSpcialDateTime classSpcialDateTime:spcialDateTimes){
                if(classSpcialDateTime.getDayOfWeek().equals(dayOfWeek)&&classSpcialDateTime.getEndDate().compareTo(dateParsed)>=0&&classSpcialDateTime.getStartDate().compareTo(dateParsed)<=0){
                    classResults.add(preetyOutputClass(entry.getKey(),classSpcialDateTime.getIndexOfAStartTime(),classSpcialDateTime.getIndexOfAEndTime(),classSpcialDateTime.getPlace()));

                }
            }
        }
        return classResults;
    }
    public List<String> ClassNotFnishedInToday(){
      LocalDateTime dateTime=LocalDateTime.now();
      return searchNotCompleteClass(dateTime);
//      turnOutResult(searchNotCompleteClass(dateTime));
    }
    public void allClassNotFinishedToday(){
       List<String> classes=ClassNotFnishedInToday();
        turnOutResult(classes);
    }

    private void turnOutResult(List<String> classes) {
        if(classes.size()==0)
        {
            print("Ooops...No class this day");
        return;}
        Collections.sort(classes);
        print("===========================");
        for(String s:classes){
            {
                print(s);
                print("\n");
            }

        }
        print("============================");
    }

    public void allClassesFinishedToday(){
      List<String> classes=searchAllClasses(LocalDateTime.now());
      turnOutResult(classes);

    }
    public void allClassesNotFinishedTomorrow(){
      LocalDateTime localDateTime=LocalDateTime.now();
      LocalDateTime tommorrow_mid=localDateTime.plusDays(1);
      LocalDateTime tommorrow=LocalDateTime.of(tommorrow_mid.getYear(),tommorrow_mid.getMonth(),tommorrow_mid.getDayOfMonth(),0,0,0);
      List<String> classes=searchAllClasses(tommorrow);
      turnOutResult(classes);
    }
    public void allClassesYesteryDay(){
       LocalDateTime localDateTime=LocalDateTime.now();
       LocalDateTime yesterday_mid=localDateTime.minusDays(1);
       LocalDateTime yesterday=LocalDateTime.of(yesterday_mid.getYear(),yesterday_mid.getMonth(),yesterday_mid.getDayOfMonth(),0,0,0);
       List<String> classes=searchAllClasses(yesterday);
       turnOutResult(classes);
    }
    public static  void main(String[] args) throws  Exception{
        ClassTimeManager classTimeManager=ClassTimeManager.newInstance();
        classTimeManager.turnOutResult(classTimeManager.ClassNotFnishedInToday());
       classTimeManager.allClassesNotFinishedTomorrow();
        classTimeManager.release();
    }
}
