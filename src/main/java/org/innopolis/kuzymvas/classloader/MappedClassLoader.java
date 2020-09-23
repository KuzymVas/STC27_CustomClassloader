package org.innopolis.kuzymvas.classloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *  Загрузчик классов, сверяющий имена с путяями с помощью таблицыю
 */
public class MappedClassLoader extends ClassLoader {

    private final Map<String, Path> classMap;

    public MappedClassLoader() {
        classMap = new HashMap<>();
    }

    /**
     * Добавляет соответствие имени и пути в таблицу загрузчика
     * @param className - полное имя класса
     * @param pathToClass - путь к соответствующему файлу .class
     */
    public void mapClass(String className, Path pathToClass) {
        classMap.put(className, pathToClass);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            if (classMap.containsKey(name)) {
                final byte[] bytes = Files.readAllBytes(
                        classMap.get(name));
                return defineClass(name, bytes, 0, bytes.length);
            }
            return super.findClass(name);
        } catch (IOException e) {
            throw new ClassNotFoundException();
        }
    }
}