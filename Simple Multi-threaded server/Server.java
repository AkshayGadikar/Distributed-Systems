import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Server {
	
	public static void main(String args[]) throws IOException {
		Server server = new Server();
		HashMap<String,Integer> FileAccessMap = new HashMap<String, Integer>();
		server.runServer(FileAccessMap);
	}

	public void runServer(HashMap<String, Integer> fileAccessMap) {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(0);
			String hostname = serverSocket.getInetAddress().getLocalHost().getHostName();
			System.out.println("host name:"+ hostname);
			System.out.println("Port no: " + serverSocket.getLocalPort());
			while(true) {
				Socket socket = serverSocket.accept();
				ServerThread serverThread = new ServerThread(socket,fileAccessMap);
				serverThread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in connecting to socket");
			e.printStackTrace();
		}	
	}
}
