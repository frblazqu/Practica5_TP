package es.ucm.fdi.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.EventFactory;

/**
 * Controla toda la simulación que se va a ejecutar. Tiene las siguientes responsabilidades:
 * -> Crear el simulador.
 * -> Leer los eventos de la simulación.
 * -> Manejo de gran parte de excepciones lanzadas en niveles inferiores.
 * 
 * @author 		Francisco Javier Blázquez
 * @version		23/03/18
 */
public class Controller
{
	//ATRIBUTOS POR DEFECTO
	private final static int 	DEFAULT_TICKS = 10;
	private final static String DEFAULT_INI_FILE = "iniFile.ini";
	private final static String DEFAULT_OUT_FILE = "outFile.ini";
	private final static String DEFAULT_READ_DIRECTORY = "src/main/resources/readStr/";
	private final static String DEFAULT_WRITE_DIRECTORY = "src/main/resources/writeStr/";

	//ATRIBUTOS DE LA CLASE
	private int ticksSimulacion;							//Duración de la simulación
	private OutputStream outputStream;						//Flujo de salida de informes de la simulación
	private InputStream inputStream;						//Flujo de entrada de datos para la simulación
	private TrafficSimulator simulador;						//Simulador a controlar
	//private EventFactory eventBuilder;					//Para parsear iniSection como events
	
	//CONSTRUCTORAS
	/**
	 * Crea un nuevo simulador y nuevos flujos de entrada salida con los parámetros recibidos.
	 * @param loadFilePath 	localización del fichero de entrada de eventos
	 * @param saveFilePath	localización del fichero de escritura de informes
	 * @param numTicks		duración de la simulación a ejecutar
	 * 
	 * @throws FileNotFoundException Si la ruta de entrada o salida de datos que se ha introducido no es alcanzable.
	 */
	public Controller(String loadFilePath, String saveFilePath, int numTicks) throws FileNotFoundException 
	{
		inputStream  = new FileInputStream( new File(loadFilePath));
		outputStream = new FileOutputStream(new File(saveFilePath));
		simulador = new TrafficSimulator();
		ticksSimulacion = numTicks;
		
		//Dejamos que lance excepción libremente si se diera el caso (la controlamos en startBatchMode)
	}
	/**
	 * Crea un nuevo simulador con entrada de eventos, flujo de salida y duración de la simulación por defecto.
	 * Llama a la constructora más general con los parámetros por defecto.
	 * Entrada por defecto -> src/main/resources/readStr/iniFile.ini
	 * Salida por defecto  -> src/main/resources/writeStr/outFile.ini
	 * Duración simulación por defecto -> 10
	 * 
	 * @see #Controller(String, String, int)
	 * @throws FileNotFoundException
	 */
	public Controller() throws FileNotFoundException 
	{
		this(DEFAULT_READ_DIRECTORY  + DEFAULT_INI_FILE/**/, DEFAULT_WRITE_DIRECTORY + DEFAULT_OUT_FILE /**/, DEFAULT_TICKS);
	}
	/**
	 * Llama a la constructora más general con el tiempo por defecto.
	 * Duración simulación por defecto -> 10 ticks
	 * 
	 * @see #Controller(String, String, int)
	 * @throws FileNotFoundException
	 */
	public Controller(String loadFilePath, String saveFilePath) throws FileNotFoundException 
	{
		this(loadFilePath, saveFilePath, DEFAULT_TICKS);
	}
	
	//MÉTODOS
	/**
	 *Lee el fichero .ini del flujo de entrada y parsea cada una de sus secciones en eventos que inserta en el simulador.
	 *Dejamos que se lancen las excepciones sin ser modificadas para que lleguen a donde se crea el objeto controller.
	 * 
	 * @throws IOException Si no se consigue leer correctamente el fichero de entrada.
	 * @throws IllegalArgumentException Si alguna sección no se consigue parsear bien.
	 */
	public void leerDatosSimulacion() throws IOException, IllegalArgumentException
	{
		//Cargamos todo el fichero en la variable ini (Puede lanzar IOException)
		Ini ini = new Ini(inputStream);
		
		//Parseamos uno a uno los eventos de las secciones
		Event evento;
		
		for(IniSection s: ini.getSections())
		{
			evento = EventFactory.buildEvent(s); //throw IllegalArgumentException()
			simulador.insertaEvento(evento);	
		}
	}
	/**
	 * Ejecuta la simulación.
	 * 
	 * @see TrafficSimulator#ejecuta(int, OutputStream)
	 * @throws IOException si hay problemas de escritura de reports de la simulación.
	 * @throws IllegalArgumentException si algún parámetro de la simulación no es válido.
	 * */
	public void run() 
	{
		try{
		simulador.ejecuta(ticksSimulacion, outputStream);	
		}
		catch(IllegalArgumentException e){
			throw new IllegalArgumentException("Something went wrong while executing the simulation", e);
		}
		catch(IOException e){
			//???
		}
		catch(Exception e)
		{
			/* Oh, Oh, this is unexpected! */ 
		}
	}
}






