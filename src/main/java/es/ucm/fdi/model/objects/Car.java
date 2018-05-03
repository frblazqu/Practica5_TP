package es.ucm.fdi.model.objects;

import java.util.Map;
import java.util.Random;
import es.ucm.fdi.ini.IniSection;

public class Car extends Vehicle{
	private int resistance;
	private int kmSinceLastFaulty;
	private double fault_probability;
	private int max_fault_duration;
	private long seed;
	private Random numAleatorio;
	
	//CONSTRUCTORAS
	//Constructora con Seed
	public Car(int res, double fProb, int mFDur, long s, String id, int maxSpeed, String[] trayecto, RoadMap map){
		super(id, maxSpeed, trayecto, map);
		resistance = res;
		fault_probability = fProb;
		max_fault_duration = mFDur;
		seed = s;
		numAleatorio = new Random(seed);
		kmSinceLastFaulty = 0;
	}
	//Constructora sin seed (default)
	public Car(int res, double fProb, int mFDur, String id, int maxSpeed, String[] trayecto, RoadMap map){
		super(id, maxSpeed, trayecto, map);
		resistance = res;
		fault_probability = fProb;
		max_fault_duration = mFDur;
		seed = System.currentTimeMillis();
		numAleatorio = new Random(seed);
		kmSinceLastFaulty = 0;
	}
	public Car()
	{
		super();
	}
	
	public int tiempoAveria(){
		return numAleatorio.nextInt(max_fault_duration) + 1;
	}
	public double posibleProbAveria(){
		return numAleatorio.nextDouble();
	}
	public void avanza(RoadMap map){
		int aux = kilometrage;
		if(!this.averiado() && kmSinceLastFaulty>resistance && posibleProbAveria() < fault_probability){
			this.setTiempoAveria(tiempoAveria());
			kmSinceLastFaulty = 0;
		}
		super.avanza();
		kmSinceLastFaulty += this.getLocalizacion() - aux;
	}
	public void fillReportDetails(Map<String, String> camposValor)
	{
		camposValor.put("type", "car");
		camposValor.put("speed", Integer.toString(velActual));
		camposValor.put("kilometrage", Integer.toString(kilometrage));
		camposValor.put("faulty", Integer.toString(tiempoAveria));
		camposValor.put("location", localizacionString());
	}
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("type", "car");
		s.setValue("speed", velActual);
		s.setValue("kilometrage", kilometrage);
		s.setValue("faulty", tiempoAveria);
		s.setValue("location", localizacionString());
	}	
}
