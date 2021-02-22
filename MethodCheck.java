package oop.ex6.main;

import java.util.ArrayList;
import java.util.HashMap;


public class MethodCheck extends BlockCheck{



    private final ArrayList<String> methodParameter;
    private ObjectCheck obj;


    /**
     * creates a method object that determines if method is legal
     * @param variables HashMap of all variables known
     * @param codeLine all method code body
     * @param methodVariables all variable type that the method is allowed to except
     */
    public MethodCheck(ArrayList<String> codeLine,
                       ArrayList<String> methodVariables, HashMap<String, VariableCheck> variables){
        super(codeLine);
        this.variables = variables;
        this.methodParameter = methodVariables;
        this.obj = new ObjectCheck();
        this.globalVariables = globalVariables;
    }

    /**
     * will not return anything if line is legal and will throw an exception if line isn't
     * @param codeLine String of a line
     * @throws IllegalCodeException general program exception
     */
    @Override
    public void validRow(String codeLine) throws IllegalCodeException{
        if (!obj.specificValidRow(codeLine, this.variables,this.globalVariables, this.methods)){
            super.validRow(codeLine);
        }

    }


    public ArrayList<String> getMethodParameters(){
        return this.methodParameter;
    }

}
