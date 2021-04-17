import java.util.Calendar;
import java.util.Date;

public class CacheItem implements Cache {

    private int minutesToExpire = 0;
    private Date expiration = null;
    private Object key = null;
    private Object value = null;
    private Cache next = null;
    private Cache previous = null;

    public CacheItem(Object key, Object value, int minutesToExpire) {
        this.key = key;
        this.value = value;
        this.minutesToExpire = minutesToExpire;
        SetExpiration();
    }

    private void SetExpiration() {
        /* only if minute to live was especifed then calculated the expiration */
        if (this.minutesToExpire > 0) {
            this.expiration = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expiration);
            calendar.add(Calendar.MINUTE, this.minutesToExpire);
            expiration = calendar.getTime();
        }
    }

    @Override
    public Object GetKey() {
        return this.key;
    }

    @Override
    public Object GetValue() {
        return this.value;
    }

    @Override
    public boolean IsExpired() {
        if (expiration == null) {
            /* no expiration cache lives permanently */
            return false;
        } else {
            return expiration.before(new Date());
        }

    }

    /*
     * Methods PullValue and PushValue should be implemented to enable the option
     * that the cache read and write to disk or DB in case the cache wants to handle
     * this, if not, not further action needed
     */
    @Override
    public boolean PullValue() {
        /* Implement code here for read to disk or DB */
        /*
         * if operation is succed reset the expiration SetExpiration();
         */
        return false;
    }

    @Override
    public boolean PushValue() {
        /* Implement code here for write from disk or DB */
        return false;
    }

    @Override
    public Object GetNext() {
        return this.next;
    }

    @Override
    public void SetNext(Object next) {
        this.next = (Cache) next;
    }

    @Override
    public Object GetPrevious() {
        return this.previous;
    }

    @Override
    public void SetPrevious(Object previous) {
        this.previous = (Cache) previous;
    }
}
