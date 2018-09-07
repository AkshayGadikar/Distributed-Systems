Steps to Compile and run:

1. execute command 'make' : this would compile my files
2. run command 'java Server' : this would start server

Description of implementation:

using Serversocket class, socket is created on server side.
Server starts and displays its name and port no for client to connect.
Each client connects to the server throught socket.accept() method. Thread is created for each process by extending thread class, and run method is called.
A hashmap is created on server and is maintained till server is running which keeps details of file access.
Inside run method:
the request is parsed, and requested is resource is serched in 'www' directory. If the resource is available, a valid HTTP response is generated, and is sent back to client.
If not found 404 error is returned on client window.
run() method is synchronized, so that only one thread would be able to access map and make changes to the count of access.
If 'www' directory does not exists, message is printed on server side, and server exits.


Sample I/O:

Client:

remote03:~/testDS> wget http://remote00.cs.binghamton.edu:43623/skype-ubuntu-precise_4.3.0.37-1_i386.deb
--2017-09-19 21:46:17--  http://remote00.cs.binghamton.edu:43623/skype-ubuntu-precise_4.3.0.37-1_i386.deb
Resolving remote00.cs.binghamton.edu (remote00.cs.binghamton.edu)... 128.226.180.162
Connecting to remote00.cs.binghamton.edu (remote00.cs.binghamton.edu)|128.226.180.162|:43623... connected.
HTTP request sent, awaiting response... 200 OK
Length: 20112698 (19M) [application/vnd.debian.binary-package]
Saving to: ‘skype-ubuntu-precise_4.3.0.37-1_i386.deb.16’
skype-ubuntu-precis 100%[===================>]  19.18M  --.-KB/s    in 0.1s
2017-09-19 21:46:18 (151 MB/s) - ‘skype-ubuntu-precise_4.3.0.37-1_i386.deb.16’ saved [20112698/20112698]

remote03:~/testDS> wget http://remote00.cs.binghamton.edu:43623/skype-ubuntu-precise_4.3.0.37-1_i386.deb
--2017-09-19 21:46:43--  http://remote00.cs.binghamton.edu:43623/skype-ubuntu-precise_4.3.0.37-1_i386.deb
Resolving remote00.cs.binghamton.edu (remote00.cs.binghamton.edu)... 128.226.180.162
Connecting to remote00.cs.binghamton.edu (remote00.cs.binghamton.edu)|128.226.180.162|:43623... connected.
HTTP request sent, awaiting response... 200 OK
Length: 20112698 (19M) [application/vnd.debian.binary-package]
Saving to: ‘skype-ubuntu-precise_4.3.0.37-1_i386.deb.17’
skype-ubuntu-precis 100%[===================>]  19.18M  --.-KB/s    in 0.06s
2017-09-19 21:46:43 (328 MB/s) - ‘skype-ubuntu-precise_4.3.0.37-1_i386.deb.17’ saved [20112698/20112698]

remote04:~/testDS> wget http://remote00.cs.binghamton.edu:43623/test.html       --2017-09-19 21:47:01--  http://remote00.cs.binghamton.edu:43623/test.html
Resolving remote00.cs.binghamton.edu (remote00.cs.binghamton.edu)... 128.226.180.162
Connecting to remote00.cs.binghamton.edu (remote00.cs.binghamton.edu)|128.226.180.162|:43623... connected.
HTTP request sent, awaiting response... 200 OK
Length: 102 [text/html]
Saving to: ‘test.html.11’
test.html.11        100%[===================>]     102  --.-KB/s    in 0s
2017-09-19 21:47:01 (7.90 MB/s) - ‘test.html.11’ saved [102/102]

Server:

remote00:~/DSproj1> java Server
host name:remote00
Port no: 43623
---------------------------------------
/skype-ubuntu-precise_4.3.0.37-1_i386.deb|128.226.180.162|39914|1
---------------------------------------
---------------------------------------
/skype-ubuntu-precise_4.3.0.37-1_i386.deb|128.226.180.162|39994|2
---------------------------------------
---------------------------------------
/test.html|128.226.180.162|40078|1
/skype-ubuntu-precise_4.3.0.37-1_i386.deb|128.226.180.162|40078|2
---------------------------------------


