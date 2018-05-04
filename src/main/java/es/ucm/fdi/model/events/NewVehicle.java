package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.model.objects.Vehicle;
import es.ucm.fdi.util.StringParser;

public class NewVehicle extends Event
{
	private String vehicleId;		//Id del nuevo vehículo a generar
	private int maxSpeed;			//Velocidad máxima del nuevo vehículo
	private String[] itinerary;		//Array de string de identificadores de los cruces por los que pasa el vehículo 

	public NewVehicle()
	{
		vehicleId = null;
		maxSpeed = 0;
		itinerary = null;
	}

	public NewVehicle(int time, String vId, int mSpeed, String[] it)
	{
		super(time);
		vehicleId = vId;
		maxSpeed = mSpeed;
		itinerary = it;
	}

	public String getId()
	{
		return vehicleId;
	}

	public int getmSpeed()
	{
		return maxSpeed;
	}

	public String[] getItinerary()
	{
		return itinerary;
	}

	public void execute(RoadMap map) throws IllegalArgumentException
	{
		if (map.duplicatedId(vehicleId))
			throw new IllegalArgumentException("The id " + vehicleId + " is already used");
		
		//Si estamos aquí es porque el identificador del vehículo es válido
		
		ArrayList<Road> itinerario = new ArrayList<>();
		
		for (int i = 1; i < itinerary.length; ++i)
		{
			Road road = map.getRoad(itinerary[i - 1], itinerary[i]);
			
			if (road == null)
				throw new IllegalArgumentException("There is no road that connects the specified junctions "+ 
			                                       itinerary[i - 1] + " and " + itinerary[i] + " for the itinerary.");
			else
				itinerario.add(road);
		}
				
		Vehicle vehic = new Vehicle(vehicleId, maxSpeed, itinerario);
		map.addVehicle(vehic);
	}
	
	
	//MÉTODOS SOLO PARA EL TESTEO
	@Override
	public String getTag()
	{
		return "new_vehicle";
	}
	@Override
	public void fillSectionDetails(IniSection s)
	{
		// TODO Auto-generated method stub

	}
	
	//BUILDER
	public static class NewVehicleBuilder implements EventBuilder
	{
		protected final String TAG = "new_vehicle";
		
		protected int time;
		protected String id;
		protected int mSpeed;
		protected String[] it;
		
		//MÉTODOS COMUNES A TODAS LAS INSTANCIAS
		/**
		 * Lee los atributos comunes a vehicle, car y bike. Estos son el tiempo del evento y el itinerario,
		 * máxima velocidad e identificador del vehículo.
		 * 
		 * @throws IllegalArgumentException Si no se puede parsear algún elemento de la sección.
		 */
		protected void leerAtributosComunes(IniSection sec)
		{
			time   = StringParser.parseTime(sec.getValue("time"));
			id     = StringParser.parseId(sec.getValue("id"));
			mSpeed = StringParser.parseIntValue(sec.getValue("max_speed"));
			it     = StringParser.parseIdList(sec.getValue("itinerary"));
		}
		/**
		 * Método encargado de decidir si la sección representa un evento de tipo NewVehicle o hijas y generar
		 * este en caso de que así sea.
		 * 
		 * @param sec Sección formato IniSection a parsear.
		 * @return un nuevo evento tipo NewVehicle, NewCar o NewBike si consigue parsearlo o null en caso contrario.
		 */
		public Event parse(IniSection sec) throws IllegalArgumentException
		{
			if (!sec.getTag().equals(TAG) || !esDeEsteTipo(sec))
				return null;
			   
				leerAtributosComunes(sec);
		 return leerAtributosEspecificos(sec);
		}
		
		//MÉTODOS A SOBREESCRIBIR POR LAS CLASES HIJAS
		/**
		 * Método que indica si estamos (dentro de los vehículos) en la instancia adecuada para generar a
		 * partir de esta seccion.
		 * 
		 * @param sec Sección en formato IniSection por parsear.
		 * @return true Si la sección se corresponde con un evento NewVehicle
		 */
		protected boolean esDeEsteTipo(IniSection sec)
		{
			return sec.getValue("type") == null;
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
			return new NewVehicle(time,id,mSpeed, it);
		}
	}
	
	public void describe(Map<String, String> out) {
		super.describe(out);
		out.put("Type", "New Vehicle " + vehicleId);
	}
	
}
