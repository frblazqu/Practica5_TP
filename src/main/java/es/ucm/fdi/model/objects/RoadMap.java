package es.ucm.fdi.model.objects;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**RoadMap debe ser la clase que gestione el almacenamiento de todos los objetos de la simulación.*/
public class RoadMap
{
	// búsqueda por ids, unicidad
	private Map<String, SimulatedObject> simObjects;
	// listados reales
	private List<Junction> junctions;
	private List<Road> roads;
	private List<Vehicle> vehicles;
	// listados read-only, via Collections.unmodifiableList();
	private List<Junction> junctionsRO;
	private List<Road> roadsRO;
	private List<Vehicle> vehiclesRO;
	
	
	public RoadMap()												//Muuuchas cosas por probar
	{
		simObjects = new HashMap<>();
		
		junctions = new ArrayList<>();
		roads = new ArrayList<>();
		vehicles = new ArrayList<>();
		
		junctionsRO = Collections.unmodifiableList(junctions);
		roadsRO = Collections.unmodifiableList(roads);
		vehicles = Collections.unmodifiableList(vehicles);
	}
	public void addJunction(Junction junc)
	{
		simObjects.put(junc.getId(), junc);
		junctions.add(junc);
		junctionsRO = Collections.unmodifiableList(junctions);
	}
	public void addRoad(Road road)
	{
		simObjects.put(road.getId(), road);
		roads.add(road);
		roadsRO = Collections.unmodifiableList(roads);
	}
	public void addVehicle(Vehicle vehic)
	{
		simObjects.put(vehic.getId(), vehic);
	}
	public Vehicle getVehicle(String id)
	{
		return null;
	}
	public Junction getJunction(String id)
	{
		return null;
	}
	public Road getRoad(String id)
	{
		
		return null;
	}
	public Road getRoad(String junctionIniId, String junctionFinId)
	{
		return null;
	}
	public List<Road> getRoads()
	{
		return roadsRO;
	}
	public List<Junction> getJunctions()
	{
		return junctionsRO;
	}
	public List<Vehicle> getVehicles()
	{
		return vehiclesRO;
	}
	public boolean duplicatedId(String id)
	{
		return simObjects.containsKey(id);
	}
}
