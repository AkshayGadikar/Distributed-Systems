
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import thrift.*;
import thrift.FileStore.Client;

public class StubImplementor implements FileStore.Iface {
	private List<NodeID> fingerTableList = null;
	NodeID nodeID = null;
	HashMap<String, RFile> Filedata = null;
	int NodeIndex;

	public StubImplementor(String ipAddr, int port) throws NoSuchAlgorithmException {
		this.nodeID = new NodeID(generateSHA256(ipAddr + ":" + port), ipAddr, port);
		Filedata = new HashMap<String, RFile>();
	}

	@Override
	public void writeFile(RFile rFile) throws SystemException, TException {
		String key;
		try {
			key = generateSHA256(rFile.getMeta().owner+":"+rFile.getMeta().filename);
			NodeID node = findSucc(key);
			if(node.id.compareTo(this.nodeID.id) == 0) {
				//write to file
				if(Filedata.containsKey(rFile.getMeta().getFilename())) {
					RFile ResObj = Filedata.get(rFile.getMeta().getFilename());
					if(ResObj.getMeta().getOwner().compareTo(rFile.getMeta().owner) == 0) {
						ResObj.setContent(rFile.getContent());
						ResObj.getMeta().setVersion(ResObj.getMeta().getVersion() + 1);
						ResObj.getMeta().setContentHash(rFile.getMeta().getContentHash());
						Filedata.put(rFile.getMeta().getFilename(), ResObj);
					}else {
						throw new SystemException().setMessage("Owner of file is different....File cannot be written");
					}
				}else {
					//put file
					Filedata.put(rFile.getMeta().getFilename(), rFile);
				}
			}
			else {
				throw new SystemException().setMessage("Server does not own the file");
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public RFile readFile(String filename, String owner) throws SystemException, TException {
		String key;
		RFile FileNode = null;
		try {
			key = generateSHA256(owner+":"+filename);
			NodeID node = findSucc(key);
			if(node.id.compareTo(this.nodeID.id) == 0) {
				//read file and return
				if(Filedata.containsKey(filename)) {
					RFile ResObj = Filedata.get(filename);
					if(ResObj.getMeta().getOwner().compareTo(owner) == 0) {
						FileNode = ResObj;	
					}else {
						throw new SystemException().setMessage("Owner of file is different....File cannot be read");
					}
				}else {
					//error file
					throw new SystemException().setMessage("File not found on this server");
				}
			}
			else {
				throw new SystemException().setMessage("Server does not own the file");
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return FileNode;
	}

	@Override
	public void setFingertable(List<NodeID> node_list) throws TException {
		fingerTableList = node_list;
	}

	@Override
	public NodeID findSucc(String key) throws SystemException, TException {
		BigInteger currentNode = new BigInteger(this.nodeID.getId(), 16);
		NodeID ResNode = findPred(key);
		BigInteger resultNode = new BigInteger(ResNode.id, 16);
		if (currentNode.compareTo(resultNode) == 0) {
			ResNode = getNodeSucc();
		} else {
			TTransport transport;
			transport = new TSocket(ResNode.getIp(), Integer.valueOf(ResNode.getPort()));
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			FileStore.Client client = new FileStore.Client(protocol);
			ResNode = client.getNodeSucc();
			transport.close();
		}
		return ResNode;
	}

	@Override
	
	public NodeID findPred(String key) throws SystemException, TException {
		BigInteger val = new BigInteger("1").shiftLeft(256);
		val = val.subtract(new BigInteger("-1"));
		BigInteger keyNode = new BigInteger(key, 16);
		BigInteger SuccessorNode = new BigInteger(fingerTableList.get(0).getId(), 16);
	
		NodeID testNode = this.nodeID;
		BigInteger currentNode = new BigInteger(testNode.getId(), 16);
		
		
		if (currentNode.compareTo(SuccessorNode) <= 0) {
			if (currentNode.compareTo(keyNode) <= 0 && keyNode.compareTo(SuccessorNode) < 0) {
				testNode = this.nodeID;
			}else {
				testNode = findClosestNode(key);
			}
		} else {
			if ((currentNode.compareTo(keyNode) <= 0 && keyNode.compareTo(val) <= 0)
					|| (new BigInteger("0").compareTo(keyNode) <= 0 && keyNode.compareTo(SuccessorNode) < 0)) {
				testNode = this.nodeID;
			}else {
				testNode = findClosestNode(key);
			}
			
		}
		return testNode;
	}

	public NodeID findClosestNode(String key) throws SystemException, TException {
		NodeID testNode = null;
		BigInteger currentNode = new BigInteger(this.nodeID.getId(), 16);
		BigInteger keyNode = new BigInteger(key, 16);
		BigInteger val = new BigInteger("1").shiftLeft(256);
		val = val.subtract(new BigInteger("-1"));
		for (int nodeIndex = fingerTableList.size() - 1; nodeIndex > 0; nodeIndex--) {
			BigInteger fingerTableNode = new BigInteger(fingerTableList.get(nodeIndex).getId(), 16);	
			if (currentNode.compareTo(keyNode) <= 0) {
				if (currentNode.compareTo(fingerTableNode) < 0 && fingerTableNode.compareTo(keyNode) <= 0) {
					// RPC call to that server to find node
					NodeIndex = nodeIndex;
					break;
				}
			} else {
				if ((currentNode.compareTo(fingerTableNode) < 0 && fingerTableNode.compareTo(val) <= 0)
						|| (new BigInteger("0").compareTo(fingerTableNode) < 0
								&& fingerTableNode.compareTo(keyNode) <= 0)) {
					// RPC call to that server to find node
					NodeIndex = nodeIndex;
					break;
				}
			}
		}
		testNode = RPC_callToFindPred(key, fingerTableList.get(NodeIndex));
	return testNode;
	}
			

	@Override
	public NodeID getNodeSucc() throws SystemException, TException {
		// this returns immediate successor, ie:- first entry in fingertable
		return fingerTableList.get(0);
	}

	public String generateSHA256(String inputString) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(inputString.getBytes(StandardCharsets.UTF_8));
		String encoded = DatatypeConverter.printHexBinary(hash);
		return encoded.toLowerCase();
	}

	public NodeID RPC_callToFindPred(String key, NodeID Servernode) throws SystemException, TException {
		BigInteger obtainedNode = new BigInteger(Servernode.id, 16);
		BigInteger currentNode = new BigInteger(this.nodeID.getId(), 16);
		if (currentNode.compareTo(obtainedNode) == 0) {
			return this.findPred(key);
		} else {
			TTransport transport;
			transport = new TSocket(Servernode.getIp(), Integer.valueOf(Servernode.getPort()));
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			FileStore.Client client = new FileStore.Client(protocol);
			Servernode = client.findPred(key);
			transport.close();
			return Servernode;
		}
	}
}
