package com.cisco.sskiba;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;

public class Agent {
    public static void premain (
            String agentArgs, Instrumentation inst) {
        System.out.println("\n STATIC LOAD WORKS FINE! \n");

        //getAllLoadedClasses()
        //getClassPool()
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader,
                                    String name,
                                    Class<?> typeIfLoaded,
                                    ProtectionDomain domain,
                                    byte[] buffer) {
                printLoadedClasses(name);
                return null; //return null to not transform and just print the most recent loaded class
            }
        });

    }
    public static void agentmain(
        String agentArgs, Instrumentation inst) throws IOException, UnmodifiableClassException, AttachNotSupportedException {

        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        String pid = nameOfRunningVM.substring(0, nameOfRunningVM.indexOf('@'));
        VirtualMachine vm = VirtualMachine.attach(pid);

        try {
            vm.loadAgent("/Users/sskiba/Desktop/javaProjects/SimpleJavaAgent/target/Agent0-jar-with-dependencies.jar");
        } catch (AgentLoadException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (AgentInitializationException e) {
            throw new RuntimeException(e);
        } finally {
            vm.detach();
        }

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(Module module,
                                    ClassLoader loader,
                                    String name,
                                    Class<?> typeIfLoaded,
                                    ProtectionDomain domain,
                                    byte[] buffer) {
                if (typeIfLoaded == null) {
                    printLoadedClasses(name);
                } else {
                    System.out.println("Class was re-loaded: " + name);
                }
                return null;
            }
        }, true);
        inst.retransformClasses(
                inst.getAllLoadedClasses());

    }

    private static void printLoadedClasses(String className){
        if (className.contains("com/cisco/sskiba") || className.contains("org/springframework"))
            System.out.println("Class was loaded: " + className);
    }
}
