package oop.ex6.main;

import java.util.ArrayList;
import java.util.HashMap;

public class ConditionCheck extends BlockCheck {

    private ObjectCheck obj;

    public ConditionCheck(HashMap<String,VariableCheck> variables, ArrayList<String> codeLines, HashMap<String,
            MethodCheck> methods) throws IllegalCodeException {
        super(codeLines);
        this.globalVariables = variables;
        this.obj = new ObjectCheck();
        this.methods = methods;
        isLegal();
    }

    @Override
    public void validRow(String codeLine) throws IllegalCodeException{
        if (!obj.specificValidRow(codeLine, this.variables,this.globalVariables, this.methods)){
            super.validRow(codeLine);
        }
    }

}
