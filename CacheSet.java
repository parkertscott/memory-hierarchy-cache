import java.util.ArrayList; 

public class CacheSet {

    public CacheBlock[] blocks;
    public int NUM_BLOCKS;
    public int REPLACEMENT_POLICY;
    PLRUTreeNode parentNode;
    int PLRUTreeGenerationCounter;
    SimulatorLineAccessor SIMULATOR_LINE_ACCESSOR;
    Cache CACHE;

    public CacheSet(int numblocks, int replacementPolicy, SimulatorLineAccessor simulatorLineAccessor, Cache cache) {
        NUM_BLOCKS = numblocks;
        REPLACEMENT_POLICY = replacementPolicy;
        SIMULATOR_LINE_ACCESSOR = simulatorLineAccessor;
        CACHE = cache;
        
        InstantiateBlocks();
        

        if(REPLACEMENT_POLICY == 1)
        {
            PLRUTreeGenerationCounter = 0;
            InstantiatePLRUTree();
        }
    }

    private void InstantiateBlocks(){
        blocks = new CacheBlock[NUM_BLOCKS];
        for(int i = 0; i < NUM_BLOCKS; i++)
        {
            blocks[i] = new CacheBlock(i);
        }
    }
    
    private void InstantiatePLRUTree(){
        int log_num_blocks = MathExtended.log(NUM_BLOCKS, 2);
        parentNode = new PLRUTreeNode();
        InstantiatePLRUTreeRecursive(0,log_num_blocks, parentNode);
    }

    private void InstantiatePLRUTreeRecursive(int level, int log_num_blocks, PLRUTreeNode currentNode)
    {
        if(level == log_num_blocks - 1)
        {
            currentNode.lChild = new PLRUTreeNode(PLRUTreeGenerationCounter);
            PLRUTreeGenerationCounter++;
            currentNode.rChild = new PLRUTreeNode(PLRUTreeGenerationCounter);
            PLRUTreeGenerationCounter++;
        }
        else
        {
            currentNode.lChild = new PLRUTreeNode();
            currentNode.rChild = new PLRUTreeNode();
            level++;
            InstantiatePLRUTreeRecursive(level, log_num_blocks, currentNode.lChild);
            InstantiatePLRUTreeRecursive(level, log_num_blocks, currentNode.rChild);
        }
    }

    public CacheBlock FindBlock(String tag)
    {
        for(int i = 0; i < NUM_BLOCKS; i++)
        {
            if(blocks[i].tag != null && blocks[i].tag.equals(tag))
            {
                return blocks[i];
            }
        }
        return null;
    }

    public CacheBlock FindLeastRecentlyUsedBlock()
    {
        
        CacheBlock leastRecentlyUsedBlock = blocks[0];
        for(int i = 0; i < NUM_BLOCKS; i++)
        {
            if(blocks[i].leastRecentlyUsedCounter < leastRecentlyUsedBlock.leastRecentlyUsedCounter)
            {
                leastRecentlyUsedBlock = blocks[i];
            }
        }
        return leastRecentlyUsedBlock;
    }

    
    public CacheBlock FindPseudoLeastRecentlyUsedBlock()
    {
        return blocks[FindPseudoLeastRecentlyUsedBlockRecursive(parentNode)];
    }

    private int FindPseudoLeastRecentlyUsedBlockRecursive(PLRUTreeNode node)
    {
        if(node.POINTER != -1)
        {
            return node.POINTER;
        }
        // reversed node.isRight since PLRU uses the opposite to find
        return FindPseudoLeastRecentlyUsedBlockRecursive((node.isRight) ? node.lChild : node.rChild);
    }

    public void AccessPseudoLeastRecentlyUsedBlock(int position)
    {
        AccessPseudoLeastRecentlyUsedBlockRecursive(Integer.toBinaryString(position), parentNode);
    }

    private void AccessPseudoLeastRecentlyUsedBlockRecursive(String binary, PLRUTreeNode node)
    {
        if(binary.length() == 0)
        {
            return;
        }
        else
        {
            boolean isRight = binary.charAt(0) == '1';
            node.isRight = isRight;
            if(isRight)
            {
                AccessPseudoLeastRecentlyUsedBlockRecursive(binary.substring(1), node.rChild);
            }
            else
            {
                AccessPseudoLeastRecentlyUsedBlockRecursive(binary.substring(1), node.lChild);
                
            }
        }
    }

    public CacheBlock FindOptimalBlock(){
        long commandIncrementer = CACHE.commandIncrementer;
        String currentLine = SIMULATOR_LINE_ACCESSOR.LINES.get((int)commandIncrementer);
        currentLine = FormatAddress(currentLine.substring(2));
        Address currentAddress = new Address(currentLine, CACHE);
        commandIncrementer++;
        ArrayList<CacheBlock> blocksList = new ArrayList<CacheBlock>();
        
        for(CacheBlock block:blocks)
        {
            blocksList.add(block);
        }
        while(blocksList.size() > 1 && commandIncrementer < SIMULATOR_LINE_ACCESSOR.LINES.size())
        {
            String line = SIMULATOR_LINE_ACCESSOR.LINES.get((int)commandIncrementer);
            line = FormatAddress(line.substring(2));
            Address address = new Address(line, CACHE);
            if(address.INDEX_INT == currentAddress.INDEX_INT)
            {
                int pos = -1;
                for(int i = 0; i < blocksList.size(); i++)
                {
                    CacheBlock block = blocksList.get(i);
                    if(address.TAG_HEX.toLowerCase().equals(block.tag.toLowerCase()))
                    {
                        pos = i;
                    }
                }
                if(pos != -1)
                {
                    blocksList.remove(pos);
                }
            }
            
            commandIncrementer++;
        }

        return blocksList.get(0);
    }


    public CacheBlock NextAvailableBlock()
    {
        for(int i = 0; i < NUM_BLOCKS; i++)
        {
            if(blocks[i].tag == null)
            {
                return blocks[i];
            }
        }
        return null;
    }

    
	public static String FormatAddress(String address)
	{
		String formattedAddress = ("00000000" + address).substring(address.length());
		return formattedAddress;
	}
}