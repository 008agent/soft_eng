package carevnalebed;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class Base64 {
    
public static String encode(String src) {
String tmp = null;
try {
BASE64Encoder benc = new BASE64Encoder();
tmp = benc.encode(src.getBytes());
} catch(Exception E) { System.err.println(E + E.toString()); }
return tmp;
}

public static String decode(String src) {
String tmp = null;
try {
BASE64Decoder bdec = new BASE64Decoder();
byte[] buffer = bdec.decodeBuffer(src);
tmp = new String(buffer);
} catch(Exception E) { System.err.println(E + E.toString()); }
return tmp;
}

}
