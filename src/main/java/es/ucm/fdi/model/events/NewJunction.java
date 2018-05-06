package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.model.Describable;
import es.ucm.fdi.model.objects.Junction;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.ini.*;

/**
 * Clase encargada de encapsular toda la información y funcionalidad relativa al
 * evento del simulador de añadir un nuevo cruce simple, incluyendo la propia
 * construccción de este tipo de eventos.
 * 
 * @author Francisco Javier Blázquez
 */
public class NewJunction extends Event {
	protected String junction_id;

	// CONSTRUCTORAS
	public NewJunction() {
		junction_id = null;
	}
	public NewJunction(String junctionId, int time) {
		super(time, EventType.NEW_JUNCTION);
		junction_id = junctionId;
	}

	// MÉTODOS PUBLICOS Y GENERALES
	public void execute(RoadMap map) throws IllegalArgumentException {
		if (map.duplicatedId(junction_id))
			throw new IllegalArgumentException("Ya existe un objeto con el id " + junction_id + '.');

		Junction junc = construyeElemento();
		map.addJunction(junc);
	}

	@Override
	public String getTag() {
		return "new_junction";
	}

	@Override
	public void fillSectionDetails(IniSection s) {
		/* Ha caido en desuso */
	}

	// MÉTODOS QUE DEBEN SOBREESCRIBIR LAS CLASES HIJAS
	protected Junction construyeElemento() {
		return new Junction(junction_id);
	}

	// BUILDER
	public static class NewJunctionBuilder implements EventBuilder {
		// ATRIBUTOS DE JUNCTION, COMUNES A TODOS LOS CRUCES
		protected final String TAG = "new_junction";
		protected int time;
		protected String id;

		// MÉTODO GENERAL PARA CONSTRUIR TODOS LOS CRUCES
		public Event parse(IniSection sec) throws IllegalArgumentException {
			if (!sec.getTag().equals(TAG) || !esDeEsteTipo(sec))
				return null;

			leerAtributosComunes(sec);
			return leerAtributosEspecificos(sec);
		}
		public void leerAtributosComunes(IniSection sec) {
			time = EventBuilder.parseTime(sec.getValue("time"));
			id = EventBuilder.parseId(sec.getValue("id"));
		}

		// MÉTODOS QUE DEBEN SER SOBREESCRITOS PARA LOS DEMÁS CRUCES
		protected boolean esDeEsteTipo(IniSection sec) {
			return sec.getValue("type") == null;
		}
		protected Event leerAtributosEspecificos(IniSection sec) {
			return new NewJunction(id, time);
		}
	}

	public void describe(Map<String, String> out) {
		super.describe(out);
		out.put("Type", "New Junction " + junction_id);
	}

}
