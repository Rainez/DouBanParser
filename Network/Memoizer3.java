package Network;

import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

interface  Computable<A,V>
{
    V compute(A arg) throws InterruptedException;
}
public class Memoizer3<A,V>  implements Computable<A,V>{
   private Computable<A,V> c;
   public Memoizer3(Computable com)
   {
       c=com;
   }
   private Map<A,Future<V>> cacheMap=new ConcurrentHashMap<A,Future<V>>();

   public V compute(A arg) throws InterruptedException {
       Future<V> future=cacheMap.get(arg);
       if(future==null) {
           FutureTask<V> task=new FutureTask<V>(new Callable<V>() {
               @Override
               public V call() throws Exception {
                  return c.compute(arg);
               }
           });
       task.run();
       }
    try {
           V v=future.get();
           return v;
    }
    catch (ExecutionException e) {
         Throwable ex=e.getCause();
            if( ex instanceof  RuntimeException)
                throw new RuntimeException(e);
            else if( ex instanceof  Error)
                throw new Error();
            else
                throw new UnknownError();

    }
   }


}
