package es.ucm.fdi.model;

import java.io.IOException;
import java.io.OutputStream;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.objects.Junction;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.model.objects.Vehicle;
import es.ucm.fdi.util.MultiTreeMap;

public class TrafficSimulator
{
	//ARGUMENTOS POR DEFECTO
	private static final int DEFAULT_SET_TIME = 0;
	
	//ATRIBUTOS
	private MultiTreeMap<Integer, Event> listaEventos;		//Eventos a ejecutar (ordenados por tiempo ascendente y orden de insercción)
	private RoadMap mapa;									//Contenedor para todos los objetos de la simulación (identificaros por id único)
	private int reloj;										//Reloj que indica el paso de la simulación actual
	
	/*CONSTRUCTORAS
	 public TrafficSimulator(Ini ini)
	 {
	 
	 }
	 
	 public TrafficSimulator(List<Event> eventos)
	 {
	 
	 }
	 */
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
	/**Ejecuta la simulación durante un número de pasos numTicks y escribe los datos e informes de esta en el flujo out.
	 * En cada paso de la simulación, siguiento este orden, se ejecutan los eventos de cada tiempo, se avanza en el estado 
	 * de los objetos de la simulación y se escriben los informes generados en este paso.
	 * @throws IOException */
	public void ejecuta(int numTicks, OutputStream out) throws IOException, IllegalArgumentException
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
		}
		catch(IllegalArgumentException e){
			throw new IllegalArgumentException("Something is wrong with one of the events", e);
		}

	}
	/**Escribe en el flujo de salida un informe de la situación de todos los objetos en el instante de la llamada. Escribe
	 * primero los informes de los cruces, después los de las carreteras y por último los vehículos.
	 * @throws IOException */
	public void generaInforme(OutputStream out) throws IOException
	{
		//1. Escribir el report de los cruces
		for(Junction junc: mapa.getJunctions())
			junc.escribeInforme(out, reloj);
		
		//2. Escribir el report de las carreteras
		for(Road road: mapa.getRoads())
			road.escribeInforme(out, reloj);
		
		//3. Escribir el report de los vehículos
		for(Vehicle car: mapa.getVehicles())
			car.escribeInforme(out, reloj);
	}

}
