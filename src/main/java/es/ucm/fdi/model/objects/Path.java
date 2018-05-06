package es.ucm.fdi.model.objects;

import java.util.Map;
import es.ucm.fdi.ini.IniSection;

public class Path extends Road {
	// CONSTRUCTORAS
	/**
	 * Constructora por defecto, NO USAR SIN PRECAUCIÓN.
	 * 
	 * @deprecated Porque requiere de la constructora por defecto de road.
	 */
	public Path() {
		super();
	}
	/**
	 * {@link Road#Road(String, int, int, Junction, Junction)}
	 */
	public Path(String id, int maxSpeed, int size, Junction junc,
			Junction ini) {
		super(id, maxSpeed, size, junc, ini);
	}

	// MÉTODOS SOBREESCRITOS
	@Override
	public void fillSectionDetails(IniSection s) {
		s.setValue("type", "dirt");
		s.setValue("state", vehiclesInRoad());
	}
	@Override
	public void fillReportDetails(Map<String, String> camposValor) {
		camposValor.put("type", "dirt");
		camposValor.put("state", vehiclesInRoad());
	}
	@Override
	public int velocidadAvance(int numAveriados) {
		return maxVelocidad / (1 + numAveriados);
	}
}
