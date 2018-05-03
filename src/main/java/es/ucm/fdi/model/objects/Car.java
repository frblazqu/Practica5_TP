package es.ucm.fdi.model.objects;

import java.util.Map;
import java.util.Random;
import es.ucm.fdi.ini.IniSection;

public class Car extends Vehicle
{
	private int resistance;				//Resistencia del coche a las averías
	private int kmSinceLastFaulty;		//Kilometros recorridos desde la última avería
	private double faultProbability;	//Factor de probabilidad de avería
	private int maxFaultDuration;		//Máxima duración de una avería
	private long seed;					//Semilla de inicio del generador de números aleatorios
	private Random random;				//Generador de números aleatorios
	
	
	/**
	 * Constructora por defecto, NO DEBE USARSE SIN PRECAUCIÓN.
	 * 
	 * @deprecated Pues requiere usar la constructora por defecto de Road.
	 */
	public Car()
	{
		super();
	}
	/**
	 * {@link Vehicle#Vehicle(String, int, String[], RoadMap)}
	 * 
	 * @deprecated Por requerir el uso de la constructora no aconsejada de Vehicle.
	 * @param res Resistencia del vehículo a las averías en km.
	 * @param fProb Factor de probabilidad de avería del vehículo.
	 * @param mFDur Máxima duración de avería del vehículo.
	 * @param s Semilla de números aleatorios.
	 */
	public Car(int res, double fProb, int mFDur, long s, String id, int maxSpeed, String[] trayecto, RoadMap map){
		super(id, maxSpeed, trayecto, map);
		resistance = res;
		faultProbability = fProb;
		maxFaultDuration = mFDur;
		seed = s;
		random = new Random(seed);
		kmSinceLastFaulty = 0;
	}
	/**
	 * {@link #Car(int, double, int, long, String, int, String[], RoadMap)} 
	 * 
	 * @deprecated
	 */
	public Car(int res, double fProb, int mFDur, String id, int maxSpeed, String[] trayecto, RoadMap map){
		this(res,fProb,mFDur, System.currentTimeMillis(), id, maxSpeed, trayecto, map);
	}

	/** 
	 * Genera aleatoriamente dentro de un rango acotado por {@link #maxFaultDuration} el tiempo
	 * que estará averiado el vehículo.
	 */
	public int tiempoAveria(){
		return random.nextInt(maxFaultDuration) + 1;
	}
	/** 
	 * Para decidir junto con {@link #faultProbability} si se avería el vehículo o no de forma
	 * aleatoria.
	 */
	public double posibleProbAveria(){
		return random.nextDouble();
	}
	@Override
	public void avanza()
	{
		int aux = kilometrage;
		
		if(!this.averiado() && kmSinceLastFaulty>resistance && posibleProbAveria() < faultProbability)
		{
			this.setTiempoAveria(tiempoAveria());
			kmSinceLastFaulty = 0;
		}
		
		super.avanza();
		
		kmSinceLastFaulty += this.getLocalizacion() - aux;
	}
	@Override
	public void fillReportDetails(Map<String, String> camposValor)
	{
		camposValor.put("type", "car");
		camposValor.put("speed", "" + velActual);
		camposValor.put("kilometrage", "" + kilometrage);
		camposValor.put("faulty", "" + tiempoAveria);
		camposValor.put("location", localizacionString());
	}
	@Override
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("type", "car");
		s.setValue("speed", velActual);
		s.setValue("kilometrage", kilometrage);
		s.setValue("faulty", tiempoAveria);
		s.setValue("location", localizacionString());
	}	
} //Car
