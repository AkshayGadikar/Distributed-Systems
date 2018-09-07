
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;


public class StubImplementor implements ReplicaServices.Iface {
	List<NodeID> NodeList = null;
	NodeID nodeID = null;
	Map<Integer,DataValue> KeyValueStoreMap = new HashMap<Integer, DataValue>();
	Map<Integer,Integer> consistencyCount = new HashMap<Integer,Integer>();
	String IPAddr;
	int port;
	private static int count = 0;

	public StubImplementor(String ipAddr, int port) throws NoSuchAlgorithmException {
		this.nodeID = new NodeID(ipAddr, port);
		IPAddr = ipAddr;
		this.port = port;
	}
	
	public static synchronized int getID() {
        return count++;
    }


	@Override
	public void setFingertable(List<NodeID> node_list) throws TException {
		NodeList = node_list;
		for(int i=0;i<NodeList.size();i++) {
			System.out.println(NodeList.get(i).ip+":"+NodeList.get(i).port);
		}
	}

	@Override
	public void ReadRepair(int key) throws SystemException, TException {
		// TODO Auto-generated method stub
		//start thread for readrepair
		ReadSync readRepair = new ReadSync(this,key);
		readRepair.start();

	}

	@Override
	public void Hinted_handoff(int key, String value) throws SystemException, TException {
		// TODO Auto-generated method stub

	}

	@Override
	public void WriteData(int key, String value, int consistencyLevel) throws SystemException, TException {
		synchronized(this) {
			List<UpdateLog> currentThreads = new ArrayList<UpdateLog>();
			String currentTS = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); 
			String result = null;
			UpdateLog updatelog = null;
			System.out.println("inside WriteData");
			System.out.println("IP:"+IPAddr);
			System.out.println("port:"+port);
			int requestID = getID();
			consistencyCount.put(requestID, 0);
			for(int i=0;i<NodeList.size();i++) {
				//write thread
				updatelog = new UpdateLog(this,key,value,consistencyLevel,currentTS,i,requestID);
				currentThreads.add(updatelog);
				updatelog.start();
			}
			System.out.println("loop completed");
			try {
				currentThreads.get(getCurrentIndex()).join();
				System.out.println("operation completed");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void putInMap(int key, String value, String ts) throws SystemException, TException{
		DataValue dataObj = new DataValue(ts, value);
		KeyValueStoreMap.put(key, dataObj);
	}


	@Override
	public String UpdateReplicas(int key, String value, String ts) throws SystemException, TException {
		//write to log
		writeToLog(key,value,ts);
		//write to map
		putInMap(key, value, ts);
		return "OK";
	}

	@Override
	public String ReadData(int key, int consistencyLevel) throws SystemException, TException {
		// TODO Auto-generated method stub
		//wait for response from number of replicas specified
		//compare timestamp
		//update accordingly
		//call readrepair(key);
		//return value
		return null;
	}

	private void writeToLog(int key, String value, String timestamp) {
		System.out.println(IPAddr);
		List<String> fileData = new ArrayList<String>();
		File logFile = new File(IPAddr+port+ ".txt");
		try {
			if(!logFile.exists()){logFile.createNewFile();}
				System.out.println("entry");
				FileWriter fw = new FileWriter(logFile,true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(key+":"+value+":"+timestamp+System.lineSeparator());
				bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}


@Override
public DataValue GetDataValue(int key) throws SystemException, TException {
	// TODO Auto-generated method stub
	return KeyValueStoreMap.get(key);
}

private int getCurrentIndex() {
	int currentIndex = 0;
	for(int i=0;i<NodeList.size();i++) {
		if(NodeList.get(i).getIp().equals(IPAddr) && NodeList.get(i).getPort() == port) {
			currentIndex =  i;
			break;
		}
	}
	return currentIndex;
}
}
