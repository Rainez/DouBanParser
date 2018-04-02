package DouBanParser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;



public class BatchImageDownloader {
  private static ExecutorService executorService=Executors.newFixedThreadPool(20);
//  private static ExecutorService singleControlDownloading=Executors.newCachedThreadPool();
//  private static BlockingQueue<String> downloadRequest=new LinkedBlockingQueue<>();
//  private static List<CompletableFuture<ImageDownloader.ImageWithName>> downloadingResult=new LinkedList<>();
  private static BlockingQueue<CompletableFuture<ImageDownloader.ImageWithName>> downloadQueen=new LinkedBlockingQueue<>();
  private static ConcurrentHashMap<CompletableFuture<ImageDownloader.ImageWithName>, Book.Builder> links=new ConcurrentHashMap<>();
  private static  final int LIMIT=100;
  private static  AtomicInteger counter=new AtomicInteger(0);
  private static volatile  boolean isOn=false;
  private static Instant startInstant=Instant.now();
  private static final long TIME_LIMIT=100;
  public static void shutDown() {
      executorService.shutdown();
  }
  public static CompletableFuture<ImageDownloader.ImageWithName> addRequests( final String url,Book.Builder builder) {
//       downloadRequest.add(url);
      Instant currentInstanct=Instant.now();
      Duration duration=Duration.between(startInstant,currentInstanct);
      if(!isOn) {
          System.out.println("Start downloading tasks");
          startWritingFile();
          isOn=false;
      }
       String[] pieces=url.split("/");
       String name=pieces[pieces.length-1];
       CompletableFuture<ImageDownloader.ImageWithName> completableFuture=CompletableFuture.supplyAsync(()->ImageDownloader.startDownloadImg(url));
       links.put(completableFuture,builder);
       downloadQueen.add(completableFuture);
       System.out.println(url+ " has been added");
        return completableFuture;
       }
 private static void startWritingFile() {
      while(true) {
          boolean notinterretupted = true;
          CompletableFuture<ImageDownloader.ImageWithName> completableFuture = null;
          try {
              completableFuture = downloadQueen.poll(2, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
              e.printStackTrace();
              notinterretupted = false;
          }

          System.out.println("completableFurure is "+completableFuture);
          if (completableFuture == null && notinterretupted) {
//              executorService.shutdown();
              return;
          }
          if (completableFuture != null) {
              notinterretupted = true;
              final CompletableFuture<ImageDownloader.ImageWithName> future=completableFuture;
              final Book.Builder builder=links.get(completableFuture);
              executorService.execute(()->{
                  ImageDownloader.ImageWithName imageWithName= null;
                  try {
                      imageWithName = future.get();
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  } catch (ExecutionException e) {
                      e.printStackTrace();
                  }
                  new WriteToFileTask(imageWithName,builder).run();
              });

              //              print("a file has been writed");
          }

      }
 }

 public static class WriteToFileTask implements  Runnable {
      private ImageDownloader.ImageWithName imageWithName;
      private Book.Builder bookBuilder;
     public WriteToFileTask(ImageDownloader.ImageWithName imageWithName,Book.Builder builder) {
         this.imageWithName=imageWithName;
         this.bookBuilder=builder;
     }

     @Override
     public void run() {
         ImageDownloader.writeImageToFile(imageWithName,bookBuilder);

     }
 }
    public static  void main(String[] args){

    }
}
