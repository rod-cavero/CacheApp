public interface Cache {

    /* This method will return the key of the cache element */
    public Object getKey();

    /* This method will return the value of the cache element */
    public Object getValue();

    /* every element should determine its own expiration */
    public boolean isExpired();

    /* The next methodes will provide the interface necesary for handdling LRU */
    public Cache getNext();

    public void setNext(Cache next);

    public Cache getPrevious();

    public void setPrevious(Cache previous);

}
