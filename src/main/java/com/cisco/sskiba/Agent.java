package com.cisco.sskiba;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;

public class Agent {
    private static final String agentAbsolutPath = "/Users/sskiba/Desktop/javaProjects/SimpleJavaAgent/target/Agent0-jar-with-dependencies.jar";
    public static void premain (
            String agentArgs, Instrumentation inst) {
        System.out.println("\n STATIC LOAD WORKS FINE! \n");

        //getAllLoadedClasses()
        //getClassPool()

        //printLoadedClasses(inst);
        measureTimeOfProcesses((inst));

    }
    public static void agentmain(
        String agentArgs, Instrumentation inst) throws IOException, UnmodifiableClassException, AttachNotSupportedException {

        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        String pid = nameOfRunningVM.substring(0, nameOfRunningVM.indexOf('@'));
        VirtualMachine vm = VirtualMachine.attach(pid);

        try {
            vm.loadAgent(agentAbsolutPath);
            System.out.println("\n DYNAMIC LOAD WORKS FINE! \n");

            printLoadedClasses(inst);

        } catch (AgentLoadException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (AgentInitializationException e) {
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

                if (name.contains("com/cisco/sskiba") || name.contains("org/springframework/samples/petclinic"))
                    System.out.println("Class was loaded: " + name);
                return null; //return null to not transform and just print the most recent loaded class
            }
        });

    }

    private  static void measureTimeOfProcesses(Instrumentation inst){
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader,
                                    String name,
                                    Class<?> typeIfLoaded,
                                    ProtectionDomain domain,
                                    byte[] buffer) {

                if (name.equals("org/springframework/samples/petclinic/owner")){ // !! CZY PO PROSTU POWINIEN BYC PACKAGE KLASY KTORA CHCE
                    try {
                        ClassPool cp = ClassPool.getDefault();
                        CtClass cc = cp.get("OwnerController"); // !! czy tutuaj po prostu daje nazwe klasy w ktorej bedzie szukana metoda
                        CtMethod m = cc.getDeclaredMethod(
                                "initFindForm"); // !! CZY PO PROSTU NAZWA METODY KTORA CHCE
                        m.addLocalVariable(
                                "startTime", CtClass.longType);
                        m.insertBefore(
                                "startTime = System.currentTimeMillis();");

                        StringBuilder endBlock = new StringBuilder();

                        m.addLocalVariable("endTime", CtClass.longType);
                        m.addLocalVariable("opTime", CtClass.longType);
                        endBlock.append(
                                "endTime = System.currentTimeMillis();");
                        endBlock.append(
                                "opTime = (endTime-startTime)/1000;");
                        endBlock.append(
                                "System.out.println(\"[Application] NEW OWNER ADDED IN:" +
                                        "\" + opTime + \" SECONDS!\");");

                        m.insertAfter(endBlock.toString());

                        buffer = cc.toBytecode();
                        cc.detach();
                    } catch (NotFoundException | CannotCompileException | IOException e) {
                        e.getMessage();
                        return buffer;
                    }
                    return buffer;
                }
                return null; //return null to not transform and just print the most recent loaded class
            }
        });
    }
}
