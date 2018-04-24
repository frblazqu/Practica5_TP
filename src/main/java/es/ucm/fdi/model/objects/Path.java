package es.ucm.fdi.model.objects;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

public class Path extends Road
{
	//CONSTRUCTORAS
	public Path() { super();}
	public Path(String id, int maxSpeed, int size, Junction junc, String ini)
	{
		super(id, maxSpeed, size, junc, ini);				
	}
	
	//MÉTODOS QUE SOBREESCRIBEN
	@Override
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("type", "dirt");
		s.setValue("state", vehiclesInRoad());
	}
	@Override
	public int velocidadAvance(int numAveriados)
	{
		return maxVelocidad/(1 + numAveriados);
	}
}
