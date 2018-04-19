package es.ucm.fdi.model.objects;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import es.ucm.fdi.ini.IniSection;

public class Junction extends SimulatedObject
{
	protected Map<String, ArrayDeque<Vehicle>> colas;	//Pares de Ids de carreteras entrantes, colas de vehículos esperando
	protected List<String> incomingRoadIds;				//Ids de las carreteras entrantes, para acceder rápido con el semáforo
	protected int semaforo;								//Índice dentro de IncomingRoads de la que tiene el semáforo verde
	protected int numCarreterasEntrantes;				//Número de carreteras que entran a este cruce
	protected int numVehiculos = 0;
	
	public Junction()
	{
		super();
		colas = new HashMap<>();
		incomingRoadIds = new ArrayList<>();
		numCarreterasEntrantes = 0;
		semaforo = -1;
	}
	public Junction(String id)
	{
		super(id, ObjectType.JUNCTION);
		colas = new HashMap<>();
		incomingRoadIds = new ArrayList<>();
		numCarreterasEntrantes = 0;
		semaforo = -1;
	}
	
	//MÉTODOS DE ESTADO
	public void avanza()
	{
		if(numCarreterasEntrantes > 0)
		{
			if(semaforo == -1) semaforo = 0;
			
			//Presupone que el semáforo se ha inicializado ya
			if(colas.get(incomingRoadIds.get(semaforo))!= null && colas.get(incomingRoadIds.get(semaforo)).size() > 0)
			{
				colas.get(incomingRoadIds.get(semaforo)).pop().moverASiguienteCarretera();
				numVehiculos--;
			}
		
			semaforo = (semaforo+1)%numCarreterasEntrantes;
		}
	}
	public void entraVehiculo(Vehicle car)
	{
		colas.get(car.actualRoad().getId()).addLast(car);
		numVehiculos++;
	}
	public void añadirCarreteraEntrante(Road road)
	{
		incomingRoadIds.add(road.getId());
		colas.put(road.getId(), new ArrayDeque<>());
		numCarreterasEntrantes++;
	}
	
	//MÉTODOS PARA REPRESENTAR EL ESTADO
	@Override
	public void fillReportDetails(Map<String, String> camposValor)
	{
		/* Ha caído en desuso! */
	}
	@Override
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("queues", colaCruce());
	}
	@Override
	public String getHeader()
	{
		return "junction_report";
	}
	public String colaCruce()
	{
		String cola = "";
		
		for(int i = 0; i < incomingRoadIds.size(); i++)
		{
			cola += "(" + incomingRoadIds.get(i) + "," + (i == semaforo ? "green," : "red,") + "[" + vehiculosCola(i) + "]),";
		}
		
		if(cola.length() > 0) cola = cola.substring(0, cola.length()-1);	//Eliminamos la ',' final
		
		return cola;
	}
	public String vehiculosCola(int index)
	{
		String vehiculos = "";
		
		for(Vehicle v: colas.get(incomingRoadIds.get(index)))
			vehiculos += v.getId() + ",";
		
		if(vehiculos.length() > 0) vehiculos = vehiculos.substring(0, vehiculos.length()-1);	//Eliminamos la ',' final
		
		return vehiculos;
	}
}
