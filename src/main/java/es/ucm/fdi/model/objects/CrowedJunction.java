package es.ucm.fdi.model.objects;

import java.util.Map;

import es.ucm.fdi.ini.IniSection;

public class CrowedJunction extends Junction {/*
											 * protected Map<String,
											 * ArrayDeque<Vehicle>> colas;
											 * //Pares de Ids de carreteras
											 * entrantes, colas de vehículos
											 * esperando protected List<String>
											 * incomingRoadIds; //Ids de las
											 * carreteras entrantes, para
											 * acceder rápido con el semáforo
											 * protected int semaforo; //Índice
											 * dentro de IncomingRoads de la que
											 * tiene el semáforo verde protected
											 * int numCarreterasEntrantes;
											 * //Número de carreteras que entran
											 * a este cruce
											 */
	private int tiempoConsumido; // Para controlar el numTicks que lleva en
									// verde el semáforo que permite el paso
	private int limiteDeTiempo; // Limite de tiempo para esta carretera para
								// este momento.

	public CrowedJunction(String id) {
		super(id);
		tiempoConsumido = 0;
		limiteDeTiempo = 1;
	}

	public void avanza() {
		if (numCarreterasEntrantes > 0) // No es un cruce de solo inicio
		{
			// Primero controlar que haya alguno en verde, tenga sentido esto
			if (semaforo == -1)
				inicializaSemaforo();

			// Avanzar el primer vehículo de la cola de la carretera en verde si
			// lo hay

			if (colaEnVerde() != null && colaEnVerde().size() > 0) {
				colaEnVerde().pop().moverASiguienteCarretera();
			}

			// Actualizar el semáforo si procede
			avanzarSemaforo();
		}
	}
	/** Presupone un número de carreteras entrantes no nulo. */
	public void inicializaSemaforo() {
		semaforo = numCarreterasEntrantes - 1;
		tiempoConsumido = 0;
	}
	/** Presupone un numero de carreteras entrantes no nulo. */
	public void avanzarSemaforo() {
		tiempoConsumido++;

		if (tiempoConsumido == limiteDeTiempo) // Hay que hacer transición
		{
			int maximo = -1;
			int indiceMax = 0;

			for (int i = 0; i < incomingRoadIds.size(); i++)
				if (colas.get(incomingRoadIds.get(i)).size() > maximo) {
					maximo = colas.get(incomingRoadIds.get(i)).size();
					indiceMax = i;
				}

			if (maximo != 0)
				semaforo = indiceMax;

			else
				semaforo = (semaforo + 1) % numCarreterasEntrantes;

			tiempoConsumido = 0;
			limiteDeTiempo = Math.max(maximo / 2, 1);

		}
	}
	@Override
	public void fillSectionDetails(IniSection s) {
		s.setValue("queues", colaCruce());
		s.setValue("type", "mc");
	}
	@Override
	public String colaCruce() {
		// TAL VEZ SEA SUFICIENTE CON UNA PEQUEÑA MODIFICACION
		String cola = "";
		int aux = (limiteDeTiempo - tiempoConsumido);

		for (int i = 0; i < incomingRoadIds.size(); i++) {
			cola += "(" + incomingRoadIds.get(i) + ","
					+ (i == semaforo ? "green:" + aux : "red") + ",[" + vehiculosCola(i)
					+ "]),";
		}

		if (cola.length() > 0)
			cola = cola.substring(0, cola.length() - 1); // Eliminamos la ','
															// final

		return cola;
	}

	@Override
	public String estadoVerde() {

		String aux = "";
		int timeLeft = limiteDeTiempo - tiempoConsumido;
		aux += "[";
		if (semaforo != -1) {
			aux += "(" + incomingRoadIds.get(semaforo) + ",green:" + timeLeft + ',' + "["
					+ vehiculosCola(semaforo) + ']' + ")]";
		}
		aux += "]";

		return aux;
	}

}
