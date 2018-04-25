package es.ucm.fdi.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
	// ATRIBUTOS POR DEFECTO
	private static final int DEFAULT_SET_TIME = 0;

	// ATRIBUTOS
	private MultiTreeMap<Integer, Event> listaEventos; 	// Eventos a ejecutar (ordenados por tiempo ascendente y orden de insercción)
	private RoadMap mapa; 								// Contenedor para todos los objetos de la simulación (identificaros por id único)
	private int reloj; 									// Reloj que indica el paso de la simulación actual
	private List<Listener> observadores; 				// Lista de observadores a los que notificar los cambios

	/**
	 * Inicializa el objeto a una simulación vacía. Tiempo inicial nulo (0 por defecto), contenedor de objetos vacío y
	 * listado de eventos vacío.
	 */
	public TrafficSimulator()
	{
		reloj = DEFAULT_SET_TIME;
		mapa = new RoadMap();
		listaEventos = new MultiTreeMap<>();
		observadores = new ArrayList<>();
	}

	// MÉTODOS
	/** Inserta un evento en el simulador manteniendo la ordenación por tiempo y orden de insercción. */
	public void insertaEvento(Event evento)
	{
		listaEventos.putValue(evento.getTime(), evento);
		
		fireUpdateEvent(EventType.NEW_EVENT, null);
	}

	/**
	 * Ejecuta la simulación durante un número de pasos numTicks y escribe los datos e informes de esta en el flujo out.
	 * En cada paso de la simulación, siguiento este orden, se ejecutan los eventos de cada tiempo, se avanza en el
	 * estado de los objetos de la simulación y se escriben los informes generados en este paso.
	 * 
	 * @throws IllegalStateException
	 *             Si no se consigue ejecutar correctamente un evento o no se puede escribir el informe de un paso de la
	 *             simulación por el flujo de salida.
	 */
	public void ejecuta(int numTicks, OutputStream out)
	{
		try
		{
			for (int i = 0; i < numTicks; ++i)
			{
				// 1. ejecutar los eventos correspondientes a ese tiempo
				if (listaEventos.get(reloj) != null)
				{
					for (Event e : listaEventos.get(reloj))
						e.execute(mapa);
				}

				// 2. invocar al método avanzar de las carreteras
				for (Road road : mapa.getRoads())
					road.avanza(mapa);

				// 3. invocar al método avanzar de los cruces
				for (Junction junc : mapa.getJunctions())
					junc.avanza();

				// 4. this.contadorTiempo++;
				reloj++;
				
				fireUpdateEvent(EventType.ADVANCED, null);

				// 5. esciribir un informe en OutputStream
				if(out != null)
					generaInforme(out);
			}
		} catch (Exception e)
		{
			fireUpdateEvent(EventType.ERROR, "No se pudo ejecutar un evento en el tiempo " + reloj + " ticks:\n" + e.getMessage());
			throw new IllegalStateException("No se pudo ejecutar un evento en el tiempo " + reloj + " ticks.", e);
		}
	}
	/**
	 * Escribe en el flujo de salida un informe de la situación de todos los objetos en el instante de la llamada.
	 * Escribe primero los informes de los cruces, después los de las carreteras y por último los vehículos.
	 * 
	 * @param out
	 *            Flujo de salida para los datos.
	 * @throws IOException
	 *             Si no se consigue almacenar bien el informe generado.
	 */
	public void generaInforme(OutputStream out)
	{
		Ini ini = new Ini();

		// 1. Escribir el report de los cruces
		for (Junction junc : mapa.getJunctions())
			ini.addsection(junc.seccionInforme(reloj));

		// 2. Escribir el report de las carreteras
		for (Road road : mapa.getRoads())
			ini.addsection(road.seccionInforme(reloj));

		// 3. Escribir el report de los vehículos
		for (Vehicle car : mapa.getVehicles())
			ini.addsection(car.seccionInforme(reloj));

		// 4. Almacenamos el informe de este paso de la simulación.
		try
		{
			ini.store(out);
		} catch (IOException e)	{
			fireUpdateEvent(EventType.ERROR, "No se ha podido almacenar el informe del tiempo " + reloj + ".\n");
			System.err.println("No se ha podido almacenar el informe del tiempo " + reloj + ".\n");
			// throw new IOException("Almacenando el report del tiempo "+ reloj + ".\n", e);
		}
	}
	/**
	 * Carga los eventos guardados en formato ini en el fichero del inputStream y los inserta en el simulador.
	 */
	public void leerDatosSimulacion(InputStream inputStream)
	{
		try
		{
			// Cargamos todo el fichero en la variable ini (Puede lanzar IOException)
			Ini ini = new Ini(inputStream);

			// Parseamos uno a uno los eventos de las secciones
			Event evento;

			for (IniSection s : ini.getSections())
			{
				evento = EventFactory.buildEvent(s); // throw IllegalArgumentException()
				insertaEvento(evento);
			}
		} catch (IllegalArgumentException e) {
			fireUpdateEvent(EventType.ERROR, "Error al cargar uno de los eventos:\n" + e.getMessage());
			throw new IllegalStateException("Error al cargar uno de los eventos." , e);
			
		}
		catch (IOException e) {
			fireUpdateEvent(EventType.ERROR, "Error al leer el fichero de eventos.");
			System.err.println("Error al leer el fichero de eventos.");
		}
	}
	/**
	 * No modificamos los eventos que ya tenemos cargados pero volvemos al instante cero de la simulación.
	 */
	public void reset()
	{
		reloj = DEFAULT_SET_TIME;
		mapa = new RoadMap();

		fireUpdateEvent(EventType.RESET, null);
	}
	
	/**
	 * Permite añadir observadores al estado de la simulación. Al nuevo observador le pasamos el estado actual del
	 * simulador para que se inicialice con esta información inicial.
	 */
	public void addSimulatorListener(Listener listener)
	{
		observadores.add(listener);
		
		listener.update(new UpdateEvent(EventType.REGISTERED), null);
	}
	/** Permite eliminar simuladores al estado de la simulación. */
	public void removeSimulatorListener(Listener listener)
	{
		observadores.remove(listener);
	}
	/**
	 * Método para notificar a los observadores que ha ourrido un evento de un tipo determinado y mostrarles el estado
	 * del simulador. Permitiendoles reaccionar a este evento como consideren más oportuno.
	 */
	private void fireUpdateEvent(EventType type, String error)
	{
		UpdateEvent ue = new UpdateEvent(type);

		for (int i = 0; i < observadores.size(); i++)
			observadores.get(i).update(ue, error);
	}

	/* PARA MVC */
	public enum EventType
	{
		REGISTERED, RESET, NEW_EVENT, ADVANCED, ERROR;
	}

	public interface Listener
	{
		void update(UpdateEvent ue, String error);
		
		/* No es forzado que implementen estos métodos, pero esperamos que den respuesta 
		 * a estas distintas situaciones que se pueden plantear. */
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
		public EventType getType()
		{
			return tipoEvento;
		}
		public RoadMap getRoadMap()
		{
			return mapa;
		}
		public List<Event> getEvenQueue()
		{
			return listaEventos.valuesList();
		}
		public int getCurrentTime()
		{
			return reloj;
		}
	}
}
