package com.cisco.sskiba;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(
            String agentArgs, Instrumentation inst) {
        System.out.println("\n STATIC LOAD WORKS FINE! \n");

        //getAllLoadedClasses()
        //getClassPool()

//        inst.addTransformer(new ClassFileTransformer() {
//            @Override
//            public byte[] transform(Module module,
//                                    ClassLoader loader,
//                                    String name,
//                                    Class<?> typeIfLoaded,
//                                    ProtectionDomain domain,
//                                    byte[] buffer) {
//                System.out.println("Class was loaded: " + name);
//                return null; //return null to not transform and just print the most recent loaded class
//            }
//        });

    }
//    public static void agentmain(
//            String agentArgs, Instrumentation inst) throws IOException, UnmodifiableClassException {
//
//        VirtualMachine vm = VirtualMachine.attach(jvmPid);
//
//        try {
//            vm.loadAgent("/Users/sskiba/Desktop/javaProjects/simpleAgent/src/main/resources/archetype-resources/src/main/java/Agent0.jar");
//        } catch (AgentLoadException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (AgentInitializationException e) {
//            throw new RuntimeException(e);
//        } finally {
//            vm.detach();
//        }
//
//        inst.addTransformer(new ClassFileTransformer() {
//            @Override
//            public byte[] transform(Module module,
//                                    ClassLoader loader,
//                                    String name,
//                                    Class<?> typeIfLoaded,
//                                    ProtectionDomain domain,
//                                    byte[] buffer) {
//                if (typeIfLoaded == null) {
//                    System.out.println("Class was loaded: " + name);
//                } else {
//                    System.out.println("Class was re-loaded: " + name);
//                }
//                return null;
//            }
//        }, true);
//        inst.retransformClasses(
//                inst.getAllLoadedClasses());
//
//    }
}
