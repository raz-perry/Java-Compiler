package oop.ex6.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllRegex {
	final static String START = "^\\s*";
	final static String END = "\\s*$";

	protected final static String END_CODE = ";\\s*$";
	protected static Pattern endCodeLine = Pattern.compile("^([^;]*)" + END_CODE);

	final static String TEXT = "\"[^\"]*\"";
	final static Pattern stringValue = Pattern.compile(START + TEXT + END);

	final static String INTEGER = "-?\\d+";
	final static Pattern intValue = Pattern.compile(START + INTEGER + END);

	final static String DOUBLE = INTEGER + "(\\.\\d+)?";
	final static Pattern doubleValue = Pattern.compile(START + DOUBLE + END);

	final static String BOOL = "true|false|" + INTEGER + "|" + DOUBLE;
	final static Pattern booleanValue = Pattern.compile(START + BOOL + END);

	final static String CHAR = "'.?'";
	final static Pattern charValue = Pattern.compile(START + CHAR + END);
	protected  static String GENERAL_NAME = "[a-zA-Z0-9_]";
	protected static String METHOD_NAME = "[a-zA-Z]" + GENERAL_NAME + "*";
	protected final static String PARENT = "\\(([^\\)]*)\\)";
	protected final static String SPACE = "\\s*";
	public static Pattern emptyRow = Pattern.compile(SPACE);
	protected final static String VAR_NAME = "_" + GENERAL_NAME + "+|" + METHOD_NAME;


//	final static String TYPE = typeCheck.getRegex();
}
