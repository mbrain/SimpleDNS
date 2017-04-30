import java.util.*;

public class DNSResponse {

  	private byte[] 	requestBytes;
  
  	public DNSResponse(byte[] request) {
  		  setRequest(request);
  	}
  
  	public void setRequest(byte[] request) {
  		  requestBytes = request;
  	}
  
  	public byte[] getRequest() {
  		  return requestBytes;
  	}
  
  	public byte[] getResponse(byte[] requestBytes) {
  		  return copyBytes(generateHeader(), generateQuestion(), generateAnswer(requestBytes));
  	}
  
  	public String getDomain() {
    
    		int intPosition = 12;
    		int length = (int)requestBytes[intPosition];
    		String strDomain = "";
    		intPosition++;
    
    		while (length != 0) {
        
      			for (int i = intPosition; i < (intPosition + length); i++) {
      				  strDomain += (char)requestBytes[i];
      			}
      
      			intPosition += length;
      			length = (int)requestBytes[intPosition];
      
      			if (length != 0) {
      				  strDomain += '.';
            }
      			intPosition++;
          
    		}
    
    		return strDomain;
      
  	}
  
  	private byte[] generateHeader() {
    
    		// Header ist bei DNS Paketen immer 12 Bytes (96 Bits).
    		byte[] headerBytes = new byte[12];
    
    		// ID
    		for (int i = 0; i < 2; i++) {
    			 headerBytes[i] = requestBytes[i];
    		}
    
    		// Options
    		headerBytes[2] = (byte)129;	// 10000001b
    		headerBytes[3] = (byte)128;	// 10000000b
    
    		// Questions
    		headerBytes[4] = 0; // 00000000b
    		headerBytes[5] = 1; // 00000001b
    
    		// Answers
    		headerBytes[6] = 0; // 00000000b
    		headerBytes[7] = 1; // 00000001b
    		
    		// NS
    		headerBytes[8] = 0; // 00000000b
    		headerBytes[9] = 0; // 00000000b
    
    		// Arcount
    		headerBytes[10] = 0; // 00000000b
    		headerBytes[11] = 0; // 00000000b
    
    		return headerBytes;
      
  	}
  
  	private byte[] generateQuestion() { 
     
  		// startet bei 12 Byte
  		int intPosition = 12;
  		// Die Länge ist das erste nach dem 12. Byte
  		int length = (int)requestBytes[intPosition];
  		Vector<Byte> domainVector = new Vector<Byte>();
  		// in den Paketbuffer
  		domainVector.addElement((byte)length);
  		// weiter
  		intPosition++;
  
  		// solange noch Daten vorhanden sind..
  		while (length != 0) {
    			for (int i = intPosition; i < intPosition + length; i++) {
      				// ..jedes Byte in den Buffer kopieren
      				domainVector.addElement(requestBytes[i]);
    			}    
    			// um soviel weitergehen wie in den Buffer geschrieben wurde
    			intPosition += length;
    			length = (int)requestBytes[intPosition];
    			domainVector.addElement((byte)length);
    			intPosition++;
  		}
  
  		// die nächsten 4 Bytes einfach kopieren, bei Fragen die Antwort und umgekehrt
  		domainVector.addElement(requestBytes[intPosition + 0]);
  		domainVector.addElement(requestBytes[intPosition + 1]);
  		domainVector.addElement(requestBytes[intPosition + 2]);
  		domainVector.addElement(requestBytes[intPosition + 3]);
  
  		// ein ByteArray aus dem Buffer erstellen
  		byte[] questionBytes = new byte[domainVector.size()];
  		for (int i = 0; i < domainVector.size(); i++) {
  			questionBytes[i] = domainVector.get(i);
  		}
  
  		return questionBytes;
  	}
  
  	private byte[] generateAnswer(byte[] requestBytes) {
    
  		// startet bei 16 Bytes
  		byte[] answerBytes = new byte[16];
  		
  		// Name
  		answerBytes[0] = (byte)192;	// 11000000b
  		answerBytes[1] = 12; // 00001100b
  		
  		// Type (RR)
  		answerBytes[2] = 0; // 00000000b
  		answerBytes[3] = 1; // 00000001b
  
  		// Class (Internet)
  		answerBytes[4] = 0; // 00000000b
  		answerBytes[5] = 1; // 00000001b
  
  		//TTL
  		answerBytes[6] = 0; // 00000000b
  		answerBytes[7] = 0; // 00000000b
  		answerBytes[8] = 0; // 00000000b
  		answerBytes[9] = (byte)151; // 10010111b
  
  		// Länge
  		answerBytes[10] = 0; // 00000000b
  		answerBytes[11] = 4; // 00000100b
  
  		// Data (IP Adresse)
  		answerBytes[12] = requestBytes[0];
  		answerBytes[13] = requestBytes[1];
  		answerBytes[14] = requestBytes[2];
  		answerBytes[15] = requestBytes[3];
  
  		return answerBytes;
  	}
  
  	private byte[] copyBytes(byte one[], byte two[], byte[] three) {
    
  		byte[] bBuffer = new byte[one.length + two.length + three.length];
  		System.arraycopy(one, 0, bBuffer, 0, one.length);
  		System.arraycopy(two, 0, bBuffer, one.length, two.length);
  		System.arraycopy(three, 0, bBuffer, (one.length + two.length), three.length);
  		return (bBuffer);
      
  	}
  
} 
