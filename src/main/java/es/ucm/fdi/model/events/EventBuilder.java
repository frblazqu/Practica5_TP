package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;
import java.lang.*;

public interface EventBuilder
{
	public Event parse(IniSection sec);
	public static String[] parseIdList(String value)	throws IllegalArgumentException{
		if(value == null){
			throw new IllegalArgumentException("The iniSection is missing a list atribute");
		}else{
			String[] ids =  value.split("\\,");
			for(int i = 0; i < ids.length; ++i){
				if(!isValidId(ids[i])){
					throw new IllegalArgumentException("The id "+ ids[i] +" is not valid.");
				}
			}
			return ids;
		}
	}
	public static boolean isValidId(String id){
		boolean valid = true;
		int i = 0;
		while(i<id.length() && valid){
			if(!Character.isLetterOrDigit(id.charAt(i)) && id.charAt(i)!='_'){
				valid=false;		
			}
			++i;
		}
		return valid;
	}
	public static String parseId(String value){
		if(value == null){
			throw new IllegalArgumentException("The id atribute is missing in the IniSection.");
		}else{
			if(isValidId(value)){
				return value;
			}else{
				throw new IllegalArgumentException("The id " + value + " is not valid.");
			}
		}
	}
	public static int parseIntValue(String value) throws IllegalArgumentException{
		if(value == null){
			throw new IllegalArgumentException("There is a missing int value in the IniSection");
		}else{
			int val = Integer.parseInt(value);
			if(val < 0){
				throw new IllegalArgumentException("The value " + value + " is not valid.");
			}else{
				return val;
			}
		}
	}
	public static int parseTime(String value){
		if (value == null){
			return 0;
		}else{
			return Integer.parseInt(value);
		}
	}
}
