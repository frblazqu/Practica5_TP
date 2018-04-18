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

	public NewVehicle()
	{
		vehicleId = null;
		maxSpeed = 0;
		itinerary = null;
	}

	public NewVehicle(int time, String vId, int mSpeed, String[] it)
	{
		super(time, EventType.NEW_VEHICLE);
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
		if (!map.duplicatedId(vehicleId))
		{
			boolean validIds = true;
			for (int i = 1; i < itinerary.length; ++i)
			{
				if (map.getRoad(itinerary[i - 1], itinerary[i]) == null)
				{
					validIds = false;
					throw new IllegalArgumentException("There is no road that connects the specified junctions "
							+ itinerary[i - 1] + " and " + itinerary[i] + " for the itinerary.");
				}
			}
			if (validIds)
			{
				Vehicle vehic = new Vehicle(vehicleId, maxSpeed, itinerary, map);
				map.addVehicle(vehic);
			}
		} else
		{
			throw new IllegalArgumentException("The id " + vehicleId + " is already used");
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

	public static class NewVehicleBuilder implements EventBuilder
	{
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
			time   = EventBuilder.parseTime(sec.getValue("time"));
			id     = EventBuilder.parseId(sec.getValue("id"));
			mSpeed = EventBuilder.parseIntValue(sec.getValue("max_speed"));
			it     = EventBuilder.parseIdList(sec.getValue("itinerary"));
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
			if (!esDeEsteTipo(sec))
				return null;
			else 
			{			   
				leerAtributosComunes(sec);
		 return leerAtributosEspecificos(sec);
			}
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
			return sec.getTag().equals("new_vehicle") && sec.getValue("type") == null;
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
}
