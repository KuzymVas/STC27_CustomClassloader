package org.innopolis.kuzymvas.runtimeclass;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс для конструирования описания нового класса
 */
public class RuntimeClass {

    private final String packageName;
    private final String name;
    private final AccessModifier accessModifier;
    private final InheritanceModifier inheritanceModifier;
    private final List<String> interfacesNames;
    private final List<MethodDescriptor> methods;

    /**
     * Создает описание нового класса без методов и полей с заданными параметарми
     *
     * @param packageName         - полное имя пакета нового класса
     * @param name                - имя нового класса
     * @param accessModifier      - уровень видимости класса
     * @param inheritanceModifier - модификаторы наследования класса (abstract или final)
     */
    public RuntimeClass(
            String packageName, String name,
            AccessModifier accessModifier, InheritanceModifier inheritanceModifier) {
        this.packageName = packageName;
        this.name = name;
        this.accessModifier = accessModifier;
        this.inheritanceModifier = inheritanceModifier;
        interfacesNames = new ArrayList<>();
        methods = new ArrayList<>();
    }

    /**
     * Добавляет новый интерфейс к строке implements для класса
     *
     * @param interfaceName - имя интерфейса
     */
    public void addInterface(String interfaceName) {
        interfacesNames.add(interfaceName);
    }

    /**
     * Добавляет новый описатель метода к классу
     *
     * @param method - описатель метода
     */
    public void addMethod(MethodDescriptor method) {
        methods.add(method);
    }

    /**
     * Возвращает строку заголовок класса, включая модификаторы, имя и строку implements при наличии интерфейсов
     *
     * @return - строка - заголовок класса
     */
    public String getClassHeader() {
        StringBuilder builder = new StringBuilder();
        switch (accessModifier) {
            case PUBLIC: {
                builder.append("public ");
                break;
            }
            case PRIVATE: {
                builder.append("private ");
                break;
            }
            case PROTECTED: {
                builder.append("protected ");
            }
        }
        switch (inheritanceModifier) {
            case FINAL: {
                builder.append("final ");
                break;
            }
            case ABSTRACT: {
                builder.append("abstract ");
            }
        }
        builder.append("class ");
        builder.append(name);
        if (interfacesNames.size() > 0) {
            builder.append(" implements ");
            for (int i = 0; i < interfacesNames.size() - 1; i++) {
                builder.append(interfacesNames.get(i)).append(", ");
            }
            builder.append(interfacesNames.get(interfacesNames.size() - 1));
        }
        return builder.toString();
    }

    /**
     * Возвращает полный текст класса, начиная от имени пакета и включая заголовк и тело класса
     *
     * @return - строка, содержащая полный текст класса.
     */
    public String getClassFullText() {
        StringBuilder builder = new StringBuilder();
        if (!packageName.isEmpty()) {
            builder.append("package ").append(packageName).append(";\n");
        }
        builder.append(getClassHeader()).append(" {\n");
        for (MethodDescriptor method : methods) {
            builder.append(method.getMethodFullText()).append("\n");
        }
        builder.append("}\n");
        return builder.toString();
    }

    /**
     * Пытается записать класс в одноименный ему файл .java в указанной папке
     * Не поддерживает запись вложенных классов (и соответсвенно никаких классов с модификаторам
     * private и protected)
     *
     * @param workDirPath - путь к рабочей папке
     * @return - Optional, содержащий путь к записанному файлу, или null, если возникла ошибка записи
     */
    public Optional<Path> writeToFile(Path workDirPath) {
        if (accessModifier == AccessModifier.PROTECTED || accessModifier == AccessModifier.PRIVATE) {
            throw new UnsupportedOperationException("Outer class can't have access modifier other " +
                                                            "than public or default and writing nested classes " +
                                                            "is unsupported as of now.");
        }
        String fileFullName = getSourcePath(workDirPath).toString();
        try (PrintStream printer = new PrintStream(new FileOutputStream(fileFullName))) {
            printer.println(getClassFullText());
            return Optional.of(getSourcePath(workDirPath));
        } catch (FileNotFoundException e) {
            System.out.println(
                    "Required file name: " + fileFullName + ", is not writeable.\n Exception text: " + e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    /**
     * Возвращает полное имя класса
     *
     * @return - строка - полное имя класса.
     */
    public String getClassName() {
        return packageName.isEmpty() ? name : packageName + "." + name;
    }

    /**
     * Пытается записать класс на диск и скомпилировать его в .class файл
     *
     * @param workDirPath - рабочая папка для записи и компиляции. Должна соответствовать ожидания компилятора
     * @return - Optional, содержащий путь к скомпилированному файлу .class, или null, если возникла ошибка записи или компиляции
     */
    public Optional<Path> writeAndCompile(Path workDirPath) {
        Optional<Path> pathToJavaFile = writeToFile(workDirPath);
        if (!pathToJavaFile.isPresent()) {
            System.out.println("Unable to write .java file. Compilation aborted.");
            return Optional.empty();
        }
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        if (javac == null) {
            System.out.println("Java compiler not found. Make sure that you run this app with JDK and not the JRE. Compilation aborted");
            System.out.println("Your java home:" + System.getProperty("java.home"));
            return Optional.empty();
        }
        int result = javac.run(null, System.out, System.err,
                               pathToJavaFile.get().toString(), "-cp", workDirPath.toString());
        if (result != 0) {
            System.out.println("Compilation failed.");
            return Optional.empty();
        }
        System.out.println("Compilation successful.");
        return Optional.of(getCompiledPath(workDirPath));
    }

    /**
     * Возвращает путь для записи исходного файла класса .java в указанной директории
     *
     * @param workDirPath - рабочая директория
     * @return - путь к файлу класса .java в в рабочей директории
     */
    private Path getSourcePath(Path workDirPath) {
        return workDirPath.resolve(name + ".java");
    }

    /**
     * Возвращает путь для скомпилированного файла класса .class в указанной директории
     *
     * @param workDirPath - рабочая директория
     * @return - путь к файлу класса .class в в рабочей директории
     */
    private Path getCompiledPath(Path workDirPath) {
        return workDirPath.resolve(name + ".class");
    }
}
