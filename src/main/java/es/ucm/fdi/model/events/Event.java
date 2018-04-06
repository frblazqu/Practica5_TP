package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.RoadMap;

/**
 * Clase abstracta encargada de dar un formato común a los eventos que actuarán posteriormente sobre el simulador.
 * Todos los eventos tendrán que implementar el método execute() alterando conforme a su función el mapa de la simulación.
 * 
 * @author Francisco Javier Blázquez
 * @version 23/03/18
 */
public abstract class Event
{
	//ATRIBUTOS DE LA CLASE
	private int time;				//Momento de ejecución
	private EventType tipo;			//Tipo de evento @deprecated
	
	//CONSTRUCTORAS
	/**deprecated Desaconsejado su uso porque no se usa un tipo de evento definido.*/
	public Event()
	{
		time = -1;
		tipo = EventType.EVENT_ERROR;
	}
	public Event(int time)
	{
		this.time = time;
	}
	/**
	 * Da valor a los campos de forma obvia.
	 * 
	 * @param time Debe ser un número entero mayor o igual que cero. Momento en el que se ejecutará el evento
	 * @param tipo Tipo del evento a construir.
	 * @see EventType
	 * */
	public Event(int time, EventType tipo)
	{
		this.time = time; this.tipo = tipo;
	}
	
	//MÉTODOS
	public Integer getTime()
	{
		return time;
	}
	public IniSection toIniSection()
	{
		IniSection s = new IniSection(getTag());
		
		s.setValue("time", "" + time);
		fillSectionDetails(s);		
	
		return s;
	}
	public abstract void execute(RoadMap mapa);
	public abstract String getTag();
	public abstract void fillSectionDetails(IniSection s);
	
	//no es necesario el javadoc en varios métodos por ser obvio.
}
