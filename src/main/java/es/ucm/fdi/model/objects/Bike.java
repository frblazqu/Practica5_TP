package es.ucm.fdi.model.objects;

import java.util.Map;
import es.ucm.fdi.ini.IniSection;

public class Bike extends Vehicle{
	public Bike(String id, int maxSpeed, String[] trayecto, RoadMap map){
		super(id, maxSpeed, trayecto, map);
	}
	public void setTiempoAveria(int tiempoAveria){
		if(this.getVelActual()>this.getVelMax()/2){
			super.setTiempoAveria(tiempoAveria);
		}
	}
	public void fillReportDetails(Map<String, String> camposValor)
	{
		camposValor.put("speed", Integer.toString(this.getVelActual()));
		camposValor.put("kilometrage", Integer.toString(this.getKilometrage()));
		camposValor.put("type", "bike");
		camposValor.put("faulty", Integer.toString(this.getTiempoAveria()));
		if(this.getEnDestino()){
			camposValor.put("location", "arrived");
		}else{
		camposValor.put("location", "(" + this.getItinerario().get(this.getIndIti()).getId() + "," + Integer.toString(this.getLocalizacion())  + ")");		
		}
	}
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("type", "bike");
		s.setValue("speed", this.getVelActual());
		s.setValue("kilometrage", this.getKilometrage());
		s.setValue("faulty", this.getTiempoAveria());
		s.setValue("location", localizacionString());
	}	
}
