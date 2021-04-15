import java.util.*;

public class CacheService {
    private static HashMap<Object, Object> cacheHash = new HashMap<Object, Object>();
    private static boolean useLRU = false;
    private static int maximunItems = 0;
    private static int garbageCacheCollectorTime = 7000;
    private static CacheItem head = null;
    private static CacheItem tail = null;

    public CacheService() {
    }

    public static void setMaximunitems(int maximunItems) {
        /*
         * if it is specify number maximun of item cache can handle then LRU is
         * activated
         */
        if (maximunItems > 0) {
            CacheService.maximunItems = maximunItems;
            CacheService.useLRU = true;
        }
    }

    public static void setGarbageCacheCollectorTime(int garbageCacheCollectorTime) {
        /* This is the time interval for the garbage cache collector */
        CacheService.garbageCacheCollectorTime = garbageCacheCollectorTime;
    }

    /* If LRU is active this will put the item into the list */
    private static void PutToList(CacheItem item) {
        if (CacheService.head != null) {
            CacheService.head.setNext(item);
            item.setPrevious(head);
        }
        CacheService.head = item;
        if (CacheService.tail == null) {
            CacheService.tail = item;
        }
    }

    /* If LRU is active this will remove the item from the list */
    private static void RemoveFromList(CacheItem item) {
        CacheItem next = (CacheItem) item.getNext();
        CacheItem previous = (CacheItem) item.getPrevious();
        if (next != null) {
            next.setPrevious(previous);
        }
        if (previous != null) {
            previous.setNext(next);
        }
        if (CacheService.head == item) {
            CacheService.head = previous;
        }
        if (CacheService.tail == item) {
            CacheService.tail = next;
        }
    }

    /* the item is eliminated from the cache */
    private static void RemoveFromCache(CacheItem item) {
        if (CacheService.useLRU) {
            RemoveFromList(item);
        }
        cacheHash.remove(item.getKey());
        item = null;
    }

    /* a new item is push to the cache */
    public static void PutCache(CacheItem item) {
        /*
         * if the LRU is active and the cache is full and is a new item then the last is
         * eliminated to insert the new one
         */
        if (CacheService.useLRU && CacheService.cacheHash.size() == maximunItems
                && CacheService.cacheHash.containsKey(item.getKey()) == false) {
            RemoveFromCache(CacheService.tail);
        }
        CacheService.cacheHash.put(item.getKey(), item);
        if (CacheService.useLRU) {
            PutToList(item);
        }
    }

    /* this will retraive the value from the cache */
    public static CacheItem GetCache(Object key) {
        CacheItem item = (CacheItem) CacheService.cacheHash.get(key);
        if (item == null) {
            /*
             * the item do not exists in the cache miss cache
             */
            return null;
        } else if (item.isExpired()) {
            /*
             * the item exist in the cache but is expired so its remove from the cache and
             * send a miss cache
             */
            RemoveFromCache(item);
            return null;
        } else {
            if (CacheService.useLRU && CacheService.head != item) {
                /* if lRU is active the item is put to head of the list */
                RemoveFromList(item);
                PutToList(item);
            }
            return item;
        }

    }

    /*
     * This static block will handle the garbage cache collector Create a new thread
     * wiht a low priority the thread will check the hash to determine which item is
     * expired to remove it
     */
    static {
        try {
            Thread garbageCacheCollector = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        while (true) {
                            Set<Object> keySet = CacheService.cacheHash.keySet();
                            Iterator<Object> keys = keySet.iterator();
                            while (keys.hasNext()) {
                                Object key = keys.next();
                                CacheItem item = (CacheItem) CacheService.cacheHash.get(key);
                                if (item.isExpired()) {
                                    RemoveFromCache(item);
                                }
                            }
                            Thread.sleep(CacheService.garbageCacheCollectorTime);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
            });
            garbageCacheCollector.setPriority(Thread.MIN_PRIORITY);
            garbageCacheCollector.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
