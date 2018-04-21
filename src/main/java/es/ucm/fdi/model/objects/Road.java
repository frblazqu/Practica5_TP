package es.ucm.fdi.model.objects;

import java.util.Comparator;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;;

/**
 * Representación y funcionalidad de una carretera en el simulador.
 * 
 * @author Francisco Javier Blázquez
 * @author Manuel Ortega
 * @version 26/03/18
 */
public class Road extends SimulatedObject
{	
	//ATRIBUTOS
	protected int longitud;									//Longitud de la carretera
	protected int maxVelocidad;								//Velocidad máxima de circulación de la carretera
	protected Junction cruceFin;							//Cruce en el que termina la carretera
	protected MultiTreeMap<Integer,Vehicle> vehiculos;		//Todos los vehículos circulando en la carretera ordenados por su distancia al 
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
	public Road(String id, int maxSpeed, int size, Junction junc)
	{
		super(id, ObjectType.ROAD);
		cruceFin = junc;
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
	public void avanza(RoadMap mapa)															
	{
		if(vehiculos.sizeOfValues() > 0)
		{
			MultiTreeMap<Integer,Vehicle> aux = new MultiTreeMap<>(new MayorAMenor()); 
			int numAveriados = 0;
			
			for(Vehicle v: vehiculos.innerValues())
			{
				if(v.getLocalizacion() == longitud);
				else
				{
					if(v.averiado())	numAveriados++;
					else 				v.setVelocidadActual(velocidadAvance(numAveriados));
				
					v.avanza(mapa);
				}

				aux.putValue(v.getLocalizacion(), v);
			}
			vehiculos = aux;
		}
	}
	public int velocidadAvance(int numAveriados)
	{
		int velocidadBase = Math.min(maxVelocidad, ((int)(maxVelocidad/vehiculos.sizeOfValues()))+1);
		
		if(numAveriados == 0)	return velocidadBase;
		else 					return velocidadBase/2;
	}
	/** Introduce un vehículo en la carretera. Siempre al comienzo de esta.	 */
	public void entraVehiculo(Vehicle vehicle)								
	{
		vehiculos.putValue(0, vehicle);
	}
	/** Elimina un vehículo que ha llegado al final de la carretera. */
	public boolean saleVehiculo(Vehicle vehicle)
	{
		return vehiculos.removeValue(longitud, vehicle);
	}
	public int getLongitud() 		{return longitud;}
	public Junction getJunctionFin()
	{
		return cruceFin;
	}
	public void fillReportDetails(Map<String, String> camposValor)						
	{
		camposValor.put("state", vehiclesInRoad());
	}
	protected String vehiclesInRoad(){
		String aux = "";
		
		for(Vehicle v: vehiculos.innerValues())
		{
			if(v != null)
				aux += '(' + v.getId() + ',' + String.valueOf(v.getLocalizacion()) + "),";
		}
		
		if(aux.length() != 0){
			aux = aux.substring(0, aux.length() - 1);
		}
			
		return aux;
	}
	public void fillSectionDetails(IniSection s)
	{
		s.setValue("state", vehiclesInRoad());
	}
	public String getHeader()
	{
		return "road_report";
	}
	private class MayorAMenor implements Comparator<Integer>							
	{
		public int compare(Integer arg0, Integer arg1)
		{												//Debe devolver:
			return arg1 - arg0;							//Negativo si arg0 < arg1
														//Positivo si arg0 > arg1
		}
	}
	
	public class DescribableRoad implements Describable {

		@Override
		public void describe(Map<String, String> out) {
			out.put("ID", getId());
			//out.put("Source", );			Método para obtener junctions
			out.put("Target", cruceFin.getId());
			out.put("Lenght", Integer.toString(longitud));
			out.put("Max Speed", Integer.toString(maxVelocidad));
			out.put("Vehicles", vehiclesInRoad());
		}
		
	}
 }
