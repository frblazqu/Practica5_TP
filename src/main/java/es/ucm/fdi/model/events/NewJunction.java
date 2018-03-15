package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.model.objects.Junction;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.ini.*;

public class NewJunction extends Event
{
	String junction_id;
	
	public NewJunction(){
		junction_id = null;
	}
	public NewJunction(String junctionId, int time)
	{
		super(time, EventType.NEW_JUNCTION);
		junction_id = junctionId;
	}
	public void execute(RoadMap map) throws IllegalArgumentException
	{
		if(!map.duplicatedId(junction_id)){
		Junction junc = new Junction(junction_id);
		map.addJunction(junc);
		}else{
			throw new IllegalArgumentException("The id is already used");
		}
	}
	public static class NewJunctionBuilder implements EventBuilder{
		public Event parse(IniSection sec)	throws IllegalArgumentException{
			if(!sec.getTag().equals("new_junction")){
				return null;
			}else{
				int tm	= EventBuilder.parseTime(sec.getValue("time"));
				try{
					String id = EventBuilder.parseId(sec.getValue("id"));
					return new NewJunction(id, tm);
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException("There was something wrong with one of the atributes.");
				}
			}
		}
	}
}
