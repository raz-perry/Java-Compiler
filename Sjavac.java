package oop.ex6.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Sjavac {

    /**
     *
     * @throws IOException
     */
    private static ArrayList<String> readAll(File codeBody) throws IOException {
        String line;
        ArrayList<String> codeLines= new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(codeBody))){
            while ((line = reader.readLine()) != null){
                codeLines.add(line);
            }
        }
        catch (IOException e){
            throw new IllegalInputException();
        }
        return codeLines;
    }
    public static void main(String[] args) {
        try{
            if (args.length !=1){
                throw new IOException();
            }
            BodyCheck allCode = new BodyCheck(readAll(new File(args[0])));
            allCode.isLegal();
            allCode.checkAllMethods();
            System.out.println(0);
        }
        catch(IllegalCodeException e){
            System.out.println(1);
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
    }
}
