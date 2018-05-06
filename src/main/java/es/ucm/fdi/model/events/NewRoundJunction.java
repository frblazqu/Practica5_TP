package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Junction;
import es.ucm.fdi.model.objects.RoundJunction;

/**
 * Clase encargada de encapsular toda la información y funcionalidad relativa al
 * evento del simulador de añadir un nuevo cruce circular avanzado, incluyendo
 * la propia construccción de este tipo de eventos.
 * 
 * @author Francisco Javier Blázquez
 */
public class NewRoundJunction extends NewJunction {
	// protected String junction_id
	private int minDurationVerde;
	private int maxDurationVerde;

	public NewRoundJunction(String id, int time, int minDurationVerde,
			int maxDurationVerde) {
		super(id, time);

		this.minDurationVerde = minDurationVerde;
		this.maxDurationVerde = maxDurationVerde;
	}

	@Override
	protected Junction construyeElemento() {
		return new RoundJunction(junction_id, minDurationVerde, maxDurationVerde);
	}

	public static class NewRoundJunctionBuilder extends NewJunction.NewJunctionBuilder {
		// protected String id (leido)
		// protected int time (leido)
		private int minDurationVerde;
		private int maxDurationVerde;

		@Override
		protected boolean esDeEsteTipo(IniSection sec) {
			return sec.getValue("type").equals("rr");
		}
		@Override
		protected Event leerAtributosEspecificos(IniSection sec) {
			minDurationVerde = EventBuilder.parseIntValue(sec.getValue("min_time_slice"));
			maxDurationVerde = EventBuilder.parseIntValue(sec.getValue("max_time_slice"));

			return new NewRoundJunction(id, time, minDurationVerde, maxDurationVerde);
		}
	}
}
