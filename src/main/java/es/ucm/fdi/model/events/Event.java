package es.ucm.fdi.model.events;

import es.ucm.fdi.model.objects.RoadMap;

public abstract class Event
{
	private int time;
	private EventType tipo;
	
	public Event()
	{
		time = -1;
		tipo = EventType.EVENT_ERROR;
	}
	public Event(int time, EventType tipo)
	{
		this.time = time; this.tipo = tipo;
	}
	public Integer getTime()
	{
		return time;
	}
	public abstract void execute(RoadMap mapa);
}
