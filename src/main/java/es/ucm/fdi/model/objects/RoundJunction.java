package es.ucm.fdi.model.objects;

import java.util.HashMap;
import java.util.Map;
import es.ucm.fdi.ini.IniSection;

/**
 * Representación y funcionalidad de un cruce circular en la simulación. El tiempo que un
 * semáforo está en verde es variable y favorece a las carreteras más congestionadas.
 * 
 * @author Francisco Javier Blázquez Martínez
 * @version 03/05/18
 */
public class RoundJunction extends Junction
{/*
	protected Map<String, ArrayDeque<Vehicle>> colas;	//Pares de Ids de carreteras entrantes, colas de vehículos esperando
	protected List<String> incomingRoadIds;				//Ids de las carreteras entrantes, para acceder rápido con el semáforo
	protected int semaforo;								//Índice dentro de IncomingRoads de la que tiene el semáforo verde
	protected int numCarreterasEntrantes;				//Número de carreteras que entran a este cruce */
	private int minDuration;							//Mínimo tiempo que un semáforo de cualquier carretera entrante puede estar verde
	private int maxDuration;							//Máximo tiempo que un semáforo de cualquier carretera entrante puede estar verde
	private int tiempoConsumido;						//Para controlar el numTicks que lleva en verde el semáforo que permite el paso
	private int ticksPasaVehiculo;						//Se incrementa solo si el tick con semáforo en verde no ha sido en vano
	private Map<String, Integer> intervalosVerde;		//Lleva a cada id de carretera entrante el tiempo que su semáforo estará en verde
	
	//CONSTRUCTORAS
	/**
	 * Constructora por defecto, NO UTILIZAR SIN PRECAUCIÓN.
	 * 
	 *  @deprecated Pues requiere usar la constructora por defecto de Junction.
	 */
	public RoundJunction()
	{
		
	}
	/**
	 * Crea una rotonda en la simulación en la que los semáforos tienen una duración mínima y máxima en verde.
	 * 
	 *  @see Junction#Junction(String)
	 *  @param minDurationVerde Mínima duración del semáforo en verde.
	 *  @param maxDurationVerde Máxima duración del semáforo en verde.
	 */
	public RoundJunction(String junction_id, int minDurationVerde, int maxDurationVerde)
	{
		super(junction_id);
		
		minDuration = minDurationVerde;
		maxDuration = maxDurationVerde;
		tiempoConsumido = 0;
		ticksPasaVehiculo = 0;
		intervalosVerde = new HashMap<>();
	}
	
	//MÉTODOS SOBREESCRITOS
	@Override
	public void inicializaSemaforo()
	{
		tiempoConsumido = intervalosVerde.get(incomingRoadIds.get(numCarreterasEntrantes - 1)) - 1;
		ticksPasaVehiculo = 1;
		semaforo = numCarreterasEntrantes-1;	
	}
	@Override
	public void avanzarSemaforo()
	{
		tiempoConsumido++;
		
		if(tiempoConsumido == intervalosVerde.get(incomingRoadIds.get(semaforo)))
		{
			actualizaIntervaloTiempo();
			semaforo = (semaforo+1)%numCarreterasEntrantes;
			ticksPasaVehiculo = 0;
			tiempoConsumido = 0;
		}
	}
	private void actualizaIntervaloTiempo()
	{
		int nuevoTiempo = intervalosVerde.get(incomingRoadIds.get(semaforo));
		
		if(ticksPasaVehiculo == tiempoConsumido)//Cada tick ha pasado un vehículo
		{
			nuevoTiempo = Math.min(nuevoTiempo + 1, maxDuration);
		}
		else if(ticksPasaVehiculo == 0)
		{
			nuevoTiempo = Math.max(nuevoTiempo - 1, minDuration);
		}
		
		intervalosVerde.put(incomingRoadIds.get(semaforo), nuevoTiempo);
	}
	@Override
	protected void completarAñadirCarretera(Road road)
	{
		intervalosVerde.put(road.getId(), maxDuration);
	}
	@Override
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("queues", colaCruce());
		s.setValue("type", "rr");
	}
	@Override
	public void fillReportDetails(Map<String,String> camposValor)
	{
		camposValor.put("queues", colaCruce());
		camposValor.put("type", "rr");
	}
	@Override
	protected String fillColaDetails()
	{
		return ":" + (intervalosVerde.get(incomingRoadIds.get(semaforo)) - tiempoConsumido);
	}
	
}//RoundJunction
