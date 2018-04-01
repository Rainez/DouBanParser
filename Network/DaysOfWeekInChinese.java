package Network;

import java.time.DayOfWeek;

import static java.time.DayOfWeek.*;

// !如果给出非法字符，那么返回的数值默认就是星期一，请注意
public class DaysOfWeekInChinese {
    public static DayOfWeek ChineseWeekDayToUniversal(String weekDay){
           switch(weekDay){
               case "星期一":
                   return MONDAY;


               case "星期二":
                   return TUESDAY;


               case "星期三":
                   return WEDNESDAY;


               case "星期四":
                   return THURSDAY;


               case "星期五":
                   return FRIDAY;


               case "星期六":
                   return SATURDAY;


               case "星期七":
                   return SUNDAY;
           }
        return MONDAY;
        }
}
