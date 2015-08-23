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
Build using maven

	mvn package
	
Extract zip into JMeter directory

	cd jmeter_home
	unzip path/to/jmeter_s3_custom_sampler/target/*.zip 

Run jmeter as ususual
 
	sh ./bin/jmeter.sh 


Add a new jmeter Java sampler, use the com.bigstep.S3Sampler class.
![Alt text](/img/jmeter1.png?raw=true "Select jmeter custom sampler")

Add your AWS key id, bucket, object and the rest.
![Alt text](/img/jmeter2.png?raw=true "Configure jmeter sampler")

When testing against another system then AWS use the proxy settings to redirect requests somewhere else. Please note that the requests will still have the original host header pointing to amazonaws.com but the system should handle the requests nontheless.

Only GET and PUT methods are currently implemented but others should be very easy to add. 

