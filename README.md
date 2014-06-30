#Custom JMeter sampler for authenticated S3 requests

This is a custom java sampler class that can be used to benchmark any S3 compatible system.
It was tested against AWS and RIAK-CS 1.3.1.

Version 0.1 (alpha) 
 
Written by: Alex Bordei @ Bigstep
(alex at bigstep dt com)

##Dependencies:
* apache jmeter sources 2.11 
* AWS SDK 1.8.1

##How to use
Copy the file over inside the sources. 
You will need to copy over ./lib/opt and ./lib. some of the jars from the SDK. You need to also copy them in both locations.For some reason the compilation works but the jars from/opt do not get distributed.

* aws-java-sdk-1.8.1.jar
* jackson-annotations-2.1.1.jar
* jackson-core-2.1.1.jar
* jackson-databind-2.1.1.jar
* joda-time-2.2.jar

```
ant package-only
```
Run jmeter as ususual from the newly created bin file. 
```
sh ./bin/jmeter.sh 
```

Add a new jmeter Java sampler, use the com.bigstep.S3Sampler class.
![Alt text](/img/jmeter1.png?raw=true "Select jmeter custom sampler")

Add your AWS key id, bucket, object and the rest.
![Alt text](/img/jmeter2.png?raw=true "Configure jmeter sampler")

When testing against another system then AWS use the proxy settings to redirect requests somewhere else. Please note that the requests will still have the original host header pointing to amazonaws.com but the system should handle the requests nontheless.

Only GET and PUT methods are currently implemented but others should be very easy to add. 

