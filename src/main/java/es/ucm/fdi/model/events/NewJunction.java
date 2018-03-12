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
				int tm;
				String id = sec.getValue("id");
				if(sec.getValue("time")==null){
					tm = 0;
				}else{
					tm = Integer.parseInt(sec.getValue("time"));
				}
				if(id == null){
					throw new IllegalArgumentException("Incorrect parameters");
				}else{
					if(EventBuilder.isValidId(id)){
						return new NewJunction(id, tm);
					}else{
						throw new IllegalArgumentException("Not valid values");
					}
				}
			}
		}
	}
}
