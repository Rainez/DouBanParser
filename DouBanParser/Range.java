package DouBanParser;

import java.util.concurrent.BlockingQueue;


public class Range {
  public static int[] rangeClosedWithStep(int start,int stop,int step) {
       int amount=(stop-start)/step+1;
    int[] range=new int[amount];
    for(int i=0;i<amount;i++) {
         range[i]=i*step;
    }
    return range;
  }
    public static  void main(String[] args){
        int[] range=rangeClosedWithStep(0,980,20);
        for(int i=0;i<range.length;i++)
            System.out.println(range[i]);
    }
}
