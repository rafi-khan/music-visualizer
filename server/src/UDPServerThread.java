import java.net.*;
import java.io.IOException;

class UDPServerThread extends Thread
{
	private DatagramSocket ds;
	private int port;
	boolean running, available;
	byte[] receiveData, sendData;
	String data;
	
	public UDPServerThread(int p) {
		port = p;
		running = false;
		available = true;
		try {
			ds = new DatagramSocket(port);
		} catch (SocketException e){
			e.printStackTrace();
		}
		receiveData = new byte[1024];
	    sendData = new byte[1024];
	}
	
	public String getData() {
		available = false;
		return data;
	}
	
	public boolean available() {
		return available;
	}
	
	@Override
	public void start() {
		running = true;
		super.start();
	}
	@Override
	public void run() {
		while(running) {
			checkForData();
			available = true;
		}
	}
	
	private void checkForData() {
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      try {
        ds.receive(receivePacket);
      } catch(IOException e) {
        e.printStackTrace();
      }
      data = new String(receivePacket.getData());
    }
	
	public void quit() {
		System.out.println("Quitting UDP thread...");
		running = false;
		interrupt();
	}
}