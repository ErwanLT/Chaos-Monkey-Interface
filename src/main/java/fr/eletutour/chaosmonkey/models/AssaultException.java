package fr.eletutour.chaosmonkey.models;


import java.util.ArrayList;
import java.util.List;

/**
 * AssaultException
 */
public class AssaultException {

    private String type;

    private String method;

    private List<ExceptionArgument> arguments = new ArrayList<>();

    public AssaultException() {
    }

    public AssaultException(String type, String method, List<ExceptionArgument> arguments) {
        this.type = type;
        this.method = method;
        this.arguments = arguments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<ExceptionArgument> getArguments() {
        return arguments;
    }

    public void setArguments(List<ExceptionArgument> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "AssaultException{" +
                "type='" + type + '\'' +
                ", method='" + method + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}

