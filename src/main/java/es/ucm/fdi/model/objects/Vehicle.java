package es.ucm.fdi.model.objects;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Describable;

/**
 * Representación y funcionalidad de un vehículo en el simulador.
 * 
 * @author Francisco Javier Blázquez
 * @author Manuel Ortega
 * @version 26/03/18
 */
public class Vehicle extends SimulatedObject implements Describable
{
	//ATRIBUTOS
	protected ArrayList<Road> itinerario;		//Carreteras que forman el itinerario del vehículo.
	protected int indiceItinerario;				//itinerario(indiceItinerario) debe ser siempre la carretera actual.
	protected int localizacion;					//Distancia recorrida en la carretera actual.
	protected int kilometrage;					//Distancia total recorrida por el vehículo.
	protected int velActual;					//Velocidad actual del vehículo (debe estar siempre actualizada!!).
	protected int velMaxima;					//Velocidad máxima de un determinado vehículo.
	protected int tiempoAveria;					//Ticks restantes hasta que el vehículo pueda seguir su itinerario.
	protected boolean enDestino;				//True si y sólo si el vehículo está al final de la última carretera de itinerario.
	
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
	 * @param id El identificador del vehículo que vamos a crear.
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
	 * Constructora usual, genera un nuevo vehículo con un itinerario concreto. ADVERTENCIA! no incorpora el vehículo a su
	 * primera carretera.
	 */
	public Vehicle(String id, int maxSpeed, List<Road> trayecto)
	{
		super(id, ObjectType.VEHICLE);
		
		itinerario = (ArrayList<Road>) trayecto;
		indiceItinerario = 0;
		kilometrage = 0;
		velActual = 0;
		tiempoAveria = 0;
		localizacion = 0;
		enDestino = false;
		velMaxima = maxSpeed;
		
		actualRoad().entraVehiculo(this);
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
		if(tiempoAveria > 0)  --tiempoAveria; 	//No avanza si está averiado
		else 
		{
			//itinerario.get(indiceItinerario) ~ carretera actual ~ acualRoad()
			
			//1. Hacemos que propiamente avance en la carretera actual
			int aux = localizacion;
			localizacion += velActual;
			
			//2. Si ha llegado al final de la carretera espera en el cruce 
			if(localizacion >= actualRoad().getLongitud())
			{
				localizacion = actualRoad().getLongitud();
				velActual = 0;
				actualRoad().getJunctionFin().entraVehiculo(this);
			}
			
			//La distancia recorrida en la carretera se suma a la distancia recorrida en total
			kilometrage += localizacion - aux;
		}
	}
	/**
	 * Permite realizar un cambio de carretera del vehículo, mueve este a su siguiente carretera determinada en el itinerario. En
	 * el caso de que se llegue al final se marca el vehículo como situado en destino. Con la disposición actual del código está 
	 * pensada para ser llamada desde un cruce del que después se eliminará el vehículo (donde este está esperando).
	 */
	public void moverASiguienteCarretera()
	{
		velActual = 0;	localizacion = 0;
		actualRoad().saleVehiculo(this);
		
		if(indiceItinerario + 1 < itinerario.size())
		{
			++indiceItinerario;
			actualRoad().entraVehiculo(this);
		}
		else
			enDestino = true;
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
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("speed", velActual);
		s.setValue("kilometrage", kilometrage);
		s.setValue("faulty", tiempoAveria);
		s.setValue("location", localizacionString());
	}	
	protected String localizacionString()
	{
		if(enDestino)	return  "arrived";
		else			return "(" + actualRoad().getId() + "," + localizacion  + ")";				
	}
	/**Devuelve el encabezado de los informes de los vehículos. No incluye '[' '] para remarcar el encabezado.'*/
	public String getHeader()
	{
		return "vehicle_report";
	}
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
	
	@Override
	public void describe(Map<String, String> out) {
		super.describe(out);
		out.put("Road", actualRoad().getId());
		out.put("Location", "" + location());
		out.put("Speed", "" + velActual);
		out.put("Km", "" + kilometrage);
		out.put("Faulty units", "" + tiempoAveria);
		out.put("Itinerary", itineraryDesc());
	}
		
	private String itineraryDesc() {
		String aux = "";
		aux += '[' + itinerario.get(0).cruceIniId;
		for(Road r: itinerario) {
			aux += ',' + r.cruceFin.getId();
		}
		aux += ']';
			
		return aux;
	}
	
	private String location(){
		if(enDestino) {
			return "arrived";
		}else{
			return "" + localizacion;
		}
	}

}
