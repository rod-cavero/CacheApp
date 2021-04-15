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
    public Object getKey() {
        return this.key;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public boolean isExpired() {
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
    public Cache getNext() {
        return this.next;
    }

    @Override
    public void setNext(Cache next) {
        this.next = next;
    }

    @Override
    public Cache getPrevious() {
        return this.previous;
    }

    @Override
    public void setPrevious(Cache previous) {
        this.previous = previous;
    }

}
