package org.innopolis.kuzymvas.codeprovider;

import org.innopolis.kuzymvas.runtimeclass.AccessModifier;
import org.innopolis.kuzymvas.runtimeclass.InheritanceModifier;
import org.innopolis.kuzymvas.runtimeclass.MethodDescriptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Создает описания методов с телом, считанным с консоли.
 */
public class ConsoleMethodReader implements MethodProvider {

    private final MethodDescriptor descriptor;

    /**
     * Создает новый поставщик, принимая все элементы заголовка метода и считывая его тело с консоли
     * @param name - имя метода
     * @param accessModifier - уровень видимости метода
     * @param inheritanceModifier - является ли метод абстрактным или финальным
     * @param returnType - тип возвращаемого значения метода
     * @param args - аргументы метода
     * @throws IOException - при ошибке чтения с консоли
     */
    public ConsoleMethodReader(
            String name, AccessModifier accessModifier,
            InheritanceModifier inheritanceModifier, String returnType, String args) throws IOException {
        descriptor = new MethodDescriptor();
        descriptor.setName(name);
        descriptor.setAccessModifier(accessModifier);
        descriptor.setInheritanceModifier(inheritanceModifier);
        descriptor.setReturnType(returnType);
        descriptor.setArgs(args);
        descriptor.setBody(getMethodBodyFromConsole());
    }

    @Override
    public MethodDescriptor provideMethod() {
        return descriptor;
    }

    /**
     * Выполняет чтение тела метода с консоли
     * @return - прочитанное тело метода
     * @throws IOException - при ошибке чтения с консоли
     */
    private String getMethodBodyFromConsole() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder builder = new StringBuilder();
        System.out.println("Please input the following method body. To end the input enter the empty line:");
        System.out.println(descriptor.getMethodHeader() + " {");
        String line = reader.readLine();
        while (!line.equals("")) {
            builder.append(line).append("\n");
            line = reader.readLine();
        }
        System.out.println("}");
        System.out.println("Input complete.");
        return builder.toString();
    }
}
