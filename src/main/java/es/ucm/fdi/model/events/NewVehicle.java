package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Car;
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
	
	public String getId(){
		return vehicleId;
	}
	public int getmSpeed(){
		return maxSpeed;
	}
	public String[] getItinerary(){
		return itinerary;
	}
	public void execute(RoadMap map)	throws IllegalArgumentException
	{
		if(!map.duplicatedId(vehicleId)){
			boolean validIds = true;
			for(int i = 1; i < itinerary.length; ++i){
				if(map.getRoad(itinerary[i-1], itinerary[i]) == null){
					validIds = false;
					throw new IllegalArgumentException("There is no road that connects the specified junctions " + itinerary[i-1] + " and " + itinerary[i] + " for the itinerary.");
				}
			}
			if(validIds){
			Vehicle vehic = new Vehicle(vehicleId, maxSpeed, itinerary, map);
			map.addVehicle(vehic);
			}
		}else{
			throw new IllegalArgumentException("The id " + vehicleId +" is already used");
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
					if(sec.getValue("type") != null){
						if(sec.getValue("type").equals("car")){
							int res = EventBuilder.parseIntValue(sec.getValue("resistance"));
							double fProb = EventBuilder.parseDoubleValue(sec.getValue("fault_probability"));
							int mFDur = EventBuilder.parseIntValue(sec.getValue("max_fault_duration"));
							if(sec.getValue("seed") != null){
								long seed = Long.parseLong(sec.getValue("seed"));
								return new NewCar(res, fProb, mFDur, seed, tm, id, mSpeed, it);
							}else{
								return new NewCar(res, fProb, mFDur, tm, id, mSpeed, it);
							}
						}else if(sec.getValue("type").equals("bike")){
							return new NewBike(tm, id, mSpeed, it);
						}else{
							throw new IllegalArgumentException("The type of vehicle " + sec.getValue("type") + "isn`t a valid type");
						}
					}else{
						return new NewVehicle(tm, id, mSpeed, it);
					}
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException("There was something wrong with one of the atributes", e);
				}
			}
		}
	}
}
