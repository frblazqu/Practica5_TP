package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;
import es.ucm.fdi.model.objects.Path;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Freeway;
import es.ucm.fdi.model.objects.Junction;
import es.ucm.fdi.model.objects.RoadMap.ConexionCruces;

public class NewFreeway extends NewRoad {
	public static class NewFreewayBuilder implements EventBuilder {

		@Override
		public Event parse(IniSection sec) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private int lanes;

	public NewFreeway() {

	}

	public NewFreeway(int time, String id, String src, String dest, int l, int mSpeed,
			int nLanes) {
		super(time, id, src, dest, l, mSpeed);
		lanes = nLanes;
	}

	public void execute(RoadMap map) throws IllegalArgumentException {
		if (map.duplicatedId(road_id))
			throw new IllegalArgumentException("Ya existe un objeto con el id " + road_id + '.');

		try {
			if (map.validJuctionsForRoad(junctionIniId, junctionDestId)) {
				// Cogemos el cruce de destino
				Junction junc = map.getJunction(junctionDestId);
				Junction ini = map.getJunction(junctionIniId);

				// Creamos la nueva autopista y la añadimos al mapa y como
				// entrante al cruce de destino
				Road road = new Freeway(road_id, maxSpeed, length, lanes, junc, ini);
				map.addRoad(road);
				junc.añadirCarreteraEntrante(road);
				ConexionCruces conJunct = new ConexionCruces(road_id, junctionDestId);

				// Cosas del manu para tener el mapa de carreteras/cruces que
				// unen completito
				if (map.getConectionMap().containsKey(junctionIniId))
					map.getConectionMap().get(junctionIniId).add(conJunct);
				else {
					List<ConexionCruces> connect = new ArrayList<ConexionCruces>();
					connect.add(conJunct);
					map.getConectionMap().put(junctionIniId, connect);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"Algo ha fallado con los cruces especificados para la carretera.\n" + e.getMessage(),
					e);
		}

	}
}
