LIB_PATH=/home/yaoliu/src_code/local/lib/usr/local/lib/libthrift-0.10.0.jar:/home/yaoliu/src_code/local/lib/usr/local/lib/slf4j-log4j12-1.7.12.jar:/home/yaoliu/src_code/local/lib/usr/local/lib/slf4j-api-1.7.12.jar
all: clean
	mkdir bin
	mkdir bin/client_classes
	mkdir bin/server_classes
	javac -classpath $(LIB_PATH) -d bin/ src/JavaServer.java src/StubImplementor.java src/UpdateLog.java src/ReadSync.java src/JavaClient.java gen-java/*


clean:
	rm -rf bin/
