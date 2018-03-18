package es.ucm.fdi.model.objects;

public class Bike extends Vehicle{
	public Bike(String id, int maxSpeed, String[] trayecto, RoadMap map){
		super(id, maxSpeed, trayecto, map);
	}
	public void setTiempoAveria(int tiempoAveria){
		if(this.getVelActual()>this.getVelMax()/2){
			super.setTiempoAveria(tiempoAveria);
		}
	}
}
