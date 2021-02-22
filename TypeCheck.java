package oop.ex6.main;
import java.util.regex.Pattern;

import static oop.ex6.main.AllRegex.*;

public enum TypeCheck {
	STRING("String"){
		@Override
		String getReg() {
			return "\"[^\"]*\"";
		}
	},
	INT("int"){
		@Override
		String getReg() {
			return "-?\\d+";
		}
	},
	DOUBLE("double"){
		@Override
		String getReg() {
			return TypeCheck.INT.getReg() +"(\\.\\d+)?";
		}

		@Override
		boolean casting(String name) {
			return name.equals(TypeCheck.INT.name) || super.casting(name);
		}
	},
	BOOLEAN("boolean"){
		@Override
		String getReg() {
			return "true|false|" + TypeCheck.DOUBLE.getReg();
		}

		@Override
		boolean casting(String name) {
			return name.equals(TypeCheck.INT.name) || name.equals(TypeCheck.DOUBLE.name) || super.casting(name);
		}
	},
	CHAR("char"){
		@Override
		String getReg() {
			return "'.?'";
		}
	};

	String name;
	TypeCheck(String str){
		name = str;
	}

	boolean pass(String value){
		Pattern typePattern = Pattern.compile(START + getReg() + END);
		return typePattern.matcher(value).matches();
	}

	boolean casting(String name){
		return this.name.equals(name);
	}

	abstract String getReg();


}
