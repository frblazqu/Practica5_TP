package es.ucm.fdi.model.events;

import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Describable;
import es.ucm.fdi.model.objects.RoadMap;

/**
 * Da un contexto común a los distintos eventos que se pueden ejecutar en el simulador.
 * 
 * @author Francisco Javier Blázquez
 * @version 23/03/18
 */
public abstract class Event implements Describable
{
	//ATRIBUTOS DE LA CLASE
	private int time;				//Momento de ejecución
	
	//CONSTRUCTORAS
	/**
	 * Constructora por defecto, NO USAR SIN PRECAUCIÓN.
	 * 
	 * @deprecated Desaconsejado su uso porque no se usa un tipo de evento definido.
	 */
	public Event()
	{
		time = -1;
	}
	public Event(int time)
	{
		this.time = time;
	}
	
	//MÉTODOS
	public Integer getTime()
	{
		return time;
	}
	/** Solo para testear. */
	public IniSection toIniSection()
	{
		IniSection s = new IniSection(getTag());
		
		s.setValue("time", "" + time);
		fillSectionDetails(s);		
	
		return s;
	}
	public void describe(Map<String, String> out) {
		out.put("Time", "" + time);
	}
	
	public abstract void execute(RoadMap mapa);
	public abstract String getTag();
	public abstract void fillSectionDetails(IniSection s);	
}
