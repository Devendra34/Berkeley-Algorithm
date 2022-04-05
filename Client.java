// A Java program for a Client 
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.*; 

public class Client { 
	// initialize socket and input output streams 
	private Socket socket = null; 
	private DataInputStream input = null; 
	private DataInputStream in = null; 
	private DataOutputStream out = null; 
	private Date currentTime = null;

	// constructor to put ip address and port 
	public Client(String address, int port) { 
		// establish a connection 
		try { 
			socket = new Socket(address, port); 
			System.out.println("Connected"); 
            currentTime = new Date(System.currentTimeMillis());
			System.out.println("Client time = " + getReadableTime(currentTime)); 
			// takes input from terminal 
			input = new DataInputStream(System.in); 
			// sends output to the socket 
			in = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
			out = new DataOutputStream(socket.getOutputStream()); 
		} 
		catch(Exception u) { 
			System.out.println(u); 
		}
		try {
			System.out.println("Checking timings..");
			long serverTime = in.readLong();
			System.out.println("Server time = " + serverTime);
			long difference = currentTime.getTime() - serverTime;
			out.writeLong(difference);
			long offset = in.readLong();
			System.out.println("Offset = " + offset);
			long correctedTime = currentTime.getTime() + offset;
			Date correctedDate = new Date(correctedTime);
			System.out.println("clock synchronized time by Berkeley algorithm:\n" + getReadableTime(correctedDate)); 
			System.out.println("Closing server connection"); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		try { 
			input.close(); 
			out.close(); 
			socket.close(); 
		} 
		catch(IOException i) { 
			System.out.println(i); 
		} 
	} 

	private String getReadableTime(Date date) {
		return new SimpleDateFormat("HH:MM:S", Locale.getDefault()).format(date.getTime());
	}
	
	public static void main(String args[]) { 
		new Client("127.0.0.1", 5400); 
	} 
} 
