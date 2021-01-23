# OpenTTDAdmin
OpenTTD Admin Client Library
Connects to the admin port of the OTTD and offers a Java-API for sending and receiving events to/from the OTTD server.

Overview
--------

![Architecture](doc/drawing.png)

Just focus of the business logic of your application and leave the networking stuff to the library.
- Receive notifications of the events you are interested in.
- Send requests to the server as you need them.
- Integrate with other tools of your choice.

How-To
------

Grab a pre-generated JAR file and include it in your project or download the whole code and compile it by yourself.
The Maven file is configured for JDK 11, but the code is compatible with older language versions as well.

1. Create an instance of the [OttdAdminClient](src/main/java/de/miltschek/openttdadmin/OttdAdminClient.java), providing the required parameters:
    - either only the admin password to your OTTD server (the connection is made to a localhost and a standard port of 3977 over TCP)
    - or the host address, port number and the admin password
1. Do the configuration:
    - decide what events you want to receive from the server (some of them are generated in any case; those manually polled will always be delivered), using the functions *setUpdate...* and *setDelivery...*
    - create listeners for the events that you are interested in (the adapters are created in such a way that you only need to override these cases that you really use, see: [ClientListenerAdapter](src/main/java/de/miltschek/openttdadmin/data/ClientListenerAdapter.java), [CompanyListenerAdapter](src/main/java/de/miltschek/openttdadmin/data/CompanyListenerAdapter.java) and [ServerListenerAdapter](src/main/java/de/miltschek/openttdadmin/data/ServerListenerAdapter.java))
1. Start the client. An instance of a client may only be used once. It means, once stopped, the internal workers can't be restarted again. You need a new instance of the client in such case.
1. Ensure your application has at least one active thread. Otherwise, the application will just quit, since the client is of a daemon type (works for as long as there is some foreground thread alive).
1. Don't worry about any network issues, disconnects etc. The client works in an endless loop and tries to keep the connection active all the time.
1. If you are done, you may stop the client, so that any resources are cleaned in a nice way. It is not a must.

Demo App
--------
A simple app presenting how to use the library is to be found under [Demo](src/main/java/de/miltschek/openttdadmin/Demo.java). It shows on how to use all offered functions.
This is the default app that is started if you just execute the jar file.

Functional Example
------------------
An example of a functional tool, reacting on some chat commands, looking ip the location of the clients and forwarding some of the data to Slack channel can be found under [BasicTool](src/main/java/de/miltschek/openttdadmin/BasicTool.java).

Integration
-----------
The code contains a sample integration of external tools. Please note, the implementation is *very* basic and its intention is only to demonstrate the possibilities:
- Slack integration in [SlackClient](src/main/java/de/miltschek/openttdadmin/integration/SlackClient.java]
- ip-api.com integration in [GeoIp](src/main/java/de/miltschek/openttdadmin/integration/GeoIp.java)

Development
-----------
The project is still under development. The basic idea stays stable. The "todo" comments indicate the areas that are about to change.
