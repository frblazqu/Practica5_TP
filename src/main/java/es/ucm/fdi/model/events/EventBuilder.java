package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;
import java.lang.*;

public interface EventBuilder
{
	public Event parse(IniSection sec);
	public static String[] parseIdList(String value){
		return value.split("\\,");
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
}
