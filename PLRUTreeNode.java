public class PLRUTreeNode{
    PLRUTreeNode lChild;
    PLRUTreeNode rChild;
    boolean isRight;
    int POINTER;

    public PLRUTreeNode(){
        isRight = false;
        POINTER = -1;
    }
    public PLRUTreeNode(int pointer)
    {
        isRight = false;
        POINTER = pointer;
    }

}