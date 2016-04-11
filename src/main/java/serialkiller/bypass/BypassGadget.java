package serialkiller.bypass;

import org.reflections.Reflections;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by alvaro on 07/04/16.
 */
public interface BypassGadget {

    public Object wrapPayload(String command, Class payload) throws Exception;
    public boolean bypassXStream();
    public boolean skipTest();

    // get payload classes by classpath scanning (from ysoserial)
    public static Set<Class<? extends BypassGadget>> getBypassGadgets () {
        final Reflections reflections = new Reflections(BypassGadget.class.getPackage().getName());
        final Set<Class<? extends BypassGadget>> payloadTypes = reflections.getSubTypesOf(BypassGadget.class);
        for ( Iterator<Class<? extends BypassGadget>> iterator = payloadTypes.iterator(); iterator.hasNext(); ) {
            Class<? extends BypassGadget> pc = iterator.next();
            if ( pc.isInterface() || Modifier.isAbstract(pc.getModifiers()) ) {
                iterator.remove();
            }
        }
        return payloadTypes;
    }

}
