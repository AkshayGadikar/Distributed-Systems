.SUFFIXES:	.java .class
.java.class:
	javac $*.java

CLASSES = \
	Server.java \
	ServerThread.java

default:	classes

classes:	$(CLASSES:.java=.class)

clean:
	$(RM) *.class