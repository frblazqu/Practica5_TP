package es.ucm.fdi.model.objects;

//A currar a partir de aquí!

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Map;
import es.ucm.fdi.ini.IniSection;

/**
 * Representación y funcionalidad de un vehículo en el simulador.
 * 
 * @author Francisco Javier Blázquez
 * @author Manuel Ortega
 * @version 26/03/18
 */
public class Vehicle extends SimulatedObject
{
	//ATRIBUTOS
	private ArrayList<Road> itinerario;						//Carreteras que forman el itinerario del vehículo.
	private int indiceItinerario;							//itinerario(indiceItinerario) debe ser siempre la carretera actual.
	private int localizacion;								//Distancia recorrida en la carretera actual.
	private int kilometrage;								//Distancia total recorrida por el vehículo.
	private int velActual;									//Velocidad actual del vehículo (debe estar siempre actualizada!!).
	private int velMaxima;									//Velocidad máxima de un determinado vehículo.
	private int tiempoAveria;								//Ticks restantes hasta que el vehículo pueda seguir su itinerario.
	private boolean enDestino;								//True si y sólo si el vehículo está al final de la última carretera de itinerario.
	
	//CONSTRUCTORAS
	/**
	 * Constructora por defecto, genera un itinerario vacío y un identificador "" para el vehículo, no preparada para usarse.
	 */
	public Vehicle()
	{
		super();
		kilometrage = 0;
		velActual = 0;
		velMaxima = 0;
		tiempoAveria = 0;
		localizacion = 0;
		enDestino = false;
		itinerario = new ArrayList<>();
		indiceItinerario = 0;
	}
	/**
	 * Constructora usual, dada un id, velocidad máxima, mapa de objetos y listado de ids de cruces inicializa las variables del
	 * vehículo con estos datos. El itinerario se completará con las carreteras que unen dos índices consecutivos del trayecto y
	 * se lanzará una excepción si esta o uno de los cruces no existen.
	 * 
	 * @param trayecto Es la representación del itinerario como ids de los cruces por los que debe pasar el vehículo.
	 */
	public Vehicle(String id, int maxSpeed, String[] trayecto, RoadMap map)
	{
		super(id, ObjectType.VEHICLE);
		
		itinerario = new ArrayList<>();
		//Esto no debe permitir saltarse el mapa a la torera. Lanzar excepciones si no existe la carretera.
		for(int i = 1; i < trayecto.length; i++)
			itinerario.add(map.getRoad(trayecto[i-1], trayecto[i]));
		
		map.getRoad(trayecto[0], trayecto[1]).entraVehiculo(this);
		indiceItinerario = 0;
		kilometrage = 0;
		velActual = 0;
		tiempoAveria = 0;
		localizacion = 0;
		enDestino = false;		//Salvo caso patolóooogico muy patológico.
		velMaxima = maxSpeed;
	}
	/**
	 * Método realizado para facilitar el testeo de la funcionalidad. No debe ser usado.
	 * 
	 * @deprecated Solo válido para el testeo.*/
	public Vehicle(String id, int maxSpeed, Road[] trayecto)
	{
		super(id, ObjectType.VEHICLE);
		
		for(Road r: trayecto)
			itinerario.add(r);

		indiceItinerario = 0;
		kilometrage = 0;
		velActual = 0;
		tiempoAveria = 0;
		localizacion = 0;
		enDestino = false;
		velMaxima = maxSpeed;
	}
	
