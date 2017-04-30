import java.io.*;
import java.net.*;
import java.util.*;

public class DNSEntry implements Serializable {

	private long date;
	private String domain;
	private byte[] addrBytes;

	public DNSEntry(String _domain, byte[] ipAddress) { 
   
  		domain = _domain.trim().toLowerCase();
  		addrBytes = ipAddress;
  		date = new Date().getTime(); 
           
	}

	public String getDomain() {
  
		return domain;
    
	}

	public byte[] getAddress() {
  
		return addrBytes;
    
	}

	public void setAddress(byte[] address) {
  
		addrBytes = address;
		date = new Date().getTime();
    
	}

	public String getAddressString() {
  
		return String.format("%d.%d.%d.%d", addrBytes[0], addrBytes[1], addrBytes[2], addrBytes[3]);
    
	}

	public long getDate() {
  
		return date;
    
	}

	public String toString() {
  
		return domain + ":" + getAddressString();
    
	}
  
}
