package oop.ex6.main;

import static oop.ex6.main.AllRegex.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectCheck {

    Pattern returnState = Pattern.compile("^\\s*return\\s*;\\s*$");

    public boolean specificValidRow(String line, HashMap<String, VariableCheck> variables,
                                 HashMap<String, VariableCheck> globalVariables,HashMap<String, MethodCheck> methods) throws IllegalCodeException {
        Matcher findReturn = returnState.matcher(line);
        if (findReturn.matches()) return true;
        String methodNamePattern = "(" + METHOD_NAME + ")";
        Pattern pattern = Pattern.compile(START + methodNamePattern + SPACE + PARENT + SPACE + END_CODE);
        Matcher match = pattern.matcher(line);
        if (match.find()) {
            isLegalMethodCall(match, variables, methods, globalVariables);
            return true;
        }
        return false;
    }

    /**
     * Check if a variables inserted to in the method call match any of known methods
     *
     * @param match matcher of String line of code regex
     * @throws IllegalCodeException general program exception
     */
    private void isLegalMethodCall(Matcher match, HashMap<String, VariableCheck> variables,
                                   HashMap<String, MethodCheck> methods,HashMap<String, VariableCheck> globalVariables )
            throws IllegalCodeException {
        MethodCheck other = methods.get(match.group(1)); // gets the object of demanded method call
        if (other == null) throw new IllegalCodeException();
        ArrayList<String> otherParameters = other.getMethodParameters();
        //
        String params = "";
        for (TypeCheck typeObj : TypeCheck.values()) {
            params += typeObj.getReg() + "|";
//            System.out.println(typeObj.name+": "+ typeObj.getReg());
        }
        params += VAR_NAME;
        Pattern p = Pattern.compile(params);
        Matcher m = p.matcher(match.group(2));
        int j = 0;
        ArrayList<String> methodCallVariables = new ArrayList<>();
        while (m.find()){
            if (j > 0 && !other.singleComma(match.group(2).substring(j, m.start()))){
                throw new IllegalCodeException();
            }
//            System.out.println(match.group(2).substring(m.start(), m.end()));
            String found = match.group(2).substring(m.start(), m.end());
            j = m.end();
            methodCallVariables.add(found);
        }
        if (!(methodCallVariables.size() == otherParameters.size())) {
            throw new IllegalCodeException();
        }
        //check suitability between method call and object
        for (int i = 0; i < methodCallVariables.size(); i++) {
            if(otherParameters.get(i) == null) throw new IllegalCodeException(); //TODO add description
            for (TypeCheck typeObj : TypeCheck.values()) {
                if (otherParameters.get(i).equals(typeObj.name)){
                    if (!validValue(typeObj, methodCallVariables.get(i), mergeLists(variables, globalVariables))){
                        throw new IllegalCodeException();
                    }
                    break;
                }
            }
        }
    }

    /**
     *
     * @param type
     * @param value
     * @param variables
     * @return
     */
        private boolean validValue(TypeCheck type, String value, HashMap<String, VariableCheck> variables){
            if (type.pass(value)) return true;
            if (variables.containsKey(value)){
                return type.casting(variables.get(value).getType());
            }
            return false;
        }

    private HashMap<String, VariableCheck> mergeLists(HashMap<String, VariableCheck> variables,
                                                      HashMap<String, VariableCheck> globalVariables){
        HashMap<String,VariableCheck> mergeLists= new HashMap<>();
        for (String varKey: variables.keySet()){
            mergeLists.put(varKey, variables.get(varKey));
        }
        for (String varKey: globalVariables.keySet()){
            if (!mergeLists.containsKey(varKey)) mergeLists.put(varKey, globalVariables.get(varKey));
        }
        return mergeLists;
    }

}
