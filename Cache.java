

public class Cache {
	
	// imports
	int BLOCKSIZE;
	int SIZE;
    int ASSOC;
	int REPLACEMENT_POLICY;
	int INCLUSION_PROPERTY;
    Cache CHILD;
    Cache PARENT;

	// computed
	int NUM_INDEX_BITS;
    int NUM_BLOCK_OFFSET_BITS;
    int NUM_TAG_BITS;
    int NUM_SETS;
    int NUM_BLOCKS;

    // variables
    CacheSet[] sets;
    int num_writes;
    int num_reads;
    int num_write_hits;
    int num_read_hits;
    int num_write_misses;
    int num_read_misses;
    int num_writebacks;
    int num_memory_writebacks_invalidation;
    long commandIncrementer;
    SimulatorLineAccessor SIMULATOR_LINE_ACCESSOR;
		
	public Cache(int blocksize, int size, int assoc, int replacementpolicy, int inclusionproperty, SimulatorLineAccessor simulatorLineAccessor)
	{
		BLOCKSIZE = blocksize;
		SIZE = size;
		ASSOC = assoc;
		REPLACEMENT_POLICY = replacementpolicy;
        INCLUSION_PROPERTY = inclusionproperty;
        SIMULATOR_LINE_ACCESSOR = simulatorLineAccessor;

        ComputeSetCount();
        ComputeBlockCount();
        ComputeAddressFieldWidths();

        InstantiateSets();
        commandIncrementer = 0;
	}

	// Reference M4-Cache.ppt Slide 12 for Address Fields Widths
	private void ComputeAddressFieldWidths(){
        NUM_INDEX_BITS = MathExtended.log(NUM_SETS, 2);
        NUM_BLOCK_OFFSET_BITS = MathExtended.log(BLOCKSIZE, 2);
        NUM_TAG_BITS = 32 - NUM_INDEX_BITS - NUM_BLOCK_OFFSET_BITS;
    }
    
    // Reference 3.1 Configurable Parameters for Set Count Formula
    private void ComputeSetCount(){
		NUM_SETS = (SIZE / (ASSOC * BLOCKSIZE));
    }
    
    // Reference 3.1 Configurable Parameters for Set Count Formula
    private void ComputeBlockCount(){
		NUM_BLOCKS = (SIZE / BLOCKSIZE);
	}

    private void InstantiateSets(){
        sets = new CacheSet[NUM_SETS];
        for(int i = 0; i < NUM_SETS; i++)
        {
            sets[i] = new CacheSet(ASSOC, REPLACEMENT_POLICY, SIMULATOR_LINE_ACCESSOR, this);
        }
    }

    // Write and Read
    public void ReadAddress(String addr)
    {
        Address address = new Address(addr, this);
        CacheBlock currentBlock = sets[address.INDEX_INT].FindBlock(address.TAG_HEX);

        if(currentBlock != null)
        {
            num_read_hits++;
        }
        else
        {
            num_read_misses++;
            
            // Section 3.4 Allocating a Block


            // Step 1:
            // If there is at least one invalid block in the set, then there is already space for the requested block X and no further action is required
            // Check for free block that will return null if all blocks are set
            currentBlock = sets[address.INDEX_INT].NextAvailableBlock();

            // On the other hand, if all blocks in the set are valid
            if(currentBlock == null)
            {
                //  then a victim block V must be singled out for eviction, according to the replacement policy
                if(REPLACEMENT_POLICY == 0)
                {
                    currentBlock = sets[address.INDEX_INT].FindLeastRecentlyUsedBlock();
                }
                if(REPLACEMENT_POLICY == 1)
                {
                    currentBlock = sets[address.INDEX_INT].FindPseudoLeastRecentlyUsedBlock();
                }
                if(REPLACEMENT_POLICY == 2)
                {
                    currentBlock = sets[address.INDEX_INT].FindOptimalBlock();
                }

                //  If this victim block V is dirty
                if(currentBlock.isDirty)
                {
                    num_writebacks++;
                    if(CHILD != null)
                    {
                        // shouldn't be writing the addr, but instead the currentblock address
                        Address writeAddress = new Address(currentBlock.tag, address.INDEX_INT, this);
                        CHILD.WriteAddress(writeAddress.ADDRESS_HEX);
                    }
                    if(PARENT != null && INCLUSION_PROPERTY == 1)
                    {
                        Address inclusiveAddress = new Address(currentBlock.tag, address.INDEX_INT, this);
                        PARENT.ProcessAddressEviction(inclusiveAddress.ADDRESS_HEX);
                    }
                }
            }

            currentBlock.tag = address.TAG_HEX;
            currentBlock.isDirty = false;

            // Step 2:
            if(CHILD != null)
            {
                CHILD.ReadAddress(addr);
            }

        }

        //update LRU
        if(REPLACEMENT_POLICY == 0)
        {
            currentBlock.leastRecentlyUsedCounter = commandIncrementer;
        }
        //update PLRU
        if(REPLACEMENT_POLICY == 1)
        {
            sets[address.INDEX_INT].AccessPseudoLeastRecentlyUsedBlock(currentBlock.POSITION);
        }

        num_reads++;
        commandIncrementer++;
    }

