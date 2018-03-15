package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.model.objects.Vehicle;

public class NewVehicle extends Event
{
	private String vehicleId;
	private int maxSpeed;
	private String[] itinerary;	
	
	public NewVehicle(){
		vehicleId = null;
		maxSpeed = 0;
		itinerary = null;
	}
	public NewVehicle(int time, String vId, int mSpeed, String[] it){
		super(time, EventType.NEW_VEHICLE);
		vehicleId = vId;
		maxSpeed = mSpeed;
		itinerary = it;
	}
	

	public void execute(RoadMap map)	throws IllegalArgumentException
	{
		if(!map.duplicatedId(vehicleId)){
			boolean validIds = true;
			for(int i = 0; i < itinerary.length; ++i){
				if(!map.duplicatedId(itinerary[i])){
					validIds = false;
					break;
				}
			}
			if(validIds){
			Vehicle vehic = new Vehicle(vehicleId, maxSpeed, itinerary, map);
			map.addVehicle(vehic);
			}else{
				throw new IllegalArgumentException("There is no junction with the specified id");
			}
		}else{
			throw new IllegalArgumentException("The id is already used");
		}
	}
	
	public static class NewVehicleBuilder implements EventBuilder {
		public Event parse(IniSection sec)	throws IllegalArgumentException{
			if (!sec.getTag().equals("new_vehicle")){
				return null;
			}else{
				int tm = EventBuilder.parseTime(sec.getValue("time"));
				try{
					String id = EventBuilder.parseId(sec.getValue("id"));
					int mSpeed = EventBuilder.parseIntValue(sec.getValue("max_speed"));
					String[] it = EventBuilder.parseIdList(sec.getValue("itinerary"));
					return new NewVehicle(tm, id, mSpeed, it);
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException("There was something wrong with one of the atributes");
				}
			}
		}
	}
}
