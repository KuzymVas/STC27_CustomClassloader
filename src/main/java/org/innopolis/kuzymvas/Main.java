package org.innopolis.kuzymvas;

import org.innopolis.kuzymvas.classloader.MappedClassLoader;
import org.innopolis.kuzymvas.codeprovider.ConsoleMethodReader;
import org.innopolis.kuzymvas.codeprovider.MethodProvider;
import org.innopolis.kuzymvas.runtimeclass.AccessModifier;
import org.innopolis.kuzymvas.runtimeclass.InheritanceModifier;
import org.innopolis.kuzymvas.runtimeclass.RuntimeClass;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        RuntimeClass someClassSource = new RuntimeClass("org.innopolis.kuzymvas", "SomeClass", AccessModifier.PUBLIC,
                                                        InheritanceModifier.NONE);
        someClassSource.addInterface("Worker");
        MethodProvider methodProvider;
        try {
            methodProvider = new ConsoleMethodReader("doWork", AccessModifier.PUBLIC,
                                                     InheritanceModifier.NONE, "void",
                                                     "");
        } catch (IOException e) {
            System.out.println("IO error, while reading the method from the console. Aborting");
            e.printStackTrace();
            return;
        }
        someClassSource.addMethod(methodProvider.provideMethod());
        Optional<Path> pathToClass = someClassSource.writeAndCompile(Paths.get("WorkDir"));
        if (!pathToClass.isPresent()) {
            System.out.println("Compilation failed. Can't load a class. Aborting");
            return;
        }
        MappedClassLoader classLoader = new MappedClassLoader();
        classLoader.mapClass(someClassSource.getClassName(), pathToClass.get());
        try {
            Class<?> someClass = classLoader.loadClass(someClassSource.getClassName());
            Worker worker = (Worker) someClass.getConstructors()[0].newInstance();
            System.out.println("someClass loaded successfully. Invoking doWork() method.");
            worker.doWork();
        } catch (ClassNotFoundException e) {
            System.out.println("Classloader failed to load a class. Aborting");
        } catch (IllegalAccessException e) {
            System.out.println("Constructor is inaccessible. Can't create object. Aborting");
        } catch (InstantiationException e) {
            System.out.println("Instantiation failed. Can't create object. Aborting");
        } catch (InvocationTargetException e) {
            System.out.println("Constructor thrown an exception. Can't create object. Aborting");
        }
    }
}
