package carevnalebed;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class Base64 {
    
    public static void main(String args[]) 
    {
        String test = "hello:world";
        String outEnc = "";
        String outDec = "";
        
        outEnc = encode(test);
        outDec = decode(outEnc);
        
        System.out.println(String.format("[Base64::encode()] %s(%d) -> %s(%d)", test, test.length(), outEnc, outEnc.length()));
        System.out.println(String.format("[Base64::decode()] %s(%d) -> %s(%d)", outEnc, outEnc.length(), outDec, outDec.length()));
        if(test.hashCode() == outDec.hashCode()) {
            System.out.println("Strings matches, exit 0");
            System.exit(0);
        } else {
            System.out.println("Strings differ, check Base64.java, exit 1");
            System.exit(1);
        }
    }
    
    public static String encode(String src) 
    {
        String tmp = null;
        try {
            BASE64Encoder benc = new BASE64Encoder();
            tmp = benc.encode(src.getBytes());
        } catch(Exception E) { 
            System.err.println(E + E.toString()); 
        }
        return tmp;
    }

    public static String decode(String src) 
    {
        String tmp = null;
        try {
            BASE64Decoder bdec = new BASE64Decoder();
            byte[] buffer = bdec.decodeBuffer(src);
            tmp = new String(buffer);
        } catch(Exception E) { 
            System.err.println(E + E.toString()); 
        }
        return tmp;
    }

}
