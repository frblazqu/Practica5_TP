package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.RoadMap;

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
	public void execute(RoadMap map)
	{
		Road road = new Road(road_id, maxSpeed, length, map);
		map.addRoad(road);
	//	map.getJunction(junctionDestId).getMap().put(road, new IncomingRoad());		Arreglar
	}
	public static class NewRoadBuilder implements EventBuilder{
		public Event parse(IniSection sec)throws IllegalArgumentException {
			if(!sec.getTag().equals("new_road")){
				return null;
			}else{
				int tm;
				String id = sec.getValue("id"), src = sec.getValue("src"), dest = sec.getValue("dest"),
						ms = sec.getValue("max_speed"), l = sec.getValue("lenght");
				if(id != null && src != null && dest != null && dest != null && ms != null && l != null){
					if(sec.getValue("time") != null){
						tm = Integer.parseInt(sec.getValue("time"));
					}else{
						tm = 0;
					}
					int mSpeed = Integer.parseInt(ms), lenght = Integer.parseInt(l);
					if(EventBuilder.isValidId(id) && EventBuilder.isValidId(src) && EventBuilder.isValidId(dest) && mSpeed >= 0 && lenght >= 0){
						return new NewRoad(tm, id, src, dest, lenght, mSpeed);
					}else{
						throw new IllegalArgumentException("Not valid values");
					}
				}else{
					throw new IllegalArgumentException("Incorrect parameters");
				}
			}
		}
	}
}
