package es.ucm.fdi.model.objects;

import java.util.Map;
import es.ucm.fdi.ini.IniSection;

/**
 * Representación y funcionalidad de un cruce avanzado en la simulación. El
 * tiempo que un semáforo está en verde se calcula para favorecer el rápido
 * tránsito en carreteras con una cola de vehículos grande.
 * 
 * @author Francisco Javier Blázquez Martínez
 * @author Manuel Ortega Salvador
 * @version 03/05/18
 */
public class CrowedJunction
		extends
			Junction {/*
						 * protected Map<String, ArrayDeque<Vehicle>> colas;
						 * //Pares de Ids de carreteras entrantes, colas de
						 * vehículos esperando protected List<String>
						 * incomingRoadIds; //Ids de las carreteras entrantes,
						 * para acceder rápido con el semáforo protected int
						 * semaforo; //Índice dentro de IncomingRoads de la que
						 * tiene el semáforo verde protected int
						 * numCarreterasEntrantes; //Número de carreteras que
						 * entran a este cruce
						 */
	private int tiempoConsumido; // Para controlar el numTicks que lleva en
									// verde el semáforo que permite el paso
	private int limiteDeTiempo; // Limite de tiempo para esta carretera para
								// este momento.

	// CONSTRUCTORAS
	/**
	 * Constructora por defecto, NO UTILIZAR SIN PRECAUCIÓN.
	 * 
	 * @deprecated Pues requiere usar la constructora por defecto de Junction.
	 */
	public CrowedJunction() {

	}
	/**
	 * Constructora usual.
	 * 
	 * @see Junction#Junction(String)
	 */
	public CrowedJunction(String id) {
		super(id);
		tiempoConsumido = 0;
		limiteDeTiempo = 1;
	}

	// MÉTODOS SOBREESCRITOS
	@Override
	public void inicializaSemaforo() {
		semaforo = numCarreterasEntrantes - 1;
		tiempoConsumido = 0;
	}
	@Override
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
	public void fillReportDetails(Map<String, String> camposValor) {
		camposValor.put("queues", colaCruce());
		camposValor.put("type", "mc");
	}
	@Override
	protected String fillColaDetails() {
		return ":" + (limiteDeTiempo - tiempoConsumido);
	}

}// CrowedJunction
