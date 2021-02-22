package oop.ex6.main;

import java.io.IOException;

public class IllegalAmountOfParametersException extends IOException {
    public IllegalAmountOfParametersException() {
    }

    public String getMessage() {
        return "Illegal amount of parameters received";
    }
}
