public class Address
{
    // imports
    String ADDRESS_HEX;
    Cache CACHE_CURRENT;

    // computed
    String ADDRESS_BITS;
    String TAG_BITS;
    String INDEX_BITS;
    String BLOCK_OFFSET_BITS;

    String TAG_HEX;
    int INDEX_INT;
    int BLOCK_OFFSET_INT;

    public Address(String address, Cache cache)
    {
        ADDRESS_HEX = address;
        CACHE_CURRENT = cache;
        ComputeAddressFieldBits();
        ComputeAddressFieldHex();
        ComputeAddressFieldInt();
    }

    public Address(String tag_hex, int index_int, Cache cache)
    {
        TAG_HEX = tag_hex;
        INDEX_INT = index_int;
        CACHE_CURRENT = cache;
        ComputeAddressHex();
    }

    private void ComputeAddressFieldBits()
    {
        ADDRESS_BITS = MathExtended.hexToBinaryAddress(ADDRESS_HEX);
        int index = 0;
        TAG_BITS = ADDRESS_BITS.substring(index, CACHE_CURRENT.NUM_TAG_BITS);
        index += CACHE_CURRENT.NUM_TAG_BITS;
        INDEX_BITS = ADDRESS_BITS.substring(index, index + CACHE_CURRENT.NUM_INDEX_BITS);
        index += CACHE_CURRENT.NUM_INDEX_BITS;
        BLOCK_OFFSET_BITS = ADDRESS_BITS.substring(index, index + CACHE_CURRENT.NUM_BLOCK_OFFSET_BITS);
    }

    private void ComputeAddressFieldHex(){
        TAG_HEX = MathExtended.binaryToHex(TAG_BITS);
    }

    private void ComputeAddressFieldInt(){
        INDEX_INT = Integer.parseInt(INDEX_BITS, 2);
        BLOCK_OFFSET_INT = Integer.parseInt(BLOCK_OFFSET_BITS, 2);
    }

    private void ComputeAddressHex(){
        String address = "";
        for(int i = 0; i < CACHE_CURRENT.NUM_BLOCK_OFFSET_BITS; i++)
        {
            address += '0';
        }
        String index_bits_size = "";
        
        for(int i = 0; i < CACHE_CURRENT.NUM_INDEX_BITS; i++)
        {
            index_bits_size += '0';
        }
        INDEX_BITS = Integer.toBinaryString(INDEX_INT);
        INDEX_BITS = (index_bits_size + INDEX_BITS).substring(INDEX_BITS.length()).toLowerCase();
        address = INDEX_BITS + address;
        TAG_BITS = MathExtended.hexToBinary(TAG_HEX);
        address = TAG_BITS + address;
        address = ("00000000000000000000000000000000" + address).substring(address.length()).toLowerCase();
        ADDRESS_BITS = address;
        address = MathExtended.binaryToHex(address);
        ADDRESS_HEX = address;
    }
}