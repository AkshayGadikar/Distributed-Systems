import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;

import thrift.FileStore;
import thrift.NodeID;
import thrift.RFile;
import thrift.RFileMetadata;

import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

public class JavaClient {
  public static void main(String [] args) {

    if (args.length != 3) {
      System.out.println("Please enter simple/secure [ip] [port]");
      System.exit(0);
    }

    try {
      TTransport transport;
      if (args[0].contains("simple")) {
        transport = new TSocket(args[1], Integer.valueOf(args[2]));
        transport.open();
      }
      else {
        /*
         * Similar to the server, you can use the parameters to setup client parameters or
         * use the default settings. On the client side, you will need a TrustStore which
         * contains the trusted certificate along with the public key. 
         * For this example it's a self-signed cert. 
         */
        TSSLTransportParameters params = new TSSLTransportParameters();
        params.setTrustStore("../../lib/java/test/.truststore", "thrift", "SunX509", "JKS");
        /*
         * Get a client transport instead of a server transport. The connection is opened on
         * invocation of the factory method, no need to specifically call open()
         */
        transport = TSSLTransportFactory.getClientSocket(args[1], Integer.valueOf(args[2]), 0, params);
      }

      TProtocol protocol = new  TBinaryProtocol(transport);
      FileStore.Client client = new FileStore.Client(protocol);
      perform(client);

      transport.close();
    } catch (TException x) {
      x.printStackTrace();
    } 
  }

  private static void perform(FileStore.Client client) throws TException
  {
	  RFileMetadata testmeta = new RFileMetadata();
	  RFile testfile = new RFile();
	  testfile.setContent("content");
	  try {
		  testmeta.setFilename("example.txt");
		  testmeta.setOwner("newowner");
		  testfile.setMeta(testmeta);
		  client.writeFile(testfile);
		  System.out.println("File written");
	  }catch(Exception e) {
		  System.out.println(e);
	  }
	  try {
		  testmeta.setFilename("example.txt");
		  testmeta.setOwner("newowner1");
		  client.writeFile(testfile);
		  System.out.println("File written");
	  }catch(Exception e) {
		  System.out.println(e);
	  }
	  try {
		  testmeta.setFilename("example.txt");
		  testmeta.setOwner("newowner2");
		  client.writeFile(testfile);
		  System.out.println("File written");
	  }catch(Exception e) {
		  System.out.println(e);
	  }
	  try {
		  testmeta.setFilename("example.txt");
		  testmeta.setOwner("newowner3");
		  client.writeFile(testfile);
		  System.out.println("File written");
	  }catch(Exception e) {
		  System.out.println(e);
	  }
	  try {
		  System.out.println(client.readFile("example.txt", "newowner1"));
	  }catch(Exception e) {
		  System.out.println(e);
	  }
	  try {
		  System.out.println(client.readFile("example.txt", "newowner2"));
	  }catch(Exception e) {
		  System.out.println(e);
	  }
	  try {
		  System.out.println(client.readFile("example.txt", "newowner3"));
	  }catch(Exception e) {
		  System.out.println(e);
	  }
	  try {
		  System.out.println(client.readFile("example.txt", "newowner"));
	  }catch(Exception e) {
		  System.out.println(e);
	  }
	  try {
		  System.out.println(client.readFile("examplefail.txt", "newowner1"));  
	  }catch(Exception e) {
		  System.out.println(e);
	  }
  }
}
