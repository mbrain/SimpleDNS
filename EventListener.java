import java.net.*;
import java.util.*;

public class EventListener extends Thread {

	private int	port;
	private boolean	listen;
	private SimpleDNS	dns;

	public EventListener(SimpleDNS _dns, int _port) {
		port = _port;
		dns = _dns;
	}

	public void close() {
		listen = false;
	}

	public void run() {
		try {
			listen();
		}
		catch (Exception e) { }
	}

	private void listen() throws Exception { 
  
		listen = true;
		byte[] bufferBytes = new byte[4];
		DatagramSocket socket = new DatagramSocket(port);
		DatagramPacket packet; 
    
		while(listen) {  
			packet = new DatagramPacket(bufferBytes, bufferBytes.length);
			socket.receive(packet);
      System.out.println(packet.getAddress().getHostAddress());
			if (packet.getAddress().getHostAddress().equals("127.0.0.1")) {
				if (bufferBytes[0] == 24)
					if (bufferBytes[1] == 48)
						dns.close();
			}  
		} 

		socket.close();  
    
	}
  
}
