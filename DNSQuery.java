import java.net.*;
import java.util.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class DNSQuery {

private static Properties prop;

	public static byte[] lookup(String _domain) throws Exception {
  
		prop = new Properties();
		InputStream input;
		try {
			input = new FileInputStream("./hosts.ini");
			prop.load(input);
		} catch (Exception ex) {}
    
		InetAddress domain;
		String[] strSplit;
		byte[] addrBytes = new byte[4];
    String d = "";
    
    // Wenn kein . am Ende der Domain => aus Hosts Datei lesen, sonst eigenen DNSLookup 
    if(!_domain.endsWith(".")) {  
        try { 
            d = prop.get(_domain.trim()).toString(); 
        } catch(Exception e) { }
        domain = InetAddress.getByName(d);        
    } else {    
        domain = InetAddress.getByName(_domain); 
    } 
       
		strSplit = domain.getHostAddress().split("\\.");
      
		// zu einem ByteArray konvertieren
    for (int i = 0; i < 4; i++) {
			int temp = Integer.parseInt(strSplit[i]);
			addrBytes[i] = (byte)temp;
		} 
      
    System.out.println("[INFO] Domain " + _domain + " aufgelöst zu " + domain);	
		return addrBytes;
    
	}
  
}
