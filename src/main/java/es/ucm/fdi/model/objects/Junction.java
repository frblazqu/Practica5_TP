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
	
	//CONSTRUCTORAS
	/**
	 * Constructora por defecto. NO DEBE USARSE SIN PRECAUCIÓN.
	 * 
	 * @deprecated Pues no inicializa el identificador ni el semáforo a un valor correcto.
	 */
	public Junction()
	{
		super();
		colas = new HashMap<>();
		incomingRoadIds = new ArrayList<>();
		numCarreterasEntrantes = 0;
		semaforo = -1;
	}
	/**
	 * Constructora usual. Genera un nuevo cruce dado:
	 * 
	 * @param id Identificador del nuevo cruce a crear.
	 */
	public Junction(String id)
	{
		super(id);
		colas = new HashMap<>();
		incomingRoadIds = new ArrayList<>();
		numCarreterasEntrantes = 0;
		semaforo = -1;
	}
	
	//FUNCIONALIDAD
	/**
	 * Avanza el estado del cruce en la simulación, esto es, permite avanzar a su siguiente carretera al
	 * vehículo que más lleva esperando en la carretera con el semáforo en verde.
	 */
	public void avanza()
	{
		if(numCarreterasEntrantes > 0) //No es un cruce de solo inicio 
		{
			//Primero controlar que haya alguno en verde, tenga sentido esto
			if(semaforo == -1) 	inicializaSemaforo();
			
			//Avanzar el primer vehículo de la cola de la carretera en verde si lo hay
			
			if(colaEnVerde() != null && colaEnVerde().size() > 0)
			{
				colaEnVerde().pop().moverASiguienteCarretera();
			}
			
			//Actualizar el semáforo si procede			
			avanzarSemaforo();
		}
	}
	/**
	 * Establece por primera vez permiso de circulación a una de las carreteras entrantes.
	 */
	protected void inicializaSemaforo()
	{
		semaforo = numCarreterasEntrantes-1;
	}
	/**
	 * Avanza el estado del semáforo del cruce. Cambiando de carretera en verde si fuera necesario. 
	 */
	protected void avanzarSemaforo()
	{
		semaforo = (semaforo+1)%numCarreterasEntrantes;
	}
	/**
	 * @return Los vehículos que se encuentran esperando al final de la carretera con semáforo en verde.
	 */
	protected ArrayDeque<Vehicle> colaEnVerde()
	{
		return colas.get(incomingRoadIds.get(semaforo));
	}
	/**
	 * Inserta el vehículo pasado como parámetro al final de la cola de espera de su carretera.
	 * 
	 * @param car Vehículo que pasa a esperar en la cola final de la carretera en la que se encuentra.
	 */
	public void entraVehiculo(Vehicle car) 
	{
		colas.get(car.actualRoad().getId()).addLast(car);
		car.setVelocidadActual(0);
	}
	/**
	 * Añade una carretera nueva que finaliza en el cruce actual.
	 * 
	 * @param road Carretera entrante que vamos a añadir.
	 */
	public void añadirCarreteraEntrante(Road road)
	{
		incomingRoadIds.add(road.getId());
		colas.put(road.getId(), new ArrayDeque<>());
		numCarreterasEntrantes++;
		
		completarAñadirCarretera(road);
	}
	/**
	 * Método para ser sobreescrito en los cruces avanzados para ejecutar sus funciones específicas.
	 * 
	 * @param road Carretera que se añade como entrante al cruce.
	 */
	protected void completarAñadirCarretera(Road road)
	{
		;
	}
	
	//INFORMES Y TABLAS
	/** @return "junction_report" como encabezado por defecto para los informes de los cruces. */
	public String getHeader()
	{
		return "junction_report";
	}
	/**
	 * Completa los detalles específicos de este cruce en el mapa pasado como parámetro.
	 *  
	 * @param camposValor Mapa en el que se introducirá la información.
	 */
	public void fillReportDetails(Map<String, String> camposValor)
	{
		/* Ha caído en desuso! */
	}
	/** 
	 * Completa los detalles específicos de este cruce en la sección pasada como parámetro. 
	 * 
	 * @param sec Sección en la que insertar la información.
	 */
	public void fillSectionDetails(IniSection sec)
	{
		sec.setValue("queues", colaCruce());
	}
	/** @return La representación textual de las colas de espera del cruce que se visualiza en los informes. */
	protected String colaCruce()
	{
		String cola = "";
		
		for(int i = 0; i < incomingRoadIds.size(); i++)
		{
			cola += "(" + incomingRoadIds.get(i) + "," + (i == semaforo ? "green" + fillColaDetails() : "red") + ",[" + vehiculosCola(i) + "]),";
		}
		
		if(cola.length() > 0) cola = cola.substring(0, cola.length()-1);	//Eliminamos la ',' final
		
		return cola;
	}
	protected String fillColaDetails()
	{
		return "";
	}
	/** @return La representación textual de una cola de vehículos esperando en el cruce. */
	protected String vehiculosCola(int index)
	{
		String vehiculos = "";
		
		for(Vehicle v: colas.get(incomingRoadIds.get(index)))
			vehiculos += v.getId() + ",";
		
		if(vehiculos.length() > 0) vehiculos = vehiculos.substring(0, vehiculos.length()-1);	//Eliminamos la ',' final
		
		return vehiculos;
	}
	
	public void describe(Map<String, String> out) {
		super.describe(out);
		out.put("Green", estadoVerde());
		out.put("Red", estadoRojo());
	}
	private String estadoVerde() {
		String aux = "";
		aux += "[";
		if(semaforo != -1){
		aux += "(" + incomingRoadIds.get(semaforo) + ",green," + "[" + vehiculosCola(semaforo) + ")]";
		}
		aux += "]";
		
		return aux;
	}
	private String estadoRojo() {
		String aux = "";
		aux += "[";
		for(int i = 0; i < semaforo; ++i) {
			aux += "(" + incomingRoadIds.get(i) + ",red," + "[" + vehiculosCola(i) + "]" + "),";
		}
		for(int i = semaforo + 1; i < incomingRoadIds.size(); ++i) {
			aux += "(" + incomingRoadIds.get(i) + ",red," + "[" + vehiculosCola(i) + "]" +  "),";
		}
		if (aux.length() > 1)	aux = aux.substring(0, aux.length()-1);
		aux += "]";
			
		return aux;
			
	}
		
}//Junction
