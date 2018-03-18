package es.ucm.fdi.model.objects;

import java.util.Map;
import java.util.Random;

public class Car extends Vehicle{
	private int resistance;
	private int kmSinceLastFaulty;
	private double fault_probability;
	private int max_fault_duration;
	private long seed;
	private Random numAleatorio;
	
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
	public int tiempoAveria(){
		return numAleatorio.nextInt(max_fault_duration) + 1;
	}
	public double posibleProbAveria(){
		return numAleatorio.nextDouble();
	}
	public void avanza(RoadMap map){
		int aux = this.getKilometrage();
		if(!this.averiado() && kmSinceLastFaulty>=resistance && posibleProbAveria() < fault_probability){
			this.setTiempoAveria(tiempoAveria());
			kmSinceLastFaulty = 0;
		}
		super.avanza(map);
		kmSinceLastFaulty += this.getLocalizacion() - aux;
	}
	public void fillReportDetails(Map<String, String> camposValor)
	{
		camposValor.put("speed", Integer.toString(this.getVelActual()));
		camposValor.put("kilometrage", Integer.toString(this.getKilometrage()));
		camposValor.put("type", "car");
		camposValor.put("faulty", Integer.toString(this.getTiempoAveria()));
		if(this.getEnDestino()){
			camposValor.put("location", "arrived");
		}else{
		camposValor.put("location", "(" + this.getItinerario().get(this.getIndIti()).getId() + "," + Integer.toString(this.getLocalizacion())  + ")");		
		}
	}
}
