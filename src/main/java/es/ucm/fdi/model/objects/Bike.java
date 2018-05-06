package es.ucm.fdi.model.objects;

import java.util.Map;
import es.ucm.fdi.ini.IniSection;

public class Bike extends Vehicle
{
	/**
	 * Constructora por defecto, NO USAR SIN PRECAUCIÓN.
	 * 
	 * @deprecated Pues requiere la constructora por defecto de road.
	 */
	public Bike()
	{
		super();
	}
	/**
	 * {@link Vehicle#Vehicle(String, int, String[], RoadMap)}
	 * 
	 * @deprecated Por requerir una constructora desaconsejada de Vehicle.
	 */
	public Bike(String id, int maxSpeed, String[] trayecto, RoadMap map){
		super(id, maxSpeed, trayecto, map);
	}
	
	@Override
	public void setTiempoAveria(int tiempoAveria){
		if(velActual > velMaxima/2){
			super.setTiempoAveria(tiempoAveria);
		}
	}
	@Override
	public void fillReportDetails(Map<String, String> camposValor)
	{
		camposValor.put("speed", "" + velActual);
		camposValor.put("kilometrage", "" + kilometrage);
		camposValor.put("type", "bike");
		camposValor.put("faulty", "" + tiempoAveria);
		camposValor.put("location", localizacionString());
	}
	@Override
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("type", "bike");
		s.setValue("speed", velActual);
		s.setValue("kilometrage", kilometrage);
		s.setValue("faulty", tiempoAveria);
		s.setValue("location", localizacionString());
	}	
}
