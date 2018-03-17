package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Junction.IncomingRoad;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.model.objects.RoadMap.ConexionCruces;

public class NewRoad extends Event
{
	private String road_id;
	private String junctionIniId;
	private String junctionDestId;
	private int length;
	private int maxSpeed;
	
	public NewRoad(){
		road_id = null;
		junctionIniId = null;
		junctionDestId = null;
		length= 0;
		maxSpeed = 0;
	}
	public NewRoad(int time, String id, String iniId, String destId, int l, int mSpeed){
		super(time, EventType.NEW_ROAD);
		road_id = id;
		junctionIniId = iniId;
		junctionDestId = destId;
		length = l;
		maxSpeed = mSpeed;
	}
	
	//Tipo y parámetros de los métodos no definitivo
	//Ningún cuerpo de función implementado!!
	public void execute(RoadMap map) throws IllegalArgumentException
	{
		if(!map.duplicatedId(road_id)){
			if(map.duplicatedId(junctionDestId) && map.duplicatedId(junctionIniId)){
				Road road = new Road(road_id, maxSpeed, length, map);
				map.addRoad(road);
				map.getJunction(junctionDestId).getMap().put(road, new IncomingRoad(road));
				map.getJunction(junctionDestId).getIncRoadList().add(new IncomingRoad(road));
				ConexionCruces conJunct = new ConexionCruces(road_id, junctionDestId);
				if(map.getConectionMap().containsKey(junctionIniId)){
					map.getConectionMap().get(junctionIniId).add(conJunct);
				}else{
					List<ConexionCruces> connect = new ArrayList<ConexionCruces>();
					connect.add(conJunct);
					map.getConectionMap().put(junctionIniId, connect);
				}
			}else{
				throw new IllegalArgumentException("There is no junction with the specified id");
			}
		}else{
			throw new IllegalArgumentException("The id is already used");
		}
	}
	public static class NewRoadBuilder implements EventBuilder{
		public Event parse(IniSection sec)throws IllegalArgumentException {
			if(!sec.getTag().equals("new_road")){
				return null;
			}else{
				int tm = EventBuilder.parseTime(sec.getValue("time"));
				try{
					String id = EventBuilder.parseId(sec.getValue("id"));
					String src = EventBuilder.parseId(sec.getValue("src"));
					String dest = EventBuilder.parseId(sec.getValue("dest"));
					int mSpeed = EventBuilder.parseIntValue(sec.getValue("max_speed"));
					int l = EventBuilder.parseIntValue(sec.getValue("length"));
					return new NewRoad(tm, id, src, dest, l, mSpeed);
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException("There is something wrong with one of the atributes.");
				}
			}
		}
	}
}