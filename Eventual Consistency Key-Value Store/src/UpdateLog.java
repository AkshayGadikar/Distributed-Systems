
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


public class UpdateLog extends Thread {
	String Caller_IPAddr;
	int caller_port;
	int key;
	String value;
	String currentTS;
	StubImplementor stubImplementor;
	int index;
	int consistencyLevel;
	String result;
	int requestID;
	
	public UpdateLog(StubImplementor stubImplementor,int key, String value, int consistencyLevel, String timestamp, int index, int requestID) {
		// TODO Auto-generated constructor stub
		Caller_IPAddr = stubImplementor.IPAddr;
		caller_port = stubImplementor.port;
		this.key = key;
		this.value = value;
		this.currentTS = timestamp;
		this.stubImplementor = stubImplementor;
		this.index = index;
		this.consistencyLevel = consistencyLevel;
		this.requestID = requestID;
	}

	public void run() {
	List<String> checkList = new ArrayList<String>();	
	if(stubImplementor.NodeList.get(index).ip.equals(Caller_IPAddr) && stubImplementor.NodeList.get(index).port == caller_port) {
		try {
			result = stubImplementor.UpdateReplicas(key,value,currentTS);
			stubImplementor.consistencyCount.put(requestID, stubImplementor.consistencyCount.get(requestID)+1);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}else {
		//RPC call to replicas and wait for response = consistencyLevel
		try {
		TTransport transport;
		transport = new TSocket(stubImplementor.NodeList.get(index).ip, stubImplementor.NodeList.get(index).port);
		transport.open();
		TProtocol protocol = new TBinaryProtocol(transport);
		ReplicaServices.Client client = new ReplicaServices.Client(protocol);
		result = client.UpdateReplicas(key,value,currentTS);
		transport.close();
		stubImplementor.consistencyCount.put(requestID, stubImplementor.consistencyCount.get(requestID)+1);
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	System.out.println("before exiting");
	if(stubImplementor.NodeList.get(index).ip.equals(Caller_IPAddr) && stubImplementor.NodeList.get(index).port == caller_port) {
		while(stubImplementor.consistencyCount.get(requestID)< consistencyLevel) {
			//keep looping
		}
	}
	System.out.println("exiting");
	}
}

