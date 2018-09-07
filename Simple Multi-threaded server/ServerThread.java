import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ServerThread extends Thread {
	Socket socket;
	HashMap<String, Integer> FileAccessMap;
	public static final String RootDirectory = "www";
	public ServerThread(Socket socket, HashMap<String, Integer> fileAccessMap) {
		this.socket = socket;
		this.FileAccessMap = fileAccessMap;
	}
	
	public synchronized void run() {
		File searchHandle = new File(RootDirectory);
		if(searchHandle.exists()) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String[] resource = (bufferedReader.readLine()).trim().split("\\s+");
			if(resource!=null && resource.length > 0) {
			String requestedFileName = resource[1].replaceAll("\\/", "");
			File Filepath = new File(RootDirectory+File.separator+requestedFileName);
			int size = 0;
			StringBuilder response =  new StringBuilder();
			if(checkIfFileExist(searchHandle,requestedFileName)) {
						if(FileAccessMap.containsKey(requestedFileName)) {
							FileAccessMap.put(requestedFileName, FileAccessMap.get(requestedFileName) + 1);
						}else {
							FileAccessMap.put(requestedFileName, 1);
						}
					
					byte [] mybytearray  = new byte [(int)Filepath.length()];
					response.append("HTTP/1.1 200 OK\r\n");
                    response.append("Date: "+getDate()+"\r\n");
                    response.append("Server :YouJustNeedTo...ASK!!\r\n");
                    response.append("Last Modified: "+dateFormat.format(Filepath.lastModified())+"\r\n");
                    response.append("Content-Type: "+Files.probeContentType(Filepath.toPath())+"\r\n");
                    response.append("Content-Length: "+mybytearray.length+"\r\n\r\n");
                    dataOutputStream.write(response.toString().getBytes("UTF-8"));

					InputStream inputFileStream = new FileInputStream(Filepath);
					while ((size = inputFileStream.read(mybytearray, 0, mybytearray.length)) > 0) {
						dataOutputStream.write(mybytearray, 0, size);
					}
					inputFileStream.close();
					printFileAccessStatus(FileAccessMap,socket);
				}
				else {
					response.append("HTTP/1.1 404 Not Found\r\n");
					response.append("Date: "+getDate()+"\r\n");
					response.append("Server :YouJustNeedTo...ASK!!\r\n");
					response.append("Content-Type: text/html\r\n\r\n");
					response.append("<html><body><h1>Status Code : 404 </h1><h2>....:( :( Sorry... File Not Found :( :(</h1></body></html>");
					dataOutputStream.write(response.toString().getBytes("UTF-8"));
				}	
			}
		} catch (IOException e) {
			System.out.println("Error in accessing file");
			e.printStackTrace();
		}finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Probem in closing socket");
				e.printStackTrace();
			}
		}
	}else {
		System.out.println("Error : Directory 'www' does not exist");
		System.exit(0);
	}
}
	

	private void printFileAccessStatus(HashMap<String, Integer> fileAccessMap, Socket socket) throws UnknownHostException {
System.out.println("---------------------------------------");		
for (Map.Entry<String, Integer> entry : fileAccessMap.entrySet()) {
		    System.out.println("/"+entry.getKey()+"|"+socket.getInetAddress().getLocalHost().getHostAddress()+"|"+socket.getPort()+"|"+entry.getValue());
		}
 System.out.println("---------------------------------------");
	}

	public boolean checkIfFileExist(File file, String filename) {
		Boolean fileFound = false;
		String[] fileList = file.list();
		for(String name: fileList) {
			if(filename.equals(name)) {
				fileFound = true;
				break;
			}
		}
		return fileFound;
	}
	
	public String getDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(calendar.getTime());
	}
}
