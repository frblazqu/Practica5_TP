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
				int tm;
				String id = sec.getValue("id"), ms = sec.getValue("max_speed"), it = sec.getValue("itinerary");
				if(id != null && ms != null && it != null){
					if(sec.getValue("time") != null){
						tm = Integer.parseInt(sec.getValue("time"));
					}else{
						tm = 0;
					}
					int mSpeed = Integer.parseInt(ms);
					String[] itiner = EventBuilder.parseIdList(it);
					boolean validListId = true;
					for(int i = 0; i < itiner.length; ++i){
						if(!EventBuilder.isValidId(itiner[i])){
							validListId = false;
							break;
						}
						++i;
					}
					if(EventBuilder.isValidId(id) && mSpeed>=0 && validListId){
						return new NewVehicle(tm, id, mSpeed, itiner);
					}else{
						throw new IllegalArgumentException("Not valid values");
					}
				}else{
					throw new IllegalArgumentException("Invalid parameters");
				}
			}
		}
	}
}
