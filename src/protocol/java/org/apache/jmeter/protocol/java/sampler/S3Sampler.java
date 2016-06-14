/* Written By Alexandru Bordei
*  Bigstep.com
*  Distrubuted under the same license as apache jmeter itself.
* http://www.apache.org/licenses/LICENSE-2.0
*/
package com.bigstep;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.File;
import java.io.Serializable;


public class S3Sampler extends AbstractJavaSamplerClient implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggingManager.getLoggerForClass();

    // set up default arguments for the JMeter GUI
    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("method", "GET");
        defaultParameters.addArgument("bucket", "");
        defaultParameters.addArgument("object", "");
        defaultParameters.addArgument("key_id", "");
        defaultParameters.addArgument("secret_key", "");
        defaultParameters.addArgument("local_file_path", "");
        defaultParameters.addArgument("proxy_host", "");
        defaultParameters.addArgument("proxy_port", "");
        defaultParameters.addArgument("endpoint", "");
        return defaultParameters;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        // pull parameters
        String bucket = context.getParameter("bucket");
        String object = context.getParameter("object");
        String method = context.getParameter("method");
        String local_file_path = context.getParameter("local_file_path");
        String key_id = context.getParameter("key_id");
        String secret_key = context.getParameter("secret_key");
        String proxy_host = context.getParameter("proxy_host");
        String proxy_port = context.getParameter("proxy_port");
        String endpoint = context.getParameter("endpoint");

        log.debug("runTest:method=" + method + " local_file_path=" + local_file_path + " bucket=" + bucket + " object=" + object);

        SampleResult result = new SampleResult();
        result.sampleStart(); // start stopwatch

        try {
            ClientConfiguration config = new ClientConfiguration();
            if (proxy_host != null && !proxy_host.isEmpty()) {
                config.setProxyHost(proxy_host);
            }
            if (proxy_port != null && !proxy_port.isEmpty()) {
                config.setProxyPort(Integer.parseInt(proxy_port));
            }
            //config.setProtocol(Protocol.HTTP);

            AWSCredentials credentials = new BasicAWSCredentials(key_id, secret_key);

            AmazonS3 s3Client = new AmazonS3Client(credentials, config);
            if (endpoint != null && !endpoint.isEmpty()) {
                s3Client.setEndpoint(endpoint);
            }
            ObjectMetadata meta = null;

            if (method.equals("GET")) {
                File file = new File(local_file_path);
                //meta= s3Client.getObject(new GetObjectRequest(bucket, object), file);
                S3Object s3object = s3Client.getObject(bucket, object);
                S3ObjectInputStream stream = s3object.getObjectContent();
                //while(stream.skip(1024*1024)>0);
                stream.close();
            } else if (method.equals("PUT")) {
                File file = new File(local_file_path);
                s3Client.putObject(bucket, object, file);
            }

            result.sampleEnd(); // stop stopwatch
            result.setSuccessful(true);
            if (meta != null) {
                result.setResponseMessage("OK on url:" + bucket + "/" + object + ". Length=" + meta.getContentLength());
            } else {
                result.setResponseMessage("OK on url:" + bucket + "/" + object + ".No metadata");
            }
            result.setResponseCodeOK(); // 200 code

        } catch (Exception e) {
            result.sampleEnd(); // stop stopwatch
            result.setSuccessful(false);
            result.setResponseMessage("Exception: " + e);

            // get stack trace as a String to return as document data
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(stringWriter));
            result.setResponseData(stringWriter.toString());
            result.setDataType(org.apache.jmeter.samplers.SampleResult.TEXT);
            result.setResponseCode("500");
        }

        return result;
    }
}
