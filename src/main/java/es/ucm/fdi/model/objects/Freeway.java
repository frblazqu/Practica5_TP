package es.ucm.fdi.model.objects;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

public class Freeway extends Road
{
	private int lanes;
	
	//CONSTRUCTORAS
	public Freeway() { super();	}
	public Freeway(String id, int maxSpeed, int size, int lanes, Junction junc, String ini)
	{
		super(id, maxSpeed, size, junc, ini);			
		this.lanes = lanes;
	}
	
	//MÉTODOS QUE SOBREESCRIBEN
	@Override
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("type", "lanes");
		s.setValue("state", vehiclesInRoad());
	}
	@Override
	public int velocidadAvance(int numAveriados)
	{
		int velocidadBase = Math.min(maxVelocidad, ((maxVelocidad*lanes)/(Math.max(1, vehiculos.sizeOfValues()))) + 1);
		
		if(numAveriados < lanes)
			return velocidadBase;
		else 
			return velocidadBase/2;
	}
}
