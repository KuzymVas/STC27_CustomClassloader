package org.innopolis.kuzymvas.codeprovider;

import org.innopolis.kuzymvas.runtimeclass.MethodDescriptor;

/**
 *  Интерфейс поставщика описаний методов.
 */
public interface MethodProvider {

    /**
     * Создает описание нового метода
     * @return - описатель метода
     */
    MethodDescriptor provideMethod();
}
