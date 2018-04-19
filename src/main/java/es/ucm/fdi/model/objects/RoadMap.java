package es.ucm.fdi.model.objects;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * RoadMap es la clase encargada de almacenar todos los objetos que forman parte de la simulación.
 * Permite acceder a estos por su identificador y preserva en todo momento la unicidad de este
 * 
 * @author Manuel Ortega
 * @author Francisco Javier Blázquez
 * @version 26/03/18
 * 
 */
public class RoadMap
{
	//ATRIBUTOS DE LA CLASE
	private Map<String, SimulatedObject> simObjects;					//Búsqueda por ids, unicidad
	private Map<String, List<ConexionCruces>> connectedJunctions;		//Cruces y carreteras que los unen
	private List<Junction> junctions;									//Listado de cruces
	private List<Road> roads;											//Listado de carreteras
	private List<Vehicle> vehicles;										//Listado de vehículos
	
	//CONSTRUCTORAS
	/**Genera un RoadMap contenedor vacío, sin objetos iniciales.*/
	public RoadMap()												
	{
		simObjects = new HashMap<>();
		
		connectedJunctions = new HashMap<>();
		
		junctions = new ArrayList<>();
		roads = new ArrayList<>();
		vehicles = new ArrayList<>();
	}
	
	//MÉTODOS BUENOS
	/**It doesn't matter whether the object is an vehicle, junction or a road. This method just adds it to the map.*/
	public void addObject(SimulatedObject simObject)
	{
		simObjects.put(simObject.getId(), simObject);
		
		if(simObject instanceof Vehicle)
		{
			vehicles.add((Vehicle)simObject);
		}
		else if(simObject instanceof Road)
		{
			roads.add((Road)simObject);
		}
		else if(simObject instanceof Junction)
		{
			junctions.add((Junction)simObject);
		}
	}
	/**Method to check whether an Id is already in the map or not.
	 * 
	 * @return true if the key is already used.
	 * 		   False in other case.
	 */
	public boolean duplicatedId(String id)
	{
		return simObjects.containsKey(id);
	}
	/**
	 * Method to access to the roads of the simulation.
	 * 
	 * @return All the roads stored in the simulation.
	 */
	public List<Road> getRoads()
	{
		return Collections.unmodifiableList(roads);
	}
	/**
	 * Method to access to the Junction of the simulation.
	 * 
	 * @return All the junctions stored in the simulation.
	 */
	public List<Junction> getJunctions()
	{
		return Collections.unmodifiableList(junctions);
	}
	/**
	 * Method to access to the vehicles of the simulation.
	 * 
	 * @return All the vehicles stored in the simulation.
	 */
	public List<Vehicle> getVehicles()
	{
		return Collections.unmodifiableList(vehicles);
	}
	/** Adds a junction to the map.*/
	public void addJunction(Junction junc)
	{
		simObjects.put(junc.getId(), junc);
		junctions.add(junc);
	}
	/** Adds a road to the map.*/
	public void addRoad(Road road)
	{
		simObjects.put(road.getId(), road);
		roads.add(road);
	}
	/** Adds a vehicle to the map.*/
	public void addVehicle(Vehicle vehic)
	{
		simObjects.put(vehic.getId(), vehic);
		vehicles.add(vehic);		
	}
	/**As an usual map does, this returns the mapped object or null if there is no vehicle in the map with that id.*/
	public Vehicle getVehicle(String id)
	{
		if(simObjects.get(id) instanceof Vehicle)
			return (Vehicle)simObjects.get(id);
		else
			return null;
	}
	/**As an usual map does, this returns the mapped object or null if there is no Junction in the map with that id.*/
	public Junction getJunction(String id)
	{
		if(simObjects.get(id) instanceof Junction)
			return (Junction)simObjects.get(id);
		else
			return null;
	}
	/**As an usual map does, this returns the mapped object or null if there is no Road in the map with that id.*/
	public Road getRoad(String id)
	{
		if(simObjects.get(id) instanceof Road)
			return (Road)simObjects.get(id);
		else
			return null;
	}
	
	
	//MÉTODOS COMPLICADOS	
	public Road getRoad(String junctionIniId, String junctionFinId) throws IllegalArgumentException
	{
		if(connectedJunctions.containsKey(junctionIniId)){
			List<ConexionCruces> conexionAux = connectedJunctions.get(junctionIniId);
			for(ConexionCruces c: conexionAux){
				if(c.getJunctionDest().equals(junctionFinId)){
					return (Road)simObjects.get(c.getRoadConnect());
				}
			}
			
			throw new IllegalArgumentException("No hay conexión entre el los cruces "+ junctionIniId + " y " + junctionFinId);
		}
		else
			throw new IllegalArgumentException("El cruce " + junctionIniId + " no existe o no está comunicado");
	}
	public boolean validJuctionsForRoad(String idIni, String idDest)
	{
		if(getJunction(idIni) != null && getJunction(idDest)!= null)
			return true;
		
		else if(getJunction(idIni) == null)
			throw new IllegalArgumentException("The map doesn´t contain a junction with the id " + idIni);
		
		else
			throw new IllegalArgumentException("The map doesn´t contain a junction with the id " + idDest);
	}
	public Map<String, List<ConexionCruces>> getConectionMap(){
		return connectedJunctions;
	}
	public Junction getJunctionDest(Road r){
		for(String s: connectedJunctions.keySet()){
			for(ConexionCruces c: connectedJunctions.get(s)){
				if(c.getRoadConnect().equals(r.getId())){
					return (Junction)simObjects.get(c.getJunctionDest());
				}
			}
		}
		return null;
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
