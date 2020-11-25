import java.math.BigInteger;

public class MathExtended{
    // x = number, b = base
    // ex.  log(1024, 2) = 10
    public static int log(int x, int b)
    {
        return (int) (Math.log(x) / Math.log(b));
    }
    
    public static String hexToBinary(String hex) {
        BigInteger bigInt = new BigInteger(hex, 16);
        String binary = bigInt.toString(2);
        return binary;
    }
    
    public static String binaryToHex(String bin) {
        return String.format("%X", Long.parseLong(bin,2)).toLowerCase();
    }

    public static String hexToBinaryAddress(String hexAddress){
        BigInteger bigInt = new BigInteger(hexAddress, 16);
        String binary = bigInt.toString(2);
        return ("00000000000000000000000000000000" + binary).substring(binary.length()).toLowerCase();
    }
}