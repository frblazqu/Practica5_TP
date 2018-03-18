package es.ucm.fdi.model.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import es.ucm.fdi.ini.IniSection;

public abstract class SimulatedObject
{
	//ATRIBUTOS
	private String id;				//Id del objeto
	ObjectType tipo;				//Tipo del objeto (en desuso)
	
	//CONSTRUCTORAS
	/**Constructora por defecto, no preparada para usarse.*/
	public SimulatedObject()
	{
		id = "";	
		tipo = ObjectType.OBJECT_ERROR;
	}
	/**Constructora que inicializa el identificador del objeto de la simulación y su tipo.*/
	public SimulatedObject(String identificador, ObjectType tipo)
	{
		id = identificador;
		this.tipo = tipo;
	}
	
	//MÉTODOS
	/**Devuelve el Id del objeto de la simulación.*/
	public String getId() 	{return id;}
	/**Escribe el informe del objeto en un determinado momento con el formato IniSection.*/
	public void escribeInforme(OutputStream out, int time) throws IOException
	{
		Map<String, String> informe = new HashMap<>();
		IniSection s;
		
		//Introducimos toda la información del report en el mapa informe
		report(time, informe);
		
		//Llenamos una IniSection con los datos del informe
		s = new IniSection(informe.get(""));
		informe.remove("");
				
		informe.forEach((key, value) -> s.setValue(key, value));
		
		//Escribimos la sección
		s.store(out);
	}
	/**Dado un mapa<String,String> introduce en este el informe del objeto campo por campo con nombreCampo-valor en cada
	 * clave-valor del mapa.*/
	public void report(int time, Map<String, String> camposValor)
	{
		//Este método se hace para tener toda la generalidad posible, no depende de un flujo de salida
		//Simplemente rellena un mapa<String,String> con toda la información necesaria.
		camposValor.put("", getHeader());
		camposValor.put("id", id);
		camposValor.put("time", Integer.toString(time)); 
		fillReportDetails(camposValor);	
	}
	/**Método encargado de introducir en el mapa con el infome los campos específicos de cada simObject.*/
	public abstract void fillReportDetails(Map<String, String> camposValor);
	/**Devuelve el encabezado de los reports específico de cada simObject.*/
	public abstract String getHeader();
}
