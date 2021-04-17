public interface Cache extends ListItem {
    /* This method will return the key of the cache element */
    public Object GetKey();

    /* This method will return the value of the cache element */
    public Object GetValue();

    /* every element should determine its own expiration */
    public boolean IsExpired();

    /* This method will read the value from the DB if is not in cache */
    public boolean PullValue();

    /* this method will write the value to the DB if its changue in the cache */
    public boolean PushValue();
}
