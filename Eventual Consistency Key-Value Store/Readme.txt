Steps to Compile and run:

1. execute 'chord.thirft' to get gen-java folder 
2. execute command 'make' : this would compile my files
3. run command './server.sh <port>' : this would start server
4. run command './client.sh <ip> <port>: this would run client

Description of implementation:

Language: Java
Implemented a basic distributed hash table (DHT) with an architecture similar to the Chord system. 
Implemented 6 functions provided by Filestore.Iface interface. 
On getting successor of key, file is checked with owner, and if server owns it, accordingly required operation is performed