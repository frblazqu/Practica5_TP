package es.ucm.fdi.model.objects;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;

/**
 * Representación y funcionalidad de un vehículo básico en el simulador.
 * 
 * @author Francisco Javier Blázquez
 * @author Manuel Ortega
 * @version 02/05/18
 */
public class Vehicle extends SimulatedObject {
	protected ArrayList<Road> itinerario; // Carreteras que forman el itinerario
											// del vehículo.
	protected int indiceItinerario; // itinerario(indiceItinerario) debe ser
									// siempre la carretera actual.
	protected int localizacion; // Distancia recorrida en la carretera actual.
	protected int kilometrage; // Distancia total recorrida por el vehículo.
	protected int velActual; // Velocidad actual del vehículo (debe estar
								// siempre actualizada!!).
	protected int velMaxima; // Velocidad máxima de un determinado vehículo.
	protected int tiempoAveria; // Ticks restantes hasta que el vehículo pueda
								// seguir su itinerario.
	protected boolean enDestino; // True si y sólo si el vehículo está al final
									// de la última carretera de itinerario.

	// CONSTRUCTORAS
	/**
	 * Constructora por defecto. NO DEBE USARSE SIN PRECAUCIÓN.
	 * 
	 * @deprecated Pues los inicializa sus atributos a parámetros no válidos
	 *             para ser ejecutados en el simulador.
	 */
	public Vehicle() {
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
	 * Genera un vehículo dados los siguientes parámetros:
	 * 
	 * @param id
	 *            Identificador del vehículo a crear (debe ser alfanumérico).
	 * @param maxSpeed
	 *            Velocidad máxima del vehículo a crear.
	 * @param trayecto
	 *            Identificadores de los cruces que el vehículo debe atravesar
	 *            por orden.
	 * @param map
	 *            Mapa de carreteras/cruces/vehículos del simulador.
	 * 
	 * @deprecated Pues requiere del mapa entero para generar el vehículo y
	 *             aumenta el acoplamiento de todo nuestro código.
	 */
	public Vehicle(String id, int maxSpeed, String[] trayecto, RoadMap map) {
		super(id);

		itinerario = new ArrayList<>();
		// Esto no debe permitir saltarse el mapa a la torera. Lanzar
		// excepciones si no existe la carretera.
		for (int i = 1; i < trayecto.length; i++)
			itinerario.add(map.getRoad(trayecto[i - 1], trayecto[i]));

		map.getRoad(trayecto[0], trayecto[1]).entraVehiculo(this);
		indiceItinerario = 0;
		kilometrage = 0;
		velActual = 0;
		tiempoAveria = 0;
		localizacion = 0;
		enDestino = false; // Salvo caso patolóooogico muy patológico.
		velMaxima = maxSpeed;
	}
	/**
	 * Genera un vehículo dados los siguientes parámetros:
	 * 
	 * @param id
	 *            Identificador del vehículo a crear (debe ser alfanumérico).
	 * @param maxSpeed
	 *            Velocidad máxima del vehículo a crear.
	 * @param trayecto
	 *            Lista ordenada de carreteras que el vehículo debe atravesar
	 *            por orden.
	 */
	public Vehicle(String id, int maxSpeed, List<Road> trayecto) {
		super(id);

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

	// FUNCIONALIDAD
	/**
	 * Hace avanzar al vehículo, esto es, actualiza sus atributos para que
	 * reflejen fielmente la nueva posición que pasa a ocupar en la carretera.
	 */
	protected void avanza() {
		if (tiempoAveria > 0)
			--tiempoAveria;
		// No avanzamos su posición en la carretera si está averiado
		else {
			// 1. Hacemos que propiamente avance en la carretera actual
			int aux = localizacion;
			localizacion += velActual;

			// 2. Si ha llegado al final de la carretera espera en el cruce
			if (localizacion >= actualRoad().getLongitud()) {
				localizacion = actualRoad().getLongitud();
				velActual = 0;
				actualRoad().getJunctionFin().entraVehiculo(this);
			}

			// 3. La distancia recorrida en la carretera se suma a la distancia
			// recorrida en total
			kilometrage += localizacion - aux;
		}
	}
	/**
	 * Mueve el vehículo a la siguiente carretera de su itinerario o marca este
	 * como situado en destino si no hay ninguna carretera siguiente
	 * especificada.
	 * 
	 * @throws IllegalStateException
	 *             Si el vehículo no se encuentra al final de la carretera o la
	 *             que debería ser su carretera actual no lo contiene.
	 */
	public void moverASiguienteCarretera() {
		if (!actualRoad().saleVehiculo(this))
			throw new IllegalStateException(
					"No se ha podido mover a la siguiente carretera el vehículo " + id
							+ ".");

		// Se pasa a la siguiente o se marca en destino dependiendo de si ha
		// llegado al final o no.
		velActual = 0;
		localizacion = 0;

		if (indiceItinerario + 1 < itinerario.size()) {
			++indiceItinerario;
			actualRoad().entraVehiculo(this);
		} else
			enDestino = true;
	}
	/**
	 * @return La carretera en la que se encuentra el vehículo. Si esté se
	 *         encuentra en un cruce esperando se devuelve la carretera por la
	 *         que el vehículo ha llegado hasta ese cruce.
	 */
	public Road actualRoad() {
		return itinerario.get(indiceItinerario);
	}
	/**
	 * Aumenta el tiempo que está averiado el vehículo.
	 * 
	 * @param tiempoAveria
	 *            El tiempo que se incrementa la avería del vehículo.
	 * @throws InvalidParameterException
	 *             Si el tiempo de avería introducido es negativo.
	 */
	public void setTiempoAveria(int tiempoAveria) {
		if (tiempoAveria > 0) {
			this.tiempoAveria += tiempoAveria;
			setVelocidadActual(0);
		} else if (tiempoAveria < 0)
			throw new InvalidParameterException("El vehículo " + id
					+ " no puede tener tiempo de avería negativo: " + tiempoAveria + ".");
	}
	/**
	 * Permite modificar la velocidad de circulación del vehículo.
	 * 
	 * @param nuevaVelocidad
	 *            Nueva velocidad de circulación del vehículo.
	 * @throws InvalidParameterException
	 *             Si la velocidad de criculación introducida es negativa.
	 */
	public void setVelocidadActual(int nuevaVelocidad) {
		if (nuevaVelocidad < 0)
			throw new InvalidParameterException("Velocidad negativa no válida.");

		if (nuevaVelocidad <= velMaxima)
			velActual = nuevaVelocidad;
		else
			velActual = velMaxima;
	}
	/**
	 * @return True si el vehículo se encuentra averiado. False en caso
	 *         contrario.
	 */
	public boolean averiado() {
		return tiempoAveria > 0;
	}
	/**
	 * @return La distancia al origen de la carretera actual del vehículo.
	 */
	public int getLocalizacion() {
		return localizacion;
	}

	// INFORMES Y TABLAS
	/**
	 * @return "vehicle_report" como encabezado por defecto para los reports de
	 *         los vehículos.
	 */
	public String getHeader() {
		return "vehicle_report";
	}
	/**
	 * Para completar el informe en formato IniSection que genera el vehículo.
	 * 
	 * @see es.ucm.fdi.ini.IniSection
	 * @param sec
	 *            Sección de tipo IniSection en la que completar los datos.
	 */
	public void fillSectionDetails(IniSection sec) {
		sec.setValue("speed", velActual);
		sec.setValue("kilometrage", kilometrage);
		sec.setValue("faulty", tiempoAveria);
		sec.setValue("location", localizacionString());
	}
	/**
	 * Para completar un mapa con la información del estado de este vehículo.
	 * 
	 * @param camposValor
	 *            Mapa en el que completar los datos.
	 */
	public void fillReportDetails(Map<String, String> camposValor) {
		camposValor.put("speed", Integer.toString(velActual));
		camposValor.put("kilometrage", Integer.toString(kilometrage));
		camposValor.put("faulty", Integer.toString(tiempoAveria));
		camposValor.put("location", localizacionString());
	}
	/**
	 * Devuelve la representación que se dará en los informes a la localización
	 * del vehículo en los informes.
	 * 
	 * @return "arrived" si el vehículo está en destino o "(Road id,
	 *         localización)" en caso contrario.
	 */
	protected String localizacionString() {
		return enDestino
				? "arrived"
				: "(" + actualRoad().getId() + "," + localizacion + ")";
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
		aux += '[' + itinerario.get(0).getJunctionIni().getId();
		for (Road r : itinerario) {
			aux += ',' + r.cruceFin.getId();
		}
		aux += ']';

		return aux;
	}
	private String location() {
		if (enDestino) {
			return "arrived";
		} else {
			return "" + localizacion;
		}
	}

}// Vehicle
