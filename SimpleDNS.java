import java.io.*;
import java.net.*;
import java.util.*;

import java.nio.charset.StandardCharsets;

public class SimpleDNS extends Thread {

	private static final String		CONFDIR	=	"./";

	private int					_intPort;
	private boolean 			_varListen;
	private EventListener		_elSystem;

	public SimpleDNS(String config) {
  
		printBanner();
		loadSettings(config);
    
	}

	public void close() {

		_varListen = false;
		_elSystem.close();
		
		try {
			Thread.currentThread().sleep(500);
		}
		catch (Exception e) { }

		System.exit(0);
	}

	public static byte[] IPv4toByteArray(String address) throws Exception {
  
		byte[] bAddress = new byte[4];
		String[] strSplit = address.split("\\.");

		if (strSplit.length != 4) {
			Exception e = new Exception("Invalid IPv4 address: " + address);
			throw(e);
		}
	
		for (int i = 0; i < 4; i++) {
			int temp = Integer.parseInt(strSplit[i]);
			bAddress[i] = (byte)temp;
		}
	
		return bAddress;
    
	}

	public void run() {
		_elSystem = new EventListener(this, 4445);
		_elSystem.start();

		try {
			_varListen = true;
			listen();
			_varListen = false;
		}
		catch (Exception e) {
			_varListen = false;
		}
	}

	public void listen() throws Exception {
		byte[] bRequest;
		DatagramPacket dPacket;
		DatagramSocket dSocket = new DatagramSocket(_intPort);

		while (_varListen) {
			bRequest = new byte[96];
			dPacket = new DatagramPacket(bRequest, bRequest.length);
			dSocket.receive(dPacket);
			new ClientThread(dSocket, dPacket.getAddress(), dPacket.getPort(), bRequest).start();
      
		}

		dSocket.close();
	}

	public class ClientThread extends Thread {
		private int				_intPort;
		private byte[]			_bRequest;
		private InetAddress 	_inaSender;
		private DatagramSocket	dns;

		public ClientThread(DatagramSocket socket, InetAddress address, int port, byte[] request) {
			_intPort = port;
			_bRequest = request;
			_inaSender = address;
			dns = socket;
		}

		public void run() {
			byte[] responseBytes;
			byte[] bufferBytes;
      
			DNSResponse dnsResponse = new DNSResponse(_bRequest);      
			String strDomain = dnsResponse.getDomain();

			bufferBytes = getAddress(strDomain);
			responseBytes = dnsResponse.getResponse(bufferBytes);
	    DatagramPacket packet = new DatagramPacket(responseBytes, responseBytes.length, _inaSender, _intPort);
			try {
	        dns.send(packet); // an den resolver
			} catch (Exception e) { }

      
		}

		private byte[] getAddress(String domain) { 
    
			DNSEntry dnsEntry = null;		
    
          byte[] bufferBytes = null;
  				try {          
  					bufferBytes = DNSQuery.lookup(domain);
  					return bufferBytes;          
  				} catch (Exception e) { }
          return bufferBytes;
      
		}

		private boolean isPresent(String domain, String[] array) {
			domain = domain.trim().toLowerCase();

			for (String strBuffer : array) {
				if (domain.endsWith(strBuffer.trim().toLowerCase()))
					return true;
			}

			return false;
		}
    
	}

	private void loadSettings(String config) {

		try {
			FileReader fReader = new FileReader(config);
			BufferedReader bReader = new BufferedReader(fReader);
			String strBuffer;
			while ((strBuffer = bReader.readLine()) != null) {
  				try {
  					parseSetting(strBuffer);
  				} catch (Exception e) { }
      } 
			bReader.close();
			fReader.close();
		} catch (Exception e) { }
    
	}

	private void parseSetting(String setting) throws Exception {
  
		String strSetting;
		String[] strSplit = setting.split("=");
		
		if (strSplit.length != 2)
			return;
		else if (strSplit[0].trim().equals(""))
			return;
		else if (strSplit[0].trim().startsWith("#"))
			return;

		strSetting = strSplit[0].trim().toLowerCase();

		if (strSetting.equals("listen"))
			_intPort = Integer.parseInt(strSplit[1]);
	}

	private static void printBanner() {
		System.out.println("[INFO] SimpleDNS auf Port 4445 gestartet\n");
	}
	
	public static void main(String[] args) {

		SimpleDNS dnsThread;
		String strConfig = CONFDIR + "simpledns.conf";
		
		if (args.length != 0) {
			strConfig = args[0].trim();
		}
		
		dnsThread = new SimpleDNS(strConfig);
		dnsThread.start();

		try {
			dnsThread.join();
		}
		catch (Exception e) {
			System.exit(-1);
		}

		System.exit(0);
    
	}
  
}
