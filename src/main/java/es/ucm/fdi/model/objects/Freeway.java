package es.ucm.fdi.model.objects;

import java.util.Map;
import es.ucm.fdi.ini.IniSection;

public class Freeway extends Road {
	private int lanes;

	// CONSTRUCTORAS
	/**
	 * Constructora por defecto, NO USAR SIN PRECAUCIÓN.
	 * 
	 * @deprecated Porque requiere uso de la constructora por defecto de Road.
	 */
	public Freeway() {
		super();
	}
	/**
	 * {@link Road#Road(String, int, int, Junction, Junction)}
	 * 
	 * @param lanes
	 *            Número de carriles de la autopista.
	 */
	public Freeway(String id, int maxSpeed, int size, int lanes, Junction junc,
			Junction ini) {
		super(id, maxSpeed, size, junc, ini);
		this.lanes = lanes;
	}

	// MÉTODOS QUE SOBREESCRIBEN
	@Override
	public void fillSectionDetails(IniSection s) {
		s.setValue("type", "lanes");
		s.setValue("state", vehiclesInRoad());
	}
	@Override
	public void fillReportDetails(Map<String, String> camposValor) {
		camposValor.put("type", "lanes");
		camposValor.put("state", vehiclesInRoad());
	}
	@Override
	public int velocidadAvance(int numAveriados) {
		int velocidadBase = Math.min(maxVelocidad,
				((maxVelocidad * lanes) / (Math.max(1, vehiculos.sizeOfValues()))) + 1);

		if (numAveriados < lanes)
			return velocidadBase;
		else
			return velocidadBase / 2;
	}
}
