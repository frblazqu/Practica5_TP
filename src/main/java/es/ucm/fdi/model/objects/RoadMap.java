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

	private List<Junction> junctions;									//Listados reales
	private List<Road> roads;
	private List<Vehicle> vehicles;
	
	private List<Junction> junctionsRO;									//Listados read-only, via Collections.unmodifiableList();
	private List<Road> roadsRO;
	private List<Vehicle> vehiclesRO;
	
	//CONSTRUCTORAS
	/**Genera un RoadMap contenedor vacío, sin objetos iniciales.*/
	public RoadMap()												
	{
		simObjects = new HashMap<>();
		
		connectedJunctions = new HashMap<>();
		
		junctions = new ArrayList<>();
		roads = new ArrayList<>();
		vehicles = new ArrayList<>();
		
		junctionsRO = Collections.unmodifiableList(junctions);
		roadsRO =     Collections.unmodifiableList(roads);
		vehiclesRO =  Collections.unmodifiableList(vehicles);
	}
	
	//MÉTODOS BUENOS
	public void addObject(SimulatedObject simObject)
	{
		simObjects.put(simObject.getId(), simObject);
		
		if(simObject instanceof Vehicle)
		{
			vehicles.add((Vehicle)simObject);
			vehiclesRO = Collections.unmodifiableList(vehicles);
		}
		else if(simObject instanceof Road)
		{
			roads.add((Road)simObject);
			roadsRO = Collections.unmodifiableList(roads);
		}
		else if(simObject instanceof Junction)
		{
			junctions.add((Junction)simObject);
			junctionsRO = Collections.unmodifiableList(junctions);
		}
	}
	public SimulatedObject getSimulatedObject(String id)
	{
		return simObjects.get(id);
	}
	public boolean duplicatedId(String id)
	{
		return simObjects.containsKey(id);
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

	
	//MÉTODOS MALOS
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
	public boolean validJuctionsForRoad(String idIni, String idDest){
		if(simObjects.containsKey(idIni) && simObjects.containsKey(idDest)){
			//Solo con esta condición ya sabes que son cruces válidos ??
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
