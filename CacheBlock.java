public class CacheBlock {
    
    //imports
    int POSITION;

    String tag;
    boolean isDirty;
    long leastRecentlyUsedCounter;

    public CacheBlock(int position)
    {
        POSITION = position;
        isDirty = true;
    }

    public CacheBlock(String t) {
        tag = t;
        isDirty = true;
    }



    // Getters and Setters

    public void SetTag(String t)
    {
        tag = t;
    }
    public String GetTag()
    {
        return tag;
    }
}