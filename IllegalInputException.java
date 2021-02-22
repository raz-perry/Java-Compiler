package oop.ex6.main;

import java.io.IOException;

public class IllegalInputException extends IOException {
    public String getMessage(){
        return "Illegal input, file broken...";
    }
}