	//MÉTODOS
	/**
	 * Permite al vehículo continuar su itinerario, avanzando en la carretera actual, incorporándose a cruces, esperando cambios
	 * de semáforo... Todo conforme a su itinerario predefinido y teniendo en consideración posibles estados de avería.
	 */
	public void avanza(RoadMap map)
	{
		if(tiempoAveria > 0) 
		{	
			--tiempoAveria;
		} 
		else 
		{
			//itinerario.get(indiceItinerario).getLongitud() 	~ longitud de la carretera actual
			//itinerario.get(indiceItinerario).getJunctionFin() ~ cruce en el que termina la carretera actual
			
			//1. Hacemos que propiamente avance en la carretera actual
			int aux = localizacion;
			localizacion += velActual;
			
			//2. Si ha llegado al final de la carretera se introduce en el cruce de final de carretera
			if(localizacion >= itinerario.get(indiceItinerario).getLongitud())
			{
				localizacion = itinerario.get(indiceItinerario).getLongitud();
				map.getJunctionDest(actualRoad()).entraVehiculo(this);
				velActual = 0;
			}
			
			//La distancia recorrida en la carretera se suma a la distancia recorrida en total
			kilometrage += localizacion - aux;
		}
	}
	/**Permite realizar un cambio de carretera del vehículo, mueve este a su siguiente carretera determinada en el itinerario. En
	 * el caso de que se llegue al final se marca el vehículo como situado en destino. Con la disposición actual del código está 
	 * pensada para ser llamada desde un cruce del que después se eliminará el vehículo. (donde este está esperando)*/
	public void moverASiguienteCarretera()
	{
		//OJO! Por cómo lo hemos hecho no esta preparado para ser llamado nada más crear el vehículo, la constructora se encarga
		//de ponerlo en la primera carretera en su posición.
		if(indiceItinerario + 1 < itinerario.size())
		{
			//Esto es solo si vamos a tener el vehículo en la carretera y el cruce a la vez.
			//itinerario.get(indiceItinerario).saleVehiculo(this);
			
			localizacion = 0;
			velActual = 0;
			++indiceItinerario;
			itinerario.get(indiceItinerario).entraVehiculo(this);
		}
		else
		{
			enDestino = true;
			//itinerario.get(indiceItinerario).saleVehiculo(this);
		}
	}
	/**Devuelve la carretera actual en la que se encuetra el vehículo.*/
	public Road actualRoad()
	{
		//Qué pasa si está en un cruce ??
		return itinerario.get(indiceItinerario);
	}
	/**Suma el tiempo de averia al actual, no pone el tiempo de averia a este valor.*/
	public void setTiempoAveria(int tiempoAveria)
	{
		if(tiempoAveria > 0)	
		{
			this.tiempoAveria += tiempoAveria;
			setVelocidadActual(0);
		}
		else if(tiempoAveria < 0)
				throw new InvalidParameterException("Tiempo de avería negativo.");
			
	}
	/**Permite modificar la velocidad de circulación del vehículo, siempre a valores no negativos, lanza InvalidParameterException
	 * si es este el caso.*/
	public void setVelocidadActual(int nuevaVelocidad)
	{
		if(nuevaVelocidad < 0){  
			throw new InvalidParameterException("Velocidad negativa no válida.");
		}else{
			if (nuevaVelocidad <= velMaxima){
				velActual = nuevaVelocidad;
			}else{
				velActual = velMaxima;
			}
		}
	}
	/**True si el vehículo se encuentra averiado. False en caso contrario.*/
	public boolean averiado()
	{
		return tiempoAveria > 0;
	}
	/**Devuelve la distancia al origen de la carretera actual del vehículo.*/
	public int getLocalizacion() { return localizacion;}
	/**Rellena el mapa @param camposValor con los campos a reportar específicos para el vehículo.*/
	public void fillReportDetails(Map<String, String> camposValor)
	{
		camposValor.put("speed", Integer.toString(velActual));
		camposValor.put("kilometrage", Integer.toString(kilometrage));
		camposValor.put("faulty", Integer.toString(tiempoAveria));
		if(enDestino){
			camposValor.put("location", "arrived");
		}else{
		camposValor.put("location", "(" + itinerario.get(indiceItinerario).getId() + "," + Integer.toString(localizacion)  + ")");		
		}
	}
	/**Devuelve el encabezado de los informes de los vehículos. No incluye '[' '] para remarcar el encabezado.'*/
	public String getHeader()
	{
		return "vehicle_report";
	}
	public int getVelActual(){
		return velActual;
	}
	public int getVelMax(){
		return velMaxima;
	}
	public boolean getEnDestino(){
		return enDestino;
	}
	public ArrayList<Road> getItinerario(){
		return itinerario;
	}
	public int getIndIti(){
		return indiceItinerario;
	}
	public int getTiempoAveria(){
		return tiempoAveria;
	}
	public int getKilometrage()
	{
		return kilometrage;
	}
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("speed", velActual);
		s.setValue("kilometrage", kilometrage);
		s.setValue("faulty", tiempoAveria);
		s.setValue("location", localizacionString());
	}	
	public String localizacionString()
	{
		if(enDestino)
		{
			return  "arrived";
		}
		else
		{
			return "(" + itinerario.get(indiceItinerario).getId() + "," + Integer.toString(localizacion)  + ")";		
		}
		
	}
}
