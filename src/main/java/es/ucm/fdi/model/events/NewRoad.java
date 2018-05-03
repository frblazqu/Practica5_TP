package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Describable;
import es.ucm.fdi.model.objects.Junction;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.model.objects.RoadMap.ConexionCruces;

public class NewRoad extends Event
{
	protected String road_id;
	protected String junctionIniId;
	protected String junctionDestId;
	protected int length;
	protected int maxSpeed;

	public NewRoad()
	{
		road_id = null;
		junctionIniId = null;
		junctionDestId = null;
		length = 0;
		maxSpeed = 0;
	}

	public NewRoad(int time, String id, String iniId, String destId, int l, int mSpeed)
	{
		super(time);
		road_id = id;
		junctionIniId = iniId;
		junctionDestId = destId;
		length = l;
		maxSpeed = mSpeed;
	}

	public void execute(RoadMap map) throws IllegalArgumentException
	{
		if (map.duplicatedId(road_id))
			throw new IllegalArgumentException("The id " + road_id + " is already used");

		try
		{
			//Si los ids son de cruces existentes...
			if (map.validJuctionsForRoad(junctionIniId, junctionDestId))
			{
				//Cogemos el cruce de destino de la carretera
				Junction junc = map.getJunction(junctionDestId);
				Junction ini  = map.getJunction(junctionIniId);
				
				//Creamos la carretera, la añadimos al mapa y como entrante al cruce de destino
				Road road = new Road(road_id, maxSpeed, length, junc, ini);
				map.addRoad(road);
				junc.añadirCarreteraEntrante(road);
				ConexionCruces conJunct = new ConexionCruces(road_id, junctionDestId);
				
				//Cosas del manu para tener el mapa de carreteras/cruces que unen completito
				if (map.getConectionMap().containsKey(junctionIniId))
					map.getConectionMap().get(junctionIniId).add(conJunct);
				else
				{
					List<ConexionCruces> connect = new ArrayList<ConexionCruces>();
					connect.add(conJunct);
					map.getConectionMap().put(junctionIniId, connect);
				}
			}
			else
				throw new IllegalArgumentException("There is something wrong with the junctions specified for the road");

		} catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException("There is something wrong with the junctions specified for the road", e);
		}
	}

	public static class NewRoadBuilder implements EventBuilder
	{
		public Event parse(IniSection sec) throws IllegalArgumentException
		{
			if (!sec.getTag().equals("new_road"))
			{
				return null;
			} else
			{
				int tm = EventBuilder.parseTime(sec.getValue("time"));
				try
				{
					String id   = EventBuilder.parseId(sec.getValue("id"));
					String src  = EventBuilder.parseId(sec.getValue("src"));
					String dest = EventBuilder.parseId(sec.getValue("dest"));
					int mSpeed  = EventBuilder.parseIntValue(sec.getValue("max_speed"));
					int l       = EventBuilder.parseIntValue(sec.getValue("length"));

					if (sec.getValue("type") == null)
						return new NewRoad(tm, id, src, dest, l, mSpeed);
					else if (sec.getValue("type").equals("dirt"))
						return new NewPath(tm, id, src, dest, l, mSpeed);
					else if (sec.getValue("type").equals("lanes"))
					{
						int lanes = EventBuilder.parseIntValue(sec.getValue("lanes"));
						return new NewFreeway(tm, id, src, dest, l, mSpeed, lanes);
					} else
						return null;
				} catch (IllegalArgumentException e)
				{
					throw new IllegalArgumentException("There is something wrong with one of the atributes.", e);
				}
			}
		}
	}

	public String getTag()
	{
		return "new_road";
	}

	public void fillSectionDetails(IniSection s)
	{
		s.setValue("id", road_id);
		s.setValue("src", junctionIniId);
		s.setValue("dest", junctionDestId);
		s.setValue("max_speed", "" + maxSpeed);
		s.setValue("length", "" + length);
	}

	public void describe(Map<String, String> out) {
		super.describe(out);
		out.put("Type", "New Road " + road_id);
	}
	
}