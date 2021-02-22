package oop.ex6.main;

import java.util.*;

public class VariableCheck{

	private final String name;
	private final String modifier; // change to general - many modifiers
	private final String type;
	protected HashMap<String, VariableCheck> variables;
	private boolean hasValue;

	public VariableCheck(String name, String modifier, String type, HashMap<String, VariableCheck> variables)
			throws IllegalCodeException {
		this.name = name;
		this.modifier = modifier;
		this.type = type;
		this.variables = variables;
		hasValue = false;
//		isLegal();
	}

	public String getType() {
		return type;
	}

	public String getModifier() {
		return modifier;
	}

//	private boolean nameExist(){
//		return variables.containsKey(name);
//	}

	public void updateValue(){
		hasValue = true;
	}

	public boolean isEmpty(){
		return !hasValue;
	}


//	public void isLegal() throws IllegalCodeException {
//		if (nameExist()){
//			throw new IllegalCodeException();
//		}
//		if (!validValue(type, value)){
//			throw new IllegalCodeException();
//		}
	}

