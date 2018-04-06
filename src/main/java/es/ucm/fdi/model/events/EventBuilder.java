package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;
import java.lang.*;

/**
 * Interfaz encargada de representar la funcionalidad necesaria para poder parsear un evento a partir de
 * una sección de tipo IniSection.
 * 
 * @author Manuel Ortega
 * @version 23/03/18
 * @see IniSection
 * @see Event
 */
public interface EventBuilder
{
	/**
	 * Parsea una sección.
	 * 
	 * @param sec sección a parsear
	 * @return Evento que representa la sección, null si no se consigue parsear.
	 */
	public Event parse(IniSection sec);
	
	//Métodos muy robustos y buenos, se puede confiar totalmente
	/**
	 * Comprueba si un id es un identificador válido o no. 
	 * 
	 * @return True si el identificador está formado por caracteres alfanuméricos o el carácter '_'.
	 */
	public static boolean isValidId(String id)
	{
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
	/**
	 * Devuelve el id resultado de parsear un valor.
	 * 
	 * @throws IllegalArgumentException si el id pasado no es válido.*/
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
	public static double parseDoubleValue(String value) throws IllegalArgumentException{
		if(value == null){
			throw new IllegalArgumentException("There is a missing double value in the IniSection");
		}else{
			double val = Double.parseDouble(value);
			if(val < 0 || val > 1){
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
			int time = Integer.parseInt(value);
			
			if(time >= 0)
				return time;
			
			else
				throw new IllegalArgumentException("The value " + time + " is not valid.");
		}
	}
}
