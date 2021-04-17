import java.util.Calendar;
import java.util.Date;

public class CacheItem implements Cache {

    private Date expiration = null;
    private Object key = null;
    private Object value = null;
    private Cache next = null;
    private Cache previous = null;

    public CacheItem(Object key, Object value, int minuteToExpire) {
        this.key = key;
        this.value = value;
        /* only if minute to live was especifed then calculated the expiration */
        if (minuteToExpire > 0) {
            this.expiration = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expiration);
            calendar.add(Calendar.MINUTE, minuteToExpire);
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
            if (expiration.before(new Date())) {
                return true;
            } else {
                return false;
            }
        }

    }

    @Override
    public boolean PullValue() {
        /* Implement code here for write to disk or DB */
        return false;
    }

    @Override
    public boolean PushValue() {
        /* Implement code here for read from disk or DB */
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
