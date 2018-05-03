package es.ucm.fdi.model.events;

import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Describable;
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
		super(time);
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
				int tm = EventBuilder.parseTime(sec.getValue("time"));
				try{
					String[] vehic = EventBuilder.parseIdList(sec.getValue("vehicles"));
					int dur = EventBuilder.parseIntValue(sec.getValue("duration"));
					return new MakeVehicleFaulty(tm, dur, vehic);
				}
				catch(IllegalArgumentException e){
					throw new IllegalArgumentException("There is something wrong with one of the atributes.", e);
				}
			}
		}
	}
	@Override
	public String getTag()
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void fillSectionDetails(IniSection s)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void describe(Map<String, String> out) {
		super.describe(out);
		out.put("Type", "Break Vehicles " + vehiclesForFaultyDesc());
	}
		
	private String vehiclesForFaultyDesc() {
			
		String aux = "";
		aux += "[";
		for(int i = 0; i < vehicleId.length; ++i) {
			aux += vehicleId[i] + ",";
		}
		aux = aux.substring(0, aux.length()-1);
		aux += "]";
			
		return aux;
	}
	
}
