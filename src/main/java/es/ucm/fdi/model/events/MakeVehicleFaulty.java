package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.RoadMap;

public class MakeVehicleFaulty extends Event
{
	private int faultDuration;
	private String[] vehicleId;
	
	public MakeVehicleFaulty(){
		faultDuration = 0;
		vehicleId = null;
	}
	public MakeVehicleFaulty(int time, int faultDur, String[] vId){
		super(time, EventType.NEW_FAULT);
		faultDuration = faultDur;
		vehicleId = vId;
	}
	
	public void execute(RoadMap map)	throws IllegalArgumentException
	{
		boolean validIds = true;
		for(int i = 0; i < vehicleId.length; ++i){
			if(!map.duplicatedId(vehicleId[i])){
				validIds = false;
				break;
			}
		}
		if(validIds){
			for(int i=0; i<vehicleId.length; ++i){
				map.getVehicle(vehicleId[i]).setTiempoAveria(faultDuration);
			}
		}else{
			throw new IllegalArgumentException("There is no vehicle with the specified id");
		}
	}
	public static class NewVehicleFaulty implements EventBuilder{
		public Event parse(IniSection sec)	throws IllegalArgumentException{
			if (!sec.getTag().equals("make_vehicle_faulty")){
				return null;
			}else{
				int tm;
				String vehic = sec.getValue("vehicles"), dur = sec.getValue("duration");
				if(vehic != null && dur != null){
					if(sec.getValue("time") != null){
						tm = Integer.parseInt(sec.getValue("time"));
					}else{
						tm = 0;
					}
					String[] vehicles = EventBuilder.parseIdList(vehic);
					int duration = Integer.parseInt(dur);
					boolean validIdList =  true;
					for(int i = 0; i < vehicles.length; ++i){
						if(!EventBuilder.isValidId(vehicles[i])){
							validIdList = false;
							break;
						}
						++i;
					}
					if(validIdList && duration >= 0){
						return new MakeVehicleFaulty(tm, duration, vehicles);
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
