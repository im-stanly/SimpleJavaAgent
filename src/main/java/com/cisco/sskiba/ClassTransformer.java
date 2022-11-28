package com.cisco.sskiba;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.time.LocalDateTime;

public class ClassTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer
    ) throws IllegalClassFormatException {
        byte[] bytecode = classfileBuffer;

        if (className.contains("org/springframework/samples/petclinic/owner")){
            ClassPool classPool = ClassPool.getDefault();
            System.out.println(
                    "[ JAVA AGENT ] {\"currentTime\": \"" + LocalDateTime.now() + "\", \"model\": " +
                            "\"class\", \"name\": \"" + className + "\", \"event\": \"loading\"}"
            );

            try {
                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(bytecode));
                CtMethod[] ctMethods = ctClass.getDeclaredMethods();

                for (CtMethod method : ctMethods){
                    if (hasBody(method) && (
                            method.getName().equals("initCreationForm") ||
                            method.getName().equals("initFindForm")
                    )) {
                        transformMethod(method);
                    }
                }
                bytecode = ctClass.toBytecode();
                ctClass.detach();
            } catch (IOException | CannotCompileException e) {
                e.printStackTrace();
            }
        }
        return bytecode;
    }

    private static boolean hasBody(CtMethod method) {
        return !Modifier.isNative(
                method.getModifiers()) &&
                !Modifier.isAbstract(method.getModifiers()) &&
                !Modifier.isInterface(
                        method.getModifiers()
                );
    }

    private static void transformMethod(CtMethod ctMethod){
        try {
            System.out.println(
                    "[ AGENT METHOD ] {\"currentTime\": \"" + LocalDateTime.now() + "\", " +
                            "\"model\": \"method\", \"name\": \"" + ctMethod.getName() + "\", " +
                            "\"event\": \"loaded\"}"
            );

            ctMethod.addLocalVariable(
                    "startTime", CtClass.longType
            );
            ctMethod.insertBefore(
                    "startTime = System.nanoTime();"
            );
            ctMethod.insertAfter(
                    "System.out.println(\"[ AGENT METHOD ] {\\\"currentTime\\\": \\\""
                            + LocalDateTime.now() + "\\\", \\\"model\\\": \\\"method\\\", " +
                            "\\\"name\\\": \\\"" + ctMethod.getName() + "\\\", " +
                            "\\\"event\\\": \\\"execution_time\\\", " +
                            "\\\"time\\\": \\\"\" + (System.nanoTime() - startTime) + \"\\\"}\");"
            );
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }
}
