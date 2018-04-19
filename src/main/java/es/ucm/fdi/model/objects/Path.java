package es.ucm.fdi.model.objects;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

public class Path extends Road
{
	//CONSTRUCTORAS
	public Path() { super();}
	public Path(String id, int maxSpeed, int size, Junction junc)
	{
		super(id, maxSpeed, size, junc);				
	}
	
	//MÉTODOS QUE SOBREESCRIBEN
	@Override
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("state", vehiclesInRoad());
		s.setValue("type", "dirt");
	}
	@Override
	public int velocidadAvance(int numAveriados)
	{
		return maxVelocidad/(1 + numAveriados);
	}
}
