import java.util.*;

public class CacheService {
    private static HashMap<Object, Object> cacheHash = new HashMap<Object, Object>();
    private static boolean useLRU = false;
    private static int maximunItems = 0;
    private static int garbageCacheCollectorTime = 7000;
    private static Cache head = null;
    private static Cache tail = null;

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

    /* If LRU is active this will add the item into the list at the head */
    private static void AddToList(Cache item) {
        if (CacheService.head != null) {
            CacheService.head.SetNext(item);
            item.SetPrevious(head);
        }
        CacheService.head = item;
        if (CacheService.tail == null) {
            CacheService.tail = item;
        }
    }

    /* If LRU is active this will remove the item from the list */
    private static void RemoveFromList(Cache item) {
        Cache next = (Cache) item.GetNext();
        Cache previous = (Cache) item.GetPrevious();
        if (next != null) {
            next.SetPrevious(previous);
        }
        if (previous != null) {
            previous.SetNext(next);
        }
        if (CacheService.head == item) {
            CacheService.head = previous;
        }
        if (CacheService.tail == item) {
            CacheService.tail = next;
        }
    }

    /* the item is eliminated from the cache */
    private static void RemoveFromCache(Cache item) {
        if (CacheService.useLRU) {
            RemoveFromList(item);
        }
        cacheHash.remove(item.GetKey());
        item = null;
    }

    /* a new item is push to the cache */
    public static void PutCache(Cache item) {

        /* Check is the key is in the hash */
        if (CacheService.cacheHash.containsKey(item.GetKey())) {
            Cache oldItem = (Cache) CacheService.cacheHash.get(item.GetKey());
            /* if the value is the same exit and do nothing */
            if (oldItem.GetValue() == item.GetValue()) {
                return;
            }
        }
        /*
         * if the LRU is active and the cache is full then the last is eliminated to
         * insert the new one
         */
        else if (CacheService.useLRU && CacheService.cacheHash.size() == maximunItems) {
            RemoveFromCache(CacheService.tail);
        }
        CacheService.cacheHash.put(item.GetKey(), item);
        if (CacheService.useLRU) {
            AddToList(item);
        }
        /* write to disk the value */
        item.PushValue();
    }

    /*
     * this will retraive the value from the cache key is the identifier of the
     * cache readobject is the cache object in which the value will be read from
     * disk if is not in the cache
     */
    public static Cache GetCache(Object key, Cache readObject) {
        Cache item = (Cache) CacheService.cacheHash.get(key);
        if (item == null) {
            /*
             * the item do not exists in the cache, so use the readObject and read from disk
             */
            if (readObject != null && readObject.PullValue()) {
                /* if read was succed add to the cache and return it */
                PutCache(readObject);
                return readObject;
            } else {
                /* cant read the value from disk return a miss cache */
                return null;
            }
        } else if (item.IsExpired()) {
            /*
             * the item exist in the cache but is expired, call the pullvalue method to
             * refresh data
             */
            if (item.PullValue()) {
                return item;
            } else {
                /*
                 * if couldnt refresh data remove from the cache and send a miss cache
                 */
                RemoveFromCache(item);
                return null;
            }
        } else {
            if (CacheService.useLRU && CacheService.head != item) {
                /* if lRU is active the item is put to the head of the list */
                RemoveFromList(item);
                AddToList(item);
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
                                Cache item = (Cache) CacheService.cacheHash.get(key);
                                if (item.IsExpired()) {
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
