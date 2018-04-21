package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;
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

	public static class NewBikeBuilder extends NewVehicleBuilder implements EventBuilder
	{
		/**
		 * Método que indica si estamos (dentro de los vehículos) en la instancia adecuada para generar a
		 * partir de esta seccion.
		 * 
		 * @param sec Sección en formato IniSection por parsear.
		 * @return true Si la sección se corresponde con un evento NewBike.
		 */
		@Override
		protected boolean esDeEsteTipo(IniSection sec)
		{
			return sec.getValue("type").equals("bike");
		}
		/**
		 * Debe terminar de parsear la sección IniSection con los atributos necesarios para generar un nuevo
		 * evento de la instancia que estemos considerando y devolver este.
		 * 
		 * @param sec La sección formato IniSection que estamos parseando.
		 * @return El evento representado por la sección.
		 */
		protected Event leerAtributosEspecificos(IniSection sec)
		{
			return new NewBike(time,id,mSpeed, it);
		}
	}
}
