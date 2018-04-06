package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Car;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.model.objects.Vehicle;

public class NewCar extends NewVehicle{
	public static class NewCarBuilder implements EventBuilder
	{

		@Override
		public Event parse(IniSection sec)
		{
			// TODO Auto-generated method stub
			return null;
		}

	}
	private int resistance;
	private double fault_probability;
	private int max_fault_duration;
	private long seed;
	
	public NewCar(int res, double fProb, int mFDur, long s, int time, String vId, int mSpeed, String[] it){
		super(time, vId, mSpeed, it);
		resistance = res;
		fault_probability = fProb;
		max_fault_duration = mFDur;
		seed = s;
	}
	public NewCar(int res, double fProb, int mFDur, int time, String vId, int mSpeed, String[] it){
		super(time, vId, mSpeed, it);
		resistance = res;
		fault_probability = fProb;
		max_fault_duration = mFDur;
		seed = System.currentTimeMillis();
	}
	public void execute(RoadMap map)	throws IllegalArgumentException
	{
		if(!map.duplicatedId(this.getId())){
			boolean validIds = true;
			for(int i = 1; i < this.getItinerary().length; ++i){
				if(map.getRoad(this.getItinerary()[i-1], this.getItinerary()[i]) == null){
					validIds = false;
					throw new IllegalArgumentException("There is no road that connects the specified junctions " + this.getItinerary()[i-1] + " and " + this.getItinerary()[i] + " for the itinerary.");
				}
			}
			if(validIds){
				
				Car car = new Car(resistance, fault_probability, max_fault_duration, seed, this.getId(), this.getmSpeed(), this.getItinerary(), map);
				map.addVehicle(car);
			}
		}else{
			throw new IllegalArgumentException("The id " + this.getId() +" is already used");
		}
	}
}
