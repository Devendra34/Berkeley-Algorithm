// A Java program for a Server 
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.io.*; 

public class Server { 
	private static final int CLIENT_COUNT = 2;

	//initialize socket and input stream 
	private ServerSocket server = null; 
	private Date currentTime = null;

	// constructor with port 
	public Server(int port) { 
		// starts server and waits for a connection

		try { 
			server = new ServerSocket(port); 
			System.out.println("Server started"); 
            currentTime = new Date(System.currentTimeMillis());
			System.out.println("Server time = " + getReadableTime(currentTime)); 

			System.out.println("Waiting for a client ..."); 

			ArrayList<Thread> threads = new ArrayList<>();
			ArrayList<ConnectionData> cDataList = new ArrayList<>();
			for (int i = 0; i < CLIENT_COUNT; i++) {
				final int c = i + 1;
				Thread thread = new Thread(() -> {
					try {
						Socket socket = server.accept();
						System.out.println(c + " Client accepted"); 
						System.out.println(socket.getLocalAddress());
						DataOutputStream out = new DataOutputStream(socket.getOutputStream()); 
						DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
						ConnectionData cData = new ConnectionData(socket, in, out);

						out.writeLong(currentTime.getTime());
						cData.difference = in.readLong();
						System.out.println("Difference from Client " + c + " = " + cData.difference);
						cDataList.add(cData);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				thread.start();
				threads.add(thread);
			}

			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			int sum = 0;
			for (ConnectionData cData : cDataList) sum += cData.difference;
			long avg = sum / (CLIENT_COUNT + 1);
			for (ConnectionData cData : cDataList) {
				cData.out.writeLong(avg - cData.difference);
				cData.close();
			}
			long correctedTime = currentTime.getTime() + avg;
			Date correctedDate = new Date(correctedTime);
			System.out.println("clock synchronized time by Berkeley algorithm:\n" + getReadableTime(correctedDate)); 
			System.out.println("Closing server connection"); 

			server.close();

		} 
		catch(IOException i) { 
			System.out.println(i); 
		} 
	}
	
	
	private String getReadableTime(Date date) {
		return new SimpleDateFormat("HH:MM:S", Locale.getDefault()).format(date.getTime());
	}

	public static void main(String args[]) { 
		new Server(5400); 
	} 
} 

class ConnectionData {
	Socket socket;
	DataInputStream in;
	DataOutputStream out;
	long difference;

	public ConnectionData(Socket socket, DataInputStream in, DataOutputStream out) {
		this.socket = socket;
		this.in = in;
		this.out = out;
	}
	public void close() {
		try {
		out.close();
		in.close();
		socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}