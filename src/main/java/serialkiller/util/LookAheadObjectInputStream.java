package serialkiller.util;

import org.apache.commons.collections4.functors.InvokerTransformer;

import java.io.*;

public class LookAheadObjectInputStream extends ObjectInputStream {
    public LookAheadObjectInputStream(InputStream inputStream)
            throws IOException {
        super(inputStream);
    }
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        if (name.indexOf("InvokerTransformer") > 0 || name.indexOf("InterceptorMethodHandler") > 0) {
            throw new InvalidClassException( "Unauthorized deserialization attempt", desc.getName());
        }
        return super.resolveClass(desc);
    }
}
