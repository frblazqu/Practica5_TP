package es.ucm.fdi.model.objects;

import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import es.ucm.fdi.util.MultiTreeMap;

public class Junction extends SimulatedObject
{
	private Map<Road, IncomingRoad> queue;
	private List<IncomingRoad> listadoColas;
	private int indiceColas;
	
	
	public Junction()
	{
		super();
		queue = new HashMap<>();
		listadoColas = new ArrayList<>();	
		indiceColas = 0;
	}
	public Junction(String id)
	{
		super(id, ObjectType.JUNCTION);
		queue = new HashMap<>();
		listadoColas = new ArrayList<>();	
		indiceColas = 0;
	}
	public Map<Road, IncomingRoad> getMap(){
		return queue;
	}
	public List<IncomingRoad> getIncRoadList(){
		return listadoColas;
	}
	public void avanza()					
	{
		if(!listadoColas.get(indiceColas).cola.isEmpty()){
		listadoColas.get(indiceColas).cola.getFirst().moverASiguienteCarretera();
		listadoColas.get(indiceColas).cola.pop();
		}
		listadoColas.get(indiceColas).setSemaforo(false);
		indiceColas = indiceSiguiente();
		listadoColas.get(indiceColas).setSemaforo(true);
	}
	public int indiceAnterior(){
		if(indiceColas == 0){
			return listadoColas.size() - 1;
		}else{
			return indiceColas - 1;
		}
	}
	public int indiceSiguiente(){
		if(indiceColas == listadoColas.size() - 1){
			return 0;
		}else{
			return indiceColas + 1;
		}
	}
	public void entraVehiculo(Vehicle car)
	{
		queue.get(car.actualRoad()).insert(car);
	}
	public void saleVehiculo(Vehicle car)
	{
		//Presupone que siempre se elimina el primer vehículo de la cola
		queue.get(car.actualRoad()).elimina();
	}
	public void fillReportDetails(Map<String, String> camposValor)
	{
		camposValor.put("queue", colaCruce());
	}
	public String getHeader()
	{
		return "junction_report";
	}
	public String colaCruce()
	{
		String aux = "";
		
		for(Road road: queue.keySet())
		{
			aux += "(" + road.getId() + "," + queue.get(road).representaSemaforo() + "," + queue.get(road).colaCarretera() + "),";
		}
		
		if(aux.length() != 0)
		aux.substring(0, aux.length() - 1);
		
		return aux;
	}
	
	/**TAD que almacena una cola de vehículos de una carretera y una situación del semáforo (verde/rojo)*/
	public static class IncomingRoad
	{
		private boolean semaforoVerde;
		private ArrayDeque<Vehicle> cola;
		
		public IncomingRoad(){
			semaforoVerde = false;
			cola = new ArrayDeque<>();
		}
		public void insert(Vehicle car)
		{
			cola.addLast(car);
		}
		public String colaCarretera()
		{
			String aux = "[";
			
			for(Vehicle v: cola)
			{
				aux += v.getId() + ",";
			}
			
			aux.substring(0, aux.length() - 1); aux += "]";
			
			
			return aux;
		}
		public void elimina()					//OJO!!
		{
			cola.removeFirst();
		}
		public String representaSemaforo()
		{
			if(semaforoVerde)	return "green";
			else				return "red";
		}
		public void setSemaforo(Boolean bool)
		{
			semaforoVerde = bool;
		}
	}

}
