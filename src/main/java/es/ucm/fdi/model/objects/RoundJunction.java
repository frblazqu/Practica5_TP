package es.ucm.fdi.model.objects;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import es.ucm.fdi.ini.IniSection;

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
	public void avanza()
	{
		if(numCarreterasEntrantes > 0) //No es un cruce de solo inicio 
		{
			//Primero controlar que haya alguno en verde, tenga sentido esto
			if(semaforo == -1) inicializaSemaforo();
			
			//Avanzar el primer vehículo de la cola de la carretera en verde si lo hay
			
			if(colaEnVerde() != null && colaEnVerde().size() > 0)
			{
				colaEnVerde().pop().moverASiguienteCarretera();
				ticksPasaVehiculo++;
			}
			
			//Actualizar el semáforo si procede			
			avanzarSemaforo();
		}
	}
	/**Presupone un número de carreteras entrantes no nulo.*/
	public void inicializaSemaforo()
	{
		tiempoConsumido = 0;
		ticksPasaVehiculo = 0;
		semaforo = 0;		
	}
	/**Presupone un numero de carreteras entrantes no nulo.*/
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
	public String colaCruce()
	{
		//TAL VEZ SEA SUFICIENTE CON UNA PEQUEÑA MODIFICACION
		String cola = "";
		
		for(int i = 0; i < incomingRoadIds.size(); i++)
		{
			cola += "(" + incomingRoadIds.get(i) + "," + (i == semaforo ? "green:1," : "red,") + "[" + vehiculosCola(i) + "]),";
		}
		
		if(cola.length() > 0) cola = cola.substring(0, cola.length()-1);	//Eliminamos la ',' final
		
		return cola;
	}
	
}
