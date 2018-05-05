package es.ucm.fdi.model.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.Describable;

/**
 * Clase que sirve de marco para todos los objetos de la simulación. Con su identificador y su funcionalidad
 * más básica. De esta clase heredan todos los objetos del simulador de tráfico.
 * 
 * @author Francisco Javier Blázquez Martínez.
 * @version 02/05/18
 */
public abstract class SimulatedObject implements Describable
{
	protected String id;		//Id del objeto
	
	/**
	 * Constructora por defecto. NO DEBE USARSE SIN PRECAUCIÓN.
	 * 
	 * @deprecated Pues no establece un identificador válido al objeto.
	 */
	public SimulatedObject()
	{
		id = "";	
	}
	/**
	 * Constructora habitual. Inicializa el identificador del objeto.
	 *
	 * @param identificador El identificador del nuevo objeto. (debe ser alfanumérico).
	 */
	public SimulatedObject(String identificador)
	{
		id = identificador;
	}
	
	public String getId() 	{return id;}
	/**
	 * Escribe el informe del objeto en un determinado momento con el formato IniSection.
	 * 
	 * @deprecated Porque es mejor que el objeto solo se pase a mapa y otro lo lleve a IniSection.
	 * @param out Flujo de salida en el que escribir el informe.
	 * @param time Tiempo en el que se escribe el informe.
	 * @throws IOException si hay problemas en la escritura.
	 */
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
	/**
	 * @see es.ucm.fdi.ini.IniSection 
	 * @return Un informe en formato IniSection de la situación del objeto.
	 */
	public IniSection seccionInforme(int time)
	{
		//Creamos la sección con el encabezado del informe
		IniSection s = new IniSection(getHeader());
				
		s.setValue("id", id);
		s.setValue("time" , "" + time);
		fillSectionDetails(s);
		
		return s;
	}
	/**
	 * Introduce en el mapa pasado como paámetro la información del estado del objeto.
	 * 
	 * @param time Tiempo en el que se genera el informe.
	 * @param camposValor Mapa en el que se introduce la información. Se presupone vacío para
	 * asegurar la corrección de otros métodos.
	 */
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
	/**Completa los aspectos únicos de cada informe. Específico para cada objeto.*/
	public abstract void fillSectionDetails(IniSection s);
	
	public void describe(Map<String, String> out) {
		out.put("ID", id);
	}
	
}//SimulatedObject
