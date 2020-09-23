package org.innopolis.kuzymvas.runtimeclass;

/**
 * Мутабельный класс содержащий описание метода.
 */
public class MethodDescriptor {

    private String name;
    private AccessModifier accessModifier;
    private InheritanceModifier inheritanceModifier;
    private String returnType;
    private String args;
    private String body;

    /**
     * Возвращет заголовок метода, включающий модификаторы, тип возвращаемого значения имя и аргументы.
     * @return - строка - заголовок метода
     */
    public String getMethodHeader() {
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
        builder.append(returnType).append(" ");
        builder.append(name);
        builder.append("(").append(args).append(")");
        return builder.toString();
    }

    /**
     * Возвращает полный текст метода, включая заголовок и тело.
     * @return - строка, содержащая полный текст метода
     */
    public String getMethodFullText() {
        StringBuilder builder = new StringBuilder();
        builder.append(getMethodHeader());
        if (inheritanceModifier == InheritanceModifier.ABSTRACT) {
            builder.append(";");
        } else {
            builder.append(" {\n");
            builder.append(body);
            builder.append("\n}");
        }
        return builder.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(AccessModifier accessModifier) {
        this.accessModifier = accessModifier;
    }

    public InheritanceModifier getInheritanceModifier() {
        return inheritanceModifier;
    }

    public void setInheritanceModifier(InheritanceModifier inheritanceModifier) {
        this.inheritanceModifier = inheritanceModifier;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
