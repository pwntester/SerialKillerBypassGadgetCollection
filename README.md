# SerialKiller: Bypass Gadget Collection

## Description

Collection of Bypass Gadgets that can be used in JVM Deserialization Gadget chains to bypass ["Look-Ahead ObjectInputStreams"](http://www.ibm.com/developerworks/library/se-lookahead/) desfensive deserialization.

Released as part of RSA 2016 Talk ["SerialKiller: Silently Pwning Your Java Endpoints"](https://www.rsaconference.com/events/us16/agenda/sessions/2389/serial-killer-silently-pwning-your-java-endpoints) by Alvaro Muñoz (@pwntester) and Christian Schneider (@cschneider4711).

Details about bypass gadget technique can be found in the following resources:
- ["Paper"](https://community.microfocus.com/t5/Fortify-User-Discussions/The-perils-of-Java-deserialization/td-p/1596306?attachment-id=63108)
- ["Slides"](https://speakerdeck.com/pwntester/serial-killer-silently-pwning-your-java-endpoints)
- ["OWASP BeNeLux Day Slides"](https://www.owasp.org/images/8/8b/OWASPBNL_Java_Deserialization.pdf)

## Disclaimer

This software has been created purely for the purposes of academic research and for the development of effective defensive techniques, and is not intended to be used to attack systems except where explicitly authorized. Project maintainers are not responsible or liable for misuse of the software. Use responsibly.

## Requirements

The current status of this project heavily depends on ["YSoSerial"](https://github.com/frohoff/ysoserial).  project and the idea is to integrate it there in the near future (see below).  It can actually be considered an extension of ysoserial and it reuses some parts of the code and all the payload gadgets in order to facilitate future integration.

Copy the current version (`ysoserial-0.0.5-SNAPSHOT-all.jar`) to `/external` and adjust the `pom.xml` if using a different version.

The following Jar files are required from Weblogic and WebSphere application servers and not distributed with SerialKiller Bypass Gadget Collection.  Copy them from your authorized version of the application server to the `/external` directory.

```
com.ibm.jaxws.thinclient_8.5.0.jar
com.ibm.ws.ejb.embeddableContainer_8.5.0.jar
com.oracle.weblogic.iiop-common.jar
com.ibm.mq.jmqi.jar
com.ibm.ws.ejb.thinclient_8.5.0.jar
com.ibm.msg.client.jms.jar
com.ibm.ws.runtime.coregroupbridge.jar
```

## Build

`mvn clean compile assembly:single`

## Usage

`java -jar target/serialkiller-bypass-gadgets-0.0.1-SNAPSHOT-all.jar <Payload Gadget, eg: CommonsCollections2> <Bypass Gadget, eg: Weblogic1> <Command, eg: 'touch /tmp/test'>`

## Future

The idea is to integrate this project into YsoSerial project as soon as it supports wrapping payloads in bypass gadgets and handle missing dependencies.
