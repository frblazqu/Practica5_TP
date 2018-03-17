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
	//Cruces y carreteras que los unen
	private Map<String, List<ConexionCruces>> connectedJunctions;
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
		
		connectedJunctions = new HashMap<>();
		
		junctions = new ArrayList<>();
		roads = new ArrayList<>();
		vehicles = new ArrayList<>();
		
		junctionsRO = Collections.unmodifiableList(junctions);
		roadsRO = Collections.unmodifiableList(roads);
		vehiclesRO = Collections.unmodifiableList(vehicles);
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
		vehicles.add(vehic);
		vehiclesRO = Collections.unmodifiableList(vehicles);
		
	}
	public SimulatedObject getSimulatedObject(String id){
		return simObjects.get(id);
	}
	public Vehicle getVehicle(String id)
	{
		return (Vehicle)simObjects.get(id);
	}
	public Junction getJunction(String id)
	{
		return (Junction)simObjects.get(id);
	}
	public Road getRoad(String id)
	{
		return (Road)simObjects.get(id);
	}
	public Road getRoad(String junctionIniId, String junctionFinId) throws IllegalArgumentException
	{
		if(connectedJunctions.containsKey(junctionIniId)){
			List<ConexionCruces> conexionAux = connectedJunctions.get(junctionIniId);
			for(ConexionCruces c: conexionAux){
				if(c.getJunctionDest().equals(junctionFinId)){
					return (Road)simObjects.get(c.getRoadConnect());
				}
			}
			return null;
		}else{
			return null;
		}
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
	public boolean validJuctionsForRoad(String idIni, String idDest){
		if(simObjects.containsKey(idIni) && simObjects.containsKey(idDest)){
			return true;
		}
		else if(!simObjects.containsKey(idIni)){
			throw new IllegalArgumentException("The map doesn´t contain a junction with the id " + idIni);
		}
		else{
			throw new IllegalArgumentException("The map doesn´t contain a junction with the id " + idDest);
		}
	}
	public Map<String, List<ConexionCruces>> getConectionMap(){
		return connectedJunctions;
	}
	//Par junction con carretera entrante
	public static class ConexionCruces{
		String idDest;
		String idRoad;
		public ConexionCruces(String idR, String idJ){
			idDest = idJ;
			idRoad = idR;
		}
		public String getRoadConnect(){
			return idRoad;
		}
		public String getJunctionDest(){
			return idDest;
		}
	}
}
