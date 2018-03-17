package es.ucm.fdi.model.objects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import es.ucm.fdi.util.MultiTreeMap;;

public class Road extends SimulatedObject
{	
	//ATRIBUTOS
	private int longitud;									//Longitud de la carretera
	private int maxVelocidad;								//Velocidad máxima de circulación de la carretera
	private MultiTreeMap<Integer,Vehicle> vehiculos;		//Todos los vehículos circulando en la carretera ordenados por su distancia al 
															//origen de manera decreciente
	//CONSTRUCTORAS
	/**Constructora por defecto, inicializa a una carretera de longitud nula con identificador  vacío "" y sin vehículos.*/
 	public Road()
	{
 		super();
		longitud = 0;
		maxVelocidad = 0;
		vehiculos = new MultiTreeMap<>();
	}
 	/**Constructora usual, genera una carretera vacía con la ordenación de vehículos por distancia al origen decreciente y
 	 * vacía de vehículos.*/
	public Road(String id, int maxSpeed, int size, RoadMap map)
	{
		super(id, ObjectType.ROAD);
		maxVelocidad = maxSpeed;
		longitud = size;
		vehiculos = new MultiTreeMap<Integer,Vehicle>((a,b) -> a - b);					
		
		//Uso con y sin lambdas!!
		//vehiculos = new MultiTreeMap<Integer,Vehicle>((a,b) -> b - a);
		//vehiculos = new MultiTreeMap<Integer,Vehicle>(new MayorAMenor());	
	}
	/**Constructora solo para testeo*/
	public Road(String id, int maxSpeed, int size)
	{
		super(id, ObjectType.ROAD);
		maxVelocidad = maxSpeed;
		longitud = size;
	}
	
	//MÉTODOS
	public void avanza(RoadMap map)																
	{
		if(vehiculos.sizeOfValues() > 0)
		{
			MultiTreeMap<Integer,Vehicle> aux = new MultiTreeMap<>(new MayorAMenor()); 
			int velocidadBase = Math.min(maxVelocidad, ((int)(maxVelocidad/vehiculos.sizeOfValues()))+1);
			int numAveriados = 0;
			
			//Esto no va a funcionar bien porque si un vehículo tiene a otro averiado en la misma localización en la carretera
			//NO debería tener el factor de reducción.
			for(Vehicle v: vehiculos.innerValues())
			{
				//Ajustamos la velocidad
				if(v.averiado())	numAveriados++;
				else if(numAveriados == 0)
				{
					v.setVelocidadActual(velocidadBase);
				}
				else
					v.setVelocidadActual(velocidadBase/2);
				
				
				//Avanzamos y si no cambia de carretera lo insertamos en el nuevo Mtm
				v.avanza(map);
				
				//Qué pasa si llega al destino ??
				if(v.getLocalizacion() != this.longitud)
				{
					aux.putValue(v.getLocalizacion(), v);
				}
			}
			vehiculos = aux;
		}
	}
	public void entraVehiculo(Vehicle vehicle)											//Excepciones									
	{
		//comprobar que no sea null
		//vehicle.setVelocidadActual(0); ??
		vehiculos.putValue(0, vehicle);
	}
	public boolean saleVehiculo(Vehicle vehicle)										//Testear y dar robustez
	{
		return vehiculos.removeValue(longitud, vehicle);
	}
	public int getLongitud() 		{return longitud;}
	public void fillReportDetails(Map<String, String> camposValor)						//Falta rellenar el estado recorriendo el Mtm
	{
		camposValor.put("state", vehiclesInRoad());
	}
	public String vehiclesInRoad(){
		String aux = "";
		
		for(Vehicle v: vehiculos.innerValues()){
			aux += '(' + v.getId() + ',' + String.valueOf(v.getLocalizacion()) + "),";
		}
		
		if(aux.length() != 0){
			aux = aux.substring(0, aux.length() - 1);
		}
			
			return aux;
	}
	public String getHeader()
	{
		return "road_report";
	}
	private class MayorAMenor implements Comparator<Integer>							//Cómo se si es de menos a mas o de mas a menos ??
	{
		public int compare(Integer arg0, Integer arg1)
		{																				//Debe devolver:
			return arg0 - arg1;															//Negativo si arg0 < arg1
																						//Positivo si arg0 > arg1
		}
	}
}
