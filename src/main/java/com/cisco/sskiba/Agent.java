package com.cisco.sskiba;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;

public class Agent {
    private static final String agentAbsolutPath = "/Users/sskiba/Desktop/javaProjects/SimpleJavaAgent/target/Agent0-jar-with-dependencies.jar";
    public static void premain (
            String agentArgs, Instrumentation inst) {
        System.out.println("\n STATIC LOAD WORKS FINE! \n");

        measureTimeOfProcesses(inst);
    }
    public static void agentmain(
        String agentArgs, Instrumentation inst) throws IOException, AttachNotSupportedException {

        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        String pid = nameOfRunningVM.substring(0, nameOfRunningVM.indexOf('@'));
        VirtualMachine vm = VirtualMachine.attach(pid);

        try {
            vm.loadAgent(agentAbsolutPath);
            System.out.println("\n DYNAMIC LOAD WORKS FINE! \n");

            printLoadedClasses(inst);

        } catch (AgentLoadException | IOException | AgentInitializationException e) {
            throw new RuntimeException(e);
        } finally {
            vm.detach();
        }

    }

    private static void printLoadedClasses(Instrumentation inst){
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader,
                                    String name,
                                    Class<?> typeIfLoaded,
                                    ProtectionDomain domain,
                                    byte[] buffer) {

                if (name.contains("com/cisco/sskiba") || name.contains("org/springframework/samples/petclinic/owner"))//   org/springframework/samples/petclinic    com/appdynamics/extensions/network
                    System.out.println("Class was loaded: " + name);
                return null; //return null to not transform and just print the most recent loaded class
            }
        });

    }

    private  static void measureTimeOfProcesses(Instrumentation inst){
        inst.addTransformer(new ClassTransformer());
    }
}
