package es.ucm.fdi.model.objects;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

public class Freeway extends Road
{
	private int lanes;
	
	public Freeway()
	{
		
	}
	public Freeway(String id, int maxSpeed, int size, int lanes, RoadMap map)
	{
		super(id, maxSpeed, size, map);			
		this.lanes = lanes;
	}
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("type", "lanes");
		s.setValue("state", vehiclesInRoad());
	}
	public void avanza(RoadMap mapa)
	{
		if(vehiculos.sizeOfValues() > 0)
		{
			MultiTreeMap<Integer,Vehicle> aux = new MultiTreeMap<>((a, b) -> (b-a)); 
			int velocidadBase = (int) Math.min(maxVelocidad, ((maxVelocidad*lanes)/(Math.max(1, vehiculos.sizeOfValues()))) + 1);
			int numAveriados = 0;
			
			
			for(Vehicle v: vehiculos.innerValues())
			{
				if(v.averiado())	numAveriados++;
				else if(numAveriados < lanes)
					v.setVelocidadActual(velocidadBase);
				else 
					v.setVelocidadActual(velocidadBase/2);
				
				v.avanza(mapa);
				
				if(v.getLocalizacion() != this.longitud)
				{
					aux.putValue(v.getLocalizacion(), v);
				}
			}
			vehiculos = aux;
		}
	}
}
