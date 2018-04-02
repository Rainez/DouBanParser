package DouBanParser;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class TagBookStoreManager {
    private static final int MAX_LIMIT=10000;
    private TagBookStoreManager(){};
    private int totalKindsCount=0;
    private static TagBookStoreManager manager=new TagBookStoreManager();

    public static TagBookStoreManager getManager() {
        return manager;
    }

    private ConcurrentHashMap<String,TagBookStore> tagBookStoreMap=new ConcurrentHashMap<>();


    public void addTagSet(TagBookStore tagBookStore){
         tagBookStoreMap.put(tagBookStore.getTag(),tagBookStore);
         synchronized (this) {
             totalKindsCount++;
         }
         }

    public int getTotalKindsCount() {
        return totalKindsCount;
    }
    public void removeTagSet(TagBookStore tagBookStore) {
         synchronized (this) {
             totalKindsCount--;
         }
         tagBookStoreMap.remove(tagBookStore.getTag());
    }
    public  TagBookStore getTagBookStore(String tagName) {
        return tagBookStoreMap.get(tagName);
    }
    public void writaRemainingToDataBase() {
       for(Map.Entry<String,TagBookStore> tagBookStoreEntry:tagBookStoreMap.entrySet()) {
            TagBookStore tagBookStore=tagBookStoreEntry.getValue();
            if(tagBookStore.size()!=0) {
                try {
                    DatabaseUtil.updateTagBookStore(tagBookStore.getTag(),tagBookStore.getBookStoreWithTag());
                    DatabaseUtil.updateMetaDes(tagBookStore.getBookStoreWithTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
       }

    }
}
