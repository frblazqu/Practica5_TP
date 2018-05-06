package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Car;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.model.objects.Vehicle;

public class NewCar extends NewVehicle {
	public static class NewCarBuilder extends NewVehicle.NewVehicleBuilder {
		// ATRIBUTOS
		// protected final String TAG = "new_vehicle";
		// protected int time;
		// protected String id;
		// protected int mSpeed;
		// protected String[] it;

		/**
		 * Método que indica si estamos (dentro de los vehículos) en la
		 * instancia adecuada para generar a partir de esta seccion.
		 * 
		 * @param sec
		 *            Sección formato IniSection por parsear.
		 * @return true Si la sección se corresponde con un evento NewCar.
		 */
		@Override
		protected boolean esDeEsteTipo(IniSection sec) {
			return sec.getValue("type").equals("car");
		}
		/**
		 * Debe terminar de parsear la sección IniSection con los atributos
		 * necesarios para generar un nuevo evento de la instancia que estemos
		 * considerando y devolver este.
		 * 
		 * @param sec
		 *            La sección formato IniSection que estamos parseando.
		 * @return El evento representado por la sección.
		 */
		protected Event leerAtributosEspecificos(IniSection sec) {
			int resist = EventBuilder.parseIntValue(sec.getValue("resistance"));
			double fProb = EventBuilder.parseDoubleValue(sec
					.getValue("fault_probability"));
			int mFDur = EventBuilder.parseIntValue(sec.getValue("max_fault_duration"));

			if (sec.getValue("seed") != null) {
				long seed = Long.parseLong(sec.getValue("seed"));
				return new NewCar(resist, fProb, mFDur, seed, time, id, mSpeed, it);
			} else
				return new NewCar(resist, fProb, mFDur, time, id, mSpeed, it);
		}
	}
	private int resistance;
	private double fault_probability;
	private int max_fault_duration;
	private long seed;

	public NewCar(int res, double fProb, int mFDur, long s, int time, String vId,
			int mSpeed, String[] it) {
		super(time, vId, mSpeed, it);
		resistance = res;
		fault_probability = fProb;
		max_fault_duration = mFDur;
		seed = s;
	}
	public NewCar(int res, double fProb, int mFDur, int time, String vId, int mSpeed,
			String[] it) {
		super(time, vId, mSpeed, it);
		resistance = res;
		fault_probability = fProb;
		max_fault_duration = mFDur;
		seed = System.currentTimeMillis();
	}
	public void execute(RoadMap map) throws IllegalArgumentException {
		if (!map.duplicatedId(this.getId())) {
			boolean validIds = true;
			for (int i = 1; i < this.getItinerary().length; ++i) {
				if (map.getRoad(this.getItinerary()[i - 1], this.getItinerary()[i]) == null) {
					validIds = false;
					throw new IllegalArgumentException(
							"No existe ninguna carretera que conecte los cruces "
									+ this.getItinerary()[i - 1] + " y "
									+ this.getItinerary()[i] + " para el itinerario.");
				}
			}
			if (validIds) {

				Car car = new Car(resistance, fault_probability, max_fault_duration,
						seed, this.getId(), this.getmSpeed(), this.getItinerary(), map);
				map.addVehicle(car);
			}
		} else {
			throw new IllegalArgumentException("Ya existe un objeto con el id "
					+ this.getId() + '.');
		}
	}
}
