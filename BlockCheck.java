package oop.ex6.main;
import static oop.ex6.main.AllRegex.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BlockCheck{

    protected final static String EMPTY = "";

    protected final static String COMMENT = "^//";
    private Pattern commentRow = Pattern.compile(COMMENT);


    protected final static String END_CODE = ";\\s*$";
    protected final static String START = "^\\s*";
    protected static String METHOD_NAME = "[a-zA-Z]" + GENERAL_NAME + "*";
    protected final static String PARENT = "\\(([^\\)]*)\\)";
//    protected final static String VAR = START + GENERAL_NAME + "\\s*" + "(=(.*))?";
    protected final static String SPACE = "\\s*";
    private final static String FINAL = "final"; // todo - make general for more modifiers
    protected final static String TYPE = "int|String|boolean|double|char";
    private final static String NEW_VAR =
            START + "((" + FINAL + ")\\s+)?(" + TYPE + ")\\s+(\\w+.*)" + END_CODE;
    private Pattern defVar = Pattern.compile(NEW_VAR);
    protected final String BLOCK_START = "\\s*\\{\\s*$";
    protected final String BLOCK_ENDS = "\\s*\\}\\s*";
    protected final static String VAR_NAME = "_" + GENERAL_NAME + "+|" + METHOD_NAME;
    protected final static String VAR = START + VAR_NAME + SPACE + "(=(.*))?";

    private final static String COMMA = ",";





    private Pattern singleVar = Pattern.compile(VAR);
    protected final int WHILE_CALL = 1;
    protected final int IF_CALL = 2;
    protected final int WRONG_CALL=0;

    protected Matcher matcher;

    //FIELD //
    protected Pattern blockStart = Pattern.compile(BLOCK_START);
    protected Pattern blockClosed = Pattern.compile(BLOCK_ENDS);
    protected HashMap<String, VariableCheck> globalVariables;
    protected HashMap<String, VariableCheck> variables;
    protected HashMap<String, MethodCheck> methods;
    protected ArrayList<String> codeLines;

    //CONSTRUCTOR//

    /**
     *cnt
     * @param codeLines
     */
    public BlockCheck(ArrayList<String> codeLines) {
        this.codeLines = codeLines;
        this.variables = new HashMap<>();
        this.methods = new HashMap<>();
        this.globalVariables = new HashMap<>();
    }

    /**
     * Checks single line that isn't an opening (method/if/while) - variable, comment or empty. otherwise
     * throw exception
     * @param codeLine String pattern of code line
     * @throws IllegalCodeException exception
     */
    public void validRow(String codeLine) throws IllegalCodeException {

        boolean a = true, b = false;

        if (a&&b){

        }
        matcher = emptyRow.matcher(codeLine);
        if (matcher.matches()){
            return;
        }
        matcher = commentRow.matcher(codeLine);
        if (matcher.find()){ // or matches?
            return;
        }
        matcher = endCodeLine.matcher(codeLine);
        if (!matcher.matches()) throw new IllegalCodeException();
        String row = matcher.group(1);
        Matcher groups = defVar.matcher(codeLine);
        // group 2 = modifier, group 3 = type, group 4 = vars
        if (groups.matches()) { // line like: "int a = 4;"
            isVariable(groups);
        }

        else{ // line like: "a = 5;"
            // todo can be a=5 and also a=b (when b init as equal type (or doublie=int)
            String equalVal = TEXT + "|[^,;]*" + "|" + VAR_NAME;
            String varName = "("+VAR_NAME+")";
            Pattern findVar =
                    Pattern.compile(SPACE + varName + SPACE + "=" + SPACE + "(" + equalVal +
                            ")" + SPACE);
            matcher = findVar.matcher(row);
            int i = 0;
            while (matcher.find()){
                if (i > 0 &&!singleComma(codeLine.substring(i, matcher.start()))){
                    throw new IllegalCodeException();
                }
                if (!mergeLists().containsKey(matcher.group(1))){
                    throw new IllegalCodeException();
                }
                String modi = mergeLists().get(matcher.group(1)).getModifier();
                if (modi != null && modi.equals(FINAL)){
                    throw new IllegalCodeException();
                }
                if (!validValue(mergeLists().get(matcher.group(1)).getType(), matcher.group(2))){
                    throw new IllegalCodeException();
                }
                i = matcher.end();
            }
            if (i == 0) throw new IllegalCodeException();
        }
    }

    private void isVariable(Matcher groups) throws IllegalCodeException{

        // todo - group 2 exist so
        boolean flag = false;
        if (groups.group(2) != null){
            flag = true;
        }
        TypeCheck typeObj =  getTypeCheck(groups.group(3));
        Pattern findType = Pattern.compile(SPACE + "(" + VAR_NAME + ")" + SPACE + "(=" + SPACE + "(" +
                typeObj.getReg() + "|" + VAR_NAME + ")" + SPACE + ")" + "?");
        matcher = findType.matcher(groups.group(4));
        int i = 0;
        while (matcher.find()){
            String a = matcher.group(1);
            String b = matcher.group(3);
            if (i > 0 && !singleComma(groups.group(4).substring(i, matcher.start()))){
                throw new IllegalCodeException();
            }
            if (variables.containsKey(matcher.group(1))) throw new IllegalCodeException();
            if (flag){
                if (matcher.group(3) == null) throw new IllegalCodeException();
            }
            if (matcher.group(3) != null  && !matcher.group(3).matches(typeObj.getReg())){
                if (variables.containsKey(matcher.group(3))){
                    if (variables.get(matcher.group(3)).isEmpty()) throw new IllegalCodeException();
                    if (!typeObj.casting(variables.get(matcher.group(3)).getType())) throw new IllegalCodeException();
                }
                else if (globalVariables.containsKey(matcher.group(3))){
                    if (globalVariables.get(matcher.group(3)).isEmpty()) throw new IllegalCodeException();
                    if (!typeObj.casting(globalVariables.get(matcher.group(3)).getType())) throw new IllegalCodeException();
                }
                else throw new IllegalCodeException();
            }
            VariableCheck var = new VariableCheck(matcher.group(1), groups.group(2), groups.group(3), variables);
            // matcher.group(3) => value
            if (matcher.group(3) != null) var.updateValue();
            variables.put(matcher.group(1), var);
            i = matcher.end();
        }
        if (!groups.group(4).substring(i).matches(SPACE)){
            throw new IllegalCodeException();
        }

    }


    private TypeCheck getTypeCheck(String value){
        for (TypeCheck typeObj : TypeCheck.values()) {
            if (typeObj.name.equals(value)){
                return typeObj;
            }
        }
        return null;
    }


    protected boolean singleComma(String str){
        return str.matches("\\s*,\\s*");
    }
    /**
     *
     * @param value
     * @return
     */
    private boolean isCondition(String value){
        matcher = booleanValue.matcher(value);
        if (matcher.matches()){
            return true;
        }
        if (variables.containsKey(value)){
            if (variables.get(value).isEmpty()) return false;
            String curType = variables.get(value).getType();
            return TypeCheck.BOOLEAN.casting(curType);
        }
        else if (globalVariables.containsKey(value)){
            if (globalVariables.get(value).isEmpty()) return false;
            String curType = globalVariables.get(value).getType();
            return TypeCheck.BOOLEAN.casting(curType);
        }
        return false;
    }

    /**
     *
     * @param value
     * @return
     */
    private boolean isOp(String value){
        return value.equals("||") || value.equals("&&");
    }

    /**
     *
     * @param codeLine
     */
    protected boolean conditionCheck(String codeLine) {
        Pattern p = Pattern.compile(START + "(.*?)" + END);
        Matcher m = p.matcher(codeLine);
        String row = codeLine;
        if (m.matches()) row = m.group(1);
        String[] cond = row.split(" ");
        if (cond.length % 2 ==0) return false;
        int i = 0;
        while (i < cond.length-1){
            if (!isCondition(cond[i])) return false;
            i++;
            if (!isOp(cond[i])) return false;
            i++;
        }
        return isCondition(cond[i]);
    }

    /**
     *
     * @param line
     * @return
     */
    protected boolean findMethodStart(String line){
        Matcher findMethodStart = blockStart.matcher(line);
        return findMethodStart.find();
    }

    /**
     *
     * @param line
     * @return
     */
    protected boolean findBlockEnd(String line){
        Matcher findBlockEnd = blockClosed.matcher(line);
        return findBlockEnd.matches();
    }

    /**
     *
     * @throws IllegalCodeException
     */
    public void isLegal() throws IllegalCodeException {
        for (int i=0; i<codeLines.size(); i++){
            if (findMethodStart(codeLines.get(i))){
                String methodCall = codeLines.get(i);
                if (checkCall(methodCall)== WRONG_CALL){
                    throw new IllegalCodeException();
                }
                ArrayList<String> blockMaker = new ArrayList<>();
                int openCounter =1;
                int closeCounter =0;
                i++;
                if (findMethodStart(codeLines.get(i))) openCounter ++;
                if (findBlockEnd(codeLines.get(i))) closeCounter ++;
                while( openCounter > closeCounter){
                    blockMaker.add(codeLines.get(i));
                    i++;
                    if (findMethodStart(codeLines.get(i))) openCounter ++;
                    if (findBlockEnd(codeLines.get(i))) closeCounter ++;
                }
                new ConditionCheck(mergeLists(),blockMaker, this.methods);
            }
            else validRow(codeLines.get(i));
        }
    }

    /**
     *
     * @param call
     * @return
     */
    protected int checkCall(String call){
        String ifPattern = START + "if" + SPACE + PARENT + SPACE + "\\{" + END;
        String whilePattern = START + "while" + SPACE + PARENT + SPACE + "\\{" + END;
        Pattern ifStatement = Pattern.compile(ifPattern);
        Pattern whileStatement = Pattern.compile(whilePattern);
        Matcher ifCheck = ifStatement.matcher(call);
        Matcher whileCheck = whileStatement.matcher(call);
        if (ifCheck.find()){
            try {
                if (conditionCheck(ifCheck.group(1))){
                    return IF_CALL;
                }
                return WRONG_CALL;
            }
            catch (IllegalStateException e){
                return IF_CALL;
            }
        }
        else if (whileCheck.find()){
            try {
                if (conditionCheck(whileCheck.group(1))){
                    return WHILE_CALL;
                }
                return WRONG_CALL;
            }
            catch (IllegalStateException e){
                return WHILE_CALL;
            }
        }
        else {
            return WRONG_CALL;
        }
    }

    /**
     *
     * @param type
     * @param value
     * @return
     */
    private boolean validValue(String type, String value){
        if (value == null){
            return true;
        }
        for (TypeCheck typeObj : TypeCheck.values()) {
            if (typeObj.name.equals(type)){
                if (variables.containsKey(value)){
                    if (variables.get(value).isEmpty()) return false;
                    return typeObj.casting(variables.get(value).getType());
                }
                if (globalVariables.containsKey(value)){
                    if (globalVariables.get(value).isEmpty()) return false;
                    return typeObj.casting(globalVariables.get(value).getType());
                }
                return typeObj.pass(value);
            }
        }
        return false;
    }


    protected HashMap<String, VariableCheck> mergeLists(){
        HashMap<String,VariableCheck> mergeLists= new HashMap<>();
        for (String varKey: this.variables.keySet()){
            mergeLists.put(varKey, variables.get(varKey));
        }
        for (String varKey: this.globalVariables.keySet()){
            if (!mergeLists.containsKey(varKey)) mergeLists.put(varKey, globalVariables.get(varKey));
        }
        return mergeLists;
    }



}