    public void WriteAddress(String addr)
    {
        Address address = new Address(addr, this);
        CacheBlock currentBlock = sets[address.INDEX_INT].FindBlock(address.TAG_HEX);
        
        if(currentBlock != null)
        {
            num_write_hits++; 
            currentBlock.isDirty = true;  
        }
        else
        {
            num_write_misses++;
            
            // Section 3.4 Allocating a Block


            // Step 1:
            // If there is at least one invalid block in the set, then there is already space for the requested block X and no further action is required
            // Check for free block that will return null if all blocks are set
            currentBlock = sets[address.INDEX_INT].NextAvailableBlock();

            // On the other hand, if all blocks in the set are valid
            if(currentBlock == null)
            {
                //  then a victim block V must be singled out for eviction, according to the replacement policy
                if(REPLACEMENT_POLICY == 0)
                {
                    currentBlock = sets[address.INDEX_INT].FindLeastRecentlyUsedBlock();
                }
                if(REPLACEMENT_POLICY == 1)
                {
                    currentBlock = sets[address.INDEX_INT].FindPseudoLeastRecentlyUsedBlock();
                }
                if(REPLACEMENT_POLICY == 2)
                {
                    currentBlock = sets[address.INDEX_INT].FindOptimalBlock();
                }

                //  If this victim block V is dirty
                if(currentBlock.isDirty)
                {
                    num_writebacks++;
                    if(CHILD != null)
                    {
                        Address writeAddress = new Address(currentBlock.tag, address.INDEX_INT, this);
                        CHILD.WriteAddress(writeAddress.ADDRESS_HEX);
                    }
                }
            }

            currentBlock.tag = address.TAG_HEX;
            currentBlock.isDirty = true;

            // Step 2:
            if(CHILD != null)
            {
                CHILD.ReadAddress(addr);
            }

        }

        //update LRU
        if(REPLACEMENT_POLICY == 0)
        {
            currentBlock.leastRecentlyUsedCounter = commandIncrementer;
        }
        //update PLRU
        if(REPLACEMENT_POLICY == 1)
        {
            sets[address.INDEX_INT].AccessPseudoLeastRecentlyUsedBlock(currentBlock.POSITION);
        }
        
        num_writes++;
        commandIncrementer++;
    }

    public double ComputeMissRate()
    {
        return ((double)(num_read_misses + num_write_misses)/(num_reads + num_writes));
    }

    public double ComputeMissRateL2()
    {
        return ((double)num_read_misses/num_reads);
    }

    public int ComputeTotalMemoryTraffic()
    {
        if(PARENT != null && INCLUSION_PROPERTY == 1)
        {
            return num_read_misses + num_write_misses + num_writebacks + PARENT.num_memory_writebacks_invalidation;
        }

        return num_read_misses + num_write_misses + num_writebacks;
    }

    public void ProcessAddressEviction(String addr)
    {
        Address address = new Address(addr, this);
        CacheBlock currentBlock = sets[address.INDEX_INT].FindBlock(address.TAG_HEX);
        if(currentBlock != null)
        {
            currentBlock.tag = null;
            if(currentBlock.isDirty)
            {
                num_memory_writebacks_invalidation++;
                currentBlock.isDirty = false;
            }
        }
    }

    // GETTERS AND SETTERS
    public Cache GetChild()
    {
        return CHILD;
    }
    public Cache GetParent()
    {
        return PARENT;
    }


    public void SetChild(Cache child)
    {
        CHILD = child;
    }
    public void SetParent(Cache parent)
    {
        PARENT = parent;
    }

}
