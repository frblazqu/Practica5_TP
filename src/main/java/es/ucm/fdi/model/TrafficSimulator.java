package es.ucm.fdi.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.EventFactory;
import es.ucm.fdi.model.objects.*;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.model.objects.Vehicle;
import es.ucm.fdi.util.MultiTreeMap;

/**
 * Clase encargada del propio simulador de tráfico. 
 */
public class TrafficSimulator
{
	//ATRIBUTOS POR DEFECTO
	private static final int DEFAULT_SET_TIME = 0;
	
	//ATRIBUTOS
	private MultiTreeMap<Integer, Event> listaEventos;		//Eventos a ejecutar (ordenados por tiempo ascendente y orden de insercción)
	private RoadMap mapa;									//Contenedor para todos los objetos de la simulación (identificaros por id único)
	private int reloj;										//Reloj que indica el paso de la simulación actual
	private List<Listener> observadores;					//Lista de observadores que notificar
	
	/**Inicializa el objeto a una simulación vacía. Tiempo inicial nulo (0 por defecto), contenedor de objetos vacío 
	 * y listado de eventos vacío.*/
	public TrafficSimulator()
	{
		reloj = DEFAULT_SET_TIME;
		mapa = new RoadMap();
		listaEventos = new MultiTreeMap<>();
	}
	
	//MÉTODOS
	/**Inserta un evento en el simulador manteniendo la ordenación por tiempo y orden de insercción.*/
	public void insertaEvento(Event evento)
	{
		listaEventos.putValue(evento.getTime(), evento);
	}
	/**
	 * Ejecuta la simulación durante un número de pasos numTicks y escribe los datos e informes de esta en el flujo out.
	 * En cada paso de la simulación, siguiento este orden, se ejecutan los eventos de cada tiempo, se avanza en el estado 
	 * de los objetos de la simulación y se escriben los informes generados en este paso.
	 * 
	 * @throws IllegalStateException Si no se consigue ejecutar correctamente un evento o no se puede escribir el informe
	 * de un paso de la simulación por el flujo de salida.
	  */
	public void ejecuta(int numTicks, OutputStream out)
	{
		try{
			for(int i = 0; i<numTicks; ++i){
				// 1. ejecutar los eventos correspondientes a ese tiempo
				if(listaEventos.get(reloj) != null){
					for(Event e: listaEventos.get(reloj))
						e.execute(mapa);
				}

				// 2. invocar al método avanzar de las carreteras
				for(Road road: mapa.getRoads())
					road.avanza(mapa);
		
				// 3. invocar al método avanzar de los cruces
				for(Junction junc: mapa.getJunctions())
					junc.avanza();
		
				// 4. this.contadorTiempo++;
				reloj++;
			
				// 5. esciribir un informe en OutputStream
				generaInforme(out);
			}
		} catch(IllegalArgumentException e){
			throw new IllegalStateException("No se pudo ejecutar un evento en el tiempo " + reloj + " ticks.", e);
		}

	}
	/**
	 * Escribe en el flujo de salida un informe de la situación de todos los objetos en el instante de la llamada. Escribe
	 * primero los informes de los cruces, después los de las carreteras y por último los vehículos.
	 * 
	 * @param out Flujo de salida para los datos.
	 * @throws IOException Si no se consigue almacenar bien el informe generado.
	 * */
	public void generaInforme(OutputStream out)
	{
		Ini ini = new Ini();
		
		//1. Escribir el report de los cruces
		for(Junction junc: mapa.getJunctions())
			ini.addsection(junc.seccionInforme(reloj));
		
		//2. Escribir el report de las carreteras
		for(Road road: mapa.getRoads())
			ini.addsection(road.seccionInforme(reloj));
		
		//3. Escribir el report de los vehículos
		for(Vehicle car: mapa.getVehicles())
			ini.addsection(car.seccionInforme(reloj));
		
		//4. Almacenamos el informe de este paso de la simulación.
		try
		{
			ini.store(out);
		}
		catch(IOException e)
		{
			//throw new IOException("Almacenando el report del tiempo "+ reloj + ".\n", e);
		}
	}
	/**
	 * Carga los eventos guardados en formato ini en el fichero del inputStream y los inserta en el simulador. 
	 */
	public void leerDatosSimulacion(InputStream inputStream) throws IOException
	{
		//Cargamos todo el fichero en la variable ini (Puede lanzar IOException)
		Ini ini = new Ini(inputStream);
		
		//Parseamos uno a uno los eventos de las secciones
		Event evento;
		
		for(IniSection s: ini.getSections())
		{
			evento = EventFactory.buildEvent(s); //throw IllegalArgumentException()
			insertaEvento(evento);	
		}
		
		//fireUpdateEvent(EventType.NEW_EVENT, null);		//Notificamos que se ha añadido un evento		
		/* Esto lo podemos poner aquí y no en insertaEvento porque la única foma que contemplamos de 
		 * momento de insertar eventos el leyendo un ini de un archivo.
		 */
	}
	
	
	private void fireUpdateEvent(EventType type, String error) 
	{
		UpdateEvent ue = new UpdateEvent(type);
		
		for(int i = 0; i < observadores.size(); i++)
			observadores.get(i).update(ue, error);	
	}
	public void addSimulatorListener(Listener listener)
	{
		observadores.add(listener);
	}
	public void removeSimulatorListener(Listener listener)
	{
		observadores.remove(listener);
	}
	public void reset()
	{
		reloj = DEFAULT_SET_TIME;
		mapa = new RoadMap();
	}
		
	
	/* PARA MVC */
	public enum EventType 
	{
		REGISTERED, RESET, NEW_EVENT, ADVANCED, ERROR;
	}
	
	public interface Listener 
	{
		void update(UpdateEvent ue, String error);
		void registered(UpdateEvent ue);
		void reset(UpdateEvent ue);
		void newEvent(UpdateEvent ue);
		void advanced(UpdateEvent ue);
		void error(UpdateEvent ue, String error);
	}
	
	public class UpdateEvent 
	{
		private EventType tipoEvento;
		
		public UpdateEvent(EventType tipo)
		{
			tipoEvento = tipo;
		}
		public EventType getType() { return tipoEvento;}
		public RoadMap getRoadMap() {return mapa;}
		public List<Event> getEvenQueue() {return listaEventos.valuesList();}
		public int getCurrentTime() {return reloj;}
	}
}
