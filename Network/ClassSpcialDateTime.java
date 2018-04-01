package Network;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.Scanner;

import static java.time.DayOfWeek.*;
import static net.mindview.util.Print.print;

// 采用的时间是24小时的机制
// 这个程序的缺陷在于一旦学校的课程时间更改了，就要重写这个类
// 同上，这一版本的程序不提供任何修改功能！
//  关于WEEK数的更新，必须要仔细考虑，但不是现在的问题
// @TODO 请在后续的版本中添加持久性储存的功能
// @Required 当前版本保证在运行的时候不会出现错误
// 在桌面文件中保存时间 lastaccesstimestamp.txt
public class ClassSpcialDateTime {
    private static int week=2; // 程序开发的时候是第二周
    private static Path  lastaccessTimeStampPath= Paths.get("/Users/mac/Desktop/lastAccessstamp.txt");
    static {
        if(!Files.exists(lastaccessTimeStampPath))
          try
          {Files.createFile(lastaccessTimeStampPath);}
          catch (Exception e){
            throw new RuntimeException(e);
          }
    }
    static DateTimeFormatter formatter= DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS");
    private static LocalDateTime lastaccessTime=LocalDateTime.now();
    static {
        try {
            Scanner scanner = new Scanner(lastaccessTimeStampPath.toFile());
            if(scanner.hasNextLine())
                week=scanner.nextInt();
            scanner.nextLine();

            String s=scanner.nextLine().trim();


//            if (scanner.hasNextLine())
                lastaccessTime = LocalDateTime.parse(s, formatter);
            print(lastaccessTime);
        }
        catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }
    }
    public int week(){
        return week;
    }
    private static LocalDateTime currentAccessTime=LocalDateTime.now();
    private static LocalTime startTimeOneDay=LocalTime.of(8,0,0);
    private static LocalTime endTimeOneDay=LocalTime.of(21,35,0);

    private static int period=45;
    public static LocalTime[] allpossibleStartTime=new LocalTime[11];
    public static LocalTime[] allEndpossibleTime=new LocalTime[11];
    static {
        allpossibleStartTime[0]=startTimeOneDay;
        allpossibleStartTime[1]=startTimeOneDay.plusMinutes(55);
        allpossibleStartTime[2]=LocalTime.of(10,0,0);
        allpossibleStartTime[3]=allpossibleStartTime[2].plusMinutes(55);
        allpossibleStartTime[4]=LocalTime.of(14,20,0);
        allpossibleStartTime[5]=allpossibleStartTime[4].plusMinutes(55);
        allpossibleStartTime[6]=LocalTime.of(16,20,0);
        allpossibleStartTime[7]=allpossibleStartTime[6].plusMinutes(55);
        allpossibleStartTime[8]=LocalTime.of(19,0,0);
        allpossibleStartTime[9]=allpossibleStartTime[8].plusMinutes(55);
        allpossibleStartTime[10]=allpossibleStartTime[9].plusMinutes(55);
    }
    static {
        for(int i=0;i<allpossibleStartTime.length;i++)
            allEndpossibleTime[i]=allpossibleStartTime[i].plusMinutes(period);
    }
    static {
        Duration duration=Duration.between(lastaccessTime,currentAccessTime);
        int weeks=(int)duration.toDays()/7;
        week+=weeks;
    }
    static {
        try
        {  saveTimeStamp();}
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public ClassSpcialDateTime(LocalTime startTime, LocalTime endTime, int startWeek, int endWeek, DayOfWeek dayOWeek,Place place) {
        this.startTime = startTime;
        this.endTime = endTime;
        setUpStartAndEndDate(startWeek,endWeek,dayOWeek);
        this.place=place;
        sectionCount=getSectionCounts();
        startSection=getIndexOfAStartTime();
        endSection=getIndexOfAEndTime();
    }
    private int startWeek;
    private void setUpStartAndEndDate(int startWeek,int endWeek,DayOfWeek dayOWeek){
        this.startWeek=startWeek;
        this.endWeek=endWeek;
        this.dayOfWeek=dayOWeek;
        int offsetWeek=0;

        offsetWeek=week-startWeek;
        LocalDate upToNow=LocalDate.now();
        startDate=upToNow.minusDays(offsetWeek*7);
        int offset=dayOWeek.ordinal()-startDate.getDayOfWeek().ordinal();
        startDate=startDate.plusDays(offset);
        offsetWeek=endWeek-week;
        endDate=upToNow.plusDays(offset*7);
        offset=dayOWeek.ordinal()-endDate.getDayOfWeek().ordinal();

        endDate=endDate.plusDays(offset);
    }
    public ClassSpcialDateTime(int startSection,int endSection,int startWeek,int endWeek,DayOfWeek dayOfWeek,Place place){
        startTime=allpossibleStartTime[startSection-1];
        endTime=allEndpossibleTime[endSection-1];
        sectionCount=endSection-startSection;
        startSection=startSection;
        endSection=endSection;
        setUpStartAndEndDate(startWeek,endWeek,dayOfWeek);
        this.place=place;
    }

    public ClassSpcialDateTime(LocalTime startTime,int classCount,int startWeek,int endWeek,DayOfWeek dayOfWeek,Place place){
         this.startTime=startTime;
         this.sectionCount=classCount;
         int index=getIndexOfAStartTime()-1;
         startSection=index+1;
         int endindex=index+sectionCount-1;


         endSection=endWeek+1;

         endTime=allEndpossibleTime[endindex];

         this.place=place;

         setUpStartAndEndDate(startWeek,endWeek,dayOfWeek);
    }

    private LocalDate startDate;

    private LocalDate endDate;


    public int getStartSection() {
        return startSection;
    }


    public int getEndSection() {
        return endSection;
    }


    private int startSection;

    private int endSection;

    private int endWeek;

    private LocalTime startTime;

   public LocalTime getStartTime(){
        return startTime;
   }

    public static int getWeek() {
        return week;
    }

    public static int getPeriod() {
        return period;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }





    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    private LocalTime endTime;

    private Place place;


    public Place getPlace(){
        return place;
    }

    private DayOfWeek  dayOfWeek; // from 1-7


    private int sectionCount=0;
    // 不能传递非法时间！！！

    private int getIndexOfAStartTime(LocalTime time){
          for(int i=0;i<allpossibleStartTime.length;i++){
               if(time.equals(allpossibleStartTime[i]))
                   return i;
          }
          return -1;
    }
    private static void saveTimeStamp() throws  Exception{
        PrintWriter writer=new PrintWriter(Files.newOutputStream(lastaccessTimeStampPath));
        writer.write(Integer.valueOf(week).toString());
        writer.write("\n");
        writer.write(currentAccessTime.toString());
        writer.flush();
        writer.close();
    }

    public int getIndexOfAStartTime(){
        return getIndexOfAStartTime(startTime)+1;
    }

    public int getIndexOfAEndTime(){
        return getindexOfAEndTime(endTime)+1;
    }

    public int getSectionCounts(){
        return getClassCount(startTime,endTime);
    }

    private int getindexOfAEndTime(LocalTime time){
         for(int i=0;i<allEndpossibleTime.length;i++){
              if(time.equals(allEndpossibleTime[i]))
                  return i;
         }
         return -1;
    }

    public int getClassCount(LocalTime startTime,LocalTime endTime){
         int startIndex=getIndexOfAStartTime(startTime);
         int endIndex=getindexOfAEndTime(endTime);
         return endIndex-startIndex+1;
    }

    public static  void main(String[] args){
       print(week);
    }

}
