/* Written By Alexandru Bordei
*  Bigstep.com
*  Distrubuted under the same license as apache jmeter itself.
* http://www.apache.org/licenses/LICENSE-2.0
*/
package com.bigstep;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.io.File;
import java.io.Serializable;

import com.bigstep.Helium;

public class HeSampler extends AbstractJavaSamplerClient implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggingManager.getLoggerForClass();
	private Helium he = null;

    // set up default arguments for the JMeter GUI
    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        
        defaultParameters.addArgument("datastore", "");
        defaultParameters.addArgument("method", "GET");
        defaultParameters.addArgument("object", "");
        defaultParameters.addArgument("key_id", "");
        return defaultParameters;
    }
	
	@Override 
    public void setupTest(JavaSamplerContext context)
    {
	try
	{
		String datastore = context.getParameter("datastore");

        he.open(datastore);
	}
	catch(Exception ex)
		{
			// log.error("setupTest failed:"+ datastore);
		}
			
    }
	
	@Override	
    public void teardownTest(JavaSamplerContext context)
    {
	//if(null!=client)
 	//	client.shutdown();	
    he.close();	
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        // pull parameters
        String object = context.getParameter("object");
        String method = context.getParameter("method");
        String key_id = context.getParameter("key_id");

        SampleResult result = new SampleResult();
        result.sampleStart(); // start stopwatch
		long startTime=System.nanoTime();
		
        try {
            if (method.equals("GET")) {

        		if(!he.exists(key_id))
        	    he.lookup(key_id);
             //   he.close();
            } else if (method.equals("PUT")) {
            //	Helium he = new Helium();
            //  he.open(datastore);
            	he.insert(key_id,object);
           // 	he.close();
            }
			
	    long endTime=System.nanoTime();
        result.sampleEnd(); // stop stopwatch
        result.setSuccessful(true);
		result.setResponseMessage( Long.toString(endTime-startTime) );
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