package es.ucm.fdi.model.objects;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

/**
 * Representación y funcionalidad de una carretera en el simulador.
 * 
 * @author Francisco Javier Blázquez
 * @author Manuel Ortega
 * @version 02/05/18
 */
public class Road extends SimulatedObject {
	protected int longitud; // Longitud de la carretera
	protected int maxVelocidad; // Velocidad máxima de circulación de la
								// carretera
	protected Junction cruceFin; // Cruce en el que termina la carretera
	protected Junction cruceIni; // Id del cruce del que parte la carretera
	protected MultiTreeMap<Integer, Vehicle> vehiculos; // Todos los vehículos
														// circulando en la
														// carretera ordenados
														// por su distancia al
														// origen de manera
														// decreciente
	// CONSTRUCTORAS
	/**
	 * Constructora por defecto, NO DEBE USARSE SIN PRECAUCIÓN.
	 * 
	 * @deprecated Pues inicializa los atributos de la carretera a valores no
	 *             válidos.
	 */
	public Road() {
		super();
		longitud = 0;
		maxVelocidad = 0;
		vehiculos = new MultiTreeMap<>();
	}
	/**
	 * Constructora usual. Genera una carretera dados:
	 * 
	 * @param id
	 *            Identificador de la carretera.
	 * @param maxSpeed
	 *            Máxima velocidad de circulación de la carretera.
	 * @param size
	 *            Longitud de la carretera.
	 * @param fin
	 *            Cruce en el que finaliza la carretera.
	 * @param ini
	 *            Cruce en el que comienza la carretera.
	 */
	public Road(String id, int maxSpeed, int size, Junction fin, Junction ini) {
		super(id);
		cruceFin = fin;
		cruceIni = ini;
		maxVelocidad = maxSpeed;
		longitud = size;
		vehiculos = new MultiTreeMap<Integer, Vehicle>((a, b) -> b - a);

		// Uso con y sin lambdas!!
		// vehiculos = new MultiTreeMap<Integer,Vehicle>((a,b) -> b - a);
		// vehiculos = new MultiTreeMap<Integer,Vehicle>(new MayorAMenor());
	}

	// FUNCIONALIDAD
	public int getLongitud() {
		return longitud;
	}
	/** @return El cruce en el que finaliza la carretera. */
	public Junction getJunctionFin() {
		return cruceFin;
	}
	/** @return El cruce en el que comienza la carretera. */
	public Junction getJunctionIni() {
		return cruceIni;
	}
	/**
	 * @return Una lista de vehículos ordenada por distancia al origen de la
	 *         carretera de todos los vehículos que circulan por esta.
	 */
	public List<Vehicle> vehicles() {
		return vehiculos.valuesList();
	}
	/**
	 * Avanza el estado de la carretera, esto es, avanzan todos los vehículos
	 * que contiene dentro de la propia carretera.
	 */
	public void avanza() {
		if (vehiculos.sizeOfValues() > 0) {
			MultiTreeMap<Integer, Vehicle> aux = new MultiTreeMap<>(
					new MayorAMenor());
			int numAveriados = 0;

			for (Vehicle v : vehiculos.innerValues()) {
				if (v.getLocalizacion() == longitud)
					;
				else {
					if (v.averiado())
						numAveriados++;
					else
						v.setVelocidadActual(velocidadAvance(numAveriados));

					v.avanza();
				}

				aux.putValue(v.getLocalizacion(), v);
			}

			vehiculos = aux;
		}
	}
	/**
	 * Devuelve la velocidad de avance del vehículo.
	 * 
	 * @param numAveriados
	 *            Número de vehículos averiados por delante del que vamos a
	 *            cambiar su velocidad.
	 */
	protected int velocidadAvance(int numAveriados) {
		int velocidadBase = Math.min(maxVelocidad,
				((int) (maxVelocidad / vehiculos.sizeOfValues())) + 1);

		if (numAveriados == 0)
			return velocidadBase;
		else
			return velocidadBase / 2;
	}
	/** Introduce un vehículo en la carretera. Siempre al comienzo de esta. */
	protected void entraVehiculo(Vehicle vehicle) {
		vehiculos.putValue(0, vehicle);
	}
	/**
	 * Elimina un vehículo que ha llegado al final de la carretera.
	 * 
	 * @return True si elimina el vehículo de esta carretera. False en caso
	 *         contrario.
	 */
	public boolean saleVehiculo(Vehicle vehicle) {
		// if(vehicle.actualRoad() != this || vehicle.getLocalizacion() !=
		// longitud)
		// return false;

		return vehiculos.removeValue(longitud, vehicle);
	}
	/**
	 * Comparador de enteros de mayor a menor.
	 */
	private class MayorAMenor implements Comparator<Integer> {
		public int compare(Integer arg0, Integer arg1) { // Debe devolver:
			return arg1 - arg0; // Negativo si arg0 < arg1
								// Positivo si arg0 > arg1
		}
	}

	// INFORMES Y TABLAS
	/**
	 * @return "road_report" como encabezado por defecto para los reports de las
	 *         carreteras.
	 */
	public String getHeader() {
		return "road_report";
	}
	/**
	 * Completa los detalles específicos de esta carretera en la sección pasada
	 * como parámetro.
	 * 
	 * @param sec
	 *            Sección a completar con la información de la carretera.
	 */
	public void fillSectionDetails(IniSection sec) {
		sec.setValue("state", vehiclesInRoad());
	}
	/**
	 * Completa los detalles específicos de esta carretera en el mapa pasado
	 * como parámetro.
	 * 
	 * @param camposValor
	 *            Mapa en el que introducir la información de la carretera.
	 */
	public void fillReportDetails(Map<String, String> camposValor) {
		camposValor.put("state", vehiclesInRoad());
	}
	/**
	 * @return La representación textual de los vehículos en la carretera de los
	 *         reports.
	 */
	protected String vehiclesInRoad() {
		String aux = "";

		for (Vehicle v : vehiculos.innerValues())
			aux += '(' + v.getId() + ',' + v.getLocalizacion() + "),";

		if (aux.length() != 0)
			aux = aux.substring(0, aux.length() - 1);

		return aux;
	}
	public void describe(Map<String, String> out) {
		super.describe(out);;
		out.put("Source", cruceIni.getId());
		out.put("Target", cruceFin.getId());
		out.put("Lenght", "" + longitud);
		out.put("Max Speed", "" + maxVelocidad);
		out.put("Vehicles", vehiclesInRoadDesc());
	}
	private String vehiclesInRoadDesc() {
		String aux = "";

		for (Vehicle v : vehiculos.innerValues()) {
			if (v != null)
				aux += '[' + v.getId() + ','
						+ String.valueOf(v.getLocalizacion()) + "],";
		}

		if (aux.length() != 0) {
			aux = aux.substring(0, aux.length() - 1);
		}

		return aux;
	}
}// Road
