package serialkiller.util;

import serialkiller.util.LookAheadObjectInputStream;

import java.io.*;

public class Serializables {

	public static byte[] serialize(final Object obj) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		serialize(obj, out);
		return out.toByteArray();
	}

	public static void serialize(final Object obj, final OutputStream out) throws IOException {
		final ObjectOutputStream objOut = new ObjectOutputStream(out);
		objOut.writeObject(obj);				
	}
	
	public static Object deserialize(final byte[] serialized) throws IOException, ClassNotFoundException {
		final ByteArrayInputStream in = new ByteArrayInputStream(serialized);
		return deserialize(in);
	}
	
	public static Object deserialize(final InputStream in) throws ClassNotFoundException, IOException {
		final ObjectInputStream objIn = new ObjectInputStream(in);
		return objIn.readObject();
	}

    public static Object secure_deserialize(final byte[] serialized) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream in = new ByteArrayInputStream(serialized);
        return secure_deserialize(in);
    }

    public static Object secure_deserialize(final InputStream in) throws ClassNotFoundException, IOException {
        final LookAheadObjectInputStream objIn = new LookAheadObjectInputStream(in);
        return objIn.readObject();
    }


}