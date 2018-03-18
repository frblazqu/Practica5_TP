package es.ucm.fdi.model.events;

import es.ucm.fdi.model.objects.Bike;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.model.objects.Vehicle;

public class NewBike extends NewVehicle{
	public NewBike(int time, String vId, int mSpeed, String[] it){
		super(time, vId, mSpeed, it);
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
			Bike bike = new Bike(this.getId(), this.getmSpeed(), this.getItinerary(), map);
			map.addVehicle(bike);
			}
		}else{
			throw new IllegalArgumentException("The id " + this.getId() +" is already used");
		}
	}
}
