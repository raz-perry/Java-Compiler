package oop.ex6.main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BodyCheck extends BlockCheck {

    protected final String VOID = "void";
    protected final String METHOD_VARIABLE = "\\s*\\(([^\\)]*)\\)";
    private final int LEGAL_METHOD_CALL = 1;
    private final String pattern = START + VOID +SPACE+  METHOD_NAME + METHOD_VARIABLE + BLOCK_START;
    private  Pattern methodPattern;



    public BodyCheck(ArrayList<String> codeLines) {
        super(codeLines);
        methodPattern = Pattern.compile(pattern);
    }


    /**
     * updates all methodCheck objects to know al programs variables and declared methods
     */
    public void setVariable() {
        for (BlockCheck object : methods.values()) {
            object.globalVariables = this.variables;
            object.methods = this.methods;
        }
    }


    /**
     * @throws IllegalCodeException
     */
    @Override
    public void isLegal() throws IllegalCodeException {
        for (int i = 0; i < codeLines.size(); i++) {
            if (findMethodStart(codeLines.get(i))) {
                String methodCall = codeLines.get(i);
                if (checkCall(methodCall) == WRONG_CALL) {
                    throw new IllegalCodeException();
                }
                ArrayList<String> blockMaker = new ArrayList<>();
                int openCounter = 1;
                int closeCounter = 0;
                i++;
                if (findMethodStart(codeLines.get(i))) openCounter ++;
                if (findBlockEnd(codeLines.get(i))) closeCounter ++;
                while (openCounter > closeCounter) {
                    blockMaker.add(codeLines.get(i));
                    i++;
                    if (i== codeLines.size()) throw new IllegalCodeException();
                    if (findMethodStart(codeLines.get(i))) openCounter++;
                    if (findBlockEnd(codeLines.get(i))) closeCounter++;
                }
                if (noReturn(codeLines.get(i-1))){
                    throw new IllegalCodeException();
                }
                blockMaker.remove(blockMaker.size()-1);
                HashMap<String, VariableCheck> localVariables = new HashMap<>();
                MethodCheck method = new MethodCheck(blockMaker,makeMethodVariables(methodCall, localVariables),localVariables );
                methods.put(methodNameMaking(methodCall), method);
            }
            else validRow(codeLines.get(i));
        }
        setVariable();
    }

    /**
     *
     * @param codeLine
     * @return
     */
    private boolean noReturn(String codeLine) {
        Pattern isReturn = Pattern.compile("\\s*return\\s*"+END_CODE);
        Matcher returning = isReturn.matcher(codeLine);
        return !returning.matches();
    }

    /**
     * @throws IllegalCodeException
     */
    public void checkAllMethods() throws IllegalCodeException {
        for (MethodCheck method : methods.values()) {
            method.isLegal();
        }
    }

    /**
     * @param call code line
     * @return
     */
    @Override
    protected int checkCall(String call) {
        Matcher isMethod = methodPattern.matcher(call);
        if (!isMethod.find()) {
            return WRONG_CALL;
        }
        return LEGAL_METHOD_CALL;
    }

    /**
     * checks if a parameters entered to a method variables are legal and returns a List of theme by order
     * @param call codeLine
     * @return ArrayList of parameters
     */
    private ArrayList<String> makeMethodVariables(String call, HashMap<String, VariableCheck> variables) throws IllegalCodeException {
        ArrayList<String> methodVariables = new ArrayList<>();
        Matcher getVariables = methodPattern.matcher(call);
        String tempVariables;
//       try {
           if(getVariables.find()) {
               tempVariables = getVariables.group(1);
               if (tempVariables.equals("")) return methodVariables;
               Pattern reg = Pattern.compile(START + "(" + TYPE + ")" + SPACE +"("+ VAR_NAME +")"+ SPACE + "$");
               String[] tempParameters = tempVariables.split(",");
               for (String parameter : tempParameters) {
                   Matcher m = reg.matcher(parameter);
                   if (m.find()) {
                       methodVariables.add(m.group(1));
                       VariableCheck var = new VariableCheck(m.group(2), null, m.group(1), mergeLists());
                       var.updateValue();
                       variables.put(m.group(2), var);
                   } else {
                       throw new IllegalCodeException();
                   }
               }
           }
//        }catch (IllegalStateException e){
//               return methodVariables;
//       }
        return methodVariables;
    }

    /**
     * @param methodCall
     * @return
     */
    private static String methodNameMaking(String methodCall) {
        String[] parts = methodCall.split("\\s*void\\s* ");
        return parts[1].split("\\(", 2)[0];
    }

}
//