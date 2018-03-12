package es.ucm.fdi.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.events.*;
import es.ucm.fdi.model.events.NewRoad.NewRoadBuilder;
import es.ucm.fdi.model.events.NewJunction.NewJunctionBuilder;
import es.ucm.fdi.model.events.NewVehicle.NewVehicleBuilder;
import es.ucm.fdi.model.events.MakeVehicleFaulty.NewVehicleFaulty;

public class Controller
{
	//ARGUMENTOS POR DEFECTO
	private final static int 	DEFAULT_TICKS = 10;
	private final static String DEFAULT_INI_FILE = "iniFile.ini";
	private final static String DEFAULT_OUT_FILE = "outFile.ini";
	private final static String DEFAULT_READ_DIRECTORY = "src/main/resources/readStr/";
	private final static String DEFAULT_WRITE_DIRECTORY = "src/main/resources/writeStr/";

	//ARGUMENTOS DE LA CLASE
	private int ticksSimulacion;							//Duración de la simulación
	private OutputStream outputStream;						//Flujo de salida de informes de la simulación
	private InputStream inputStream;						//Flujo de entrada de datos para la simulación
	private TrafficSimulator simulador;						//Simulador a controlar
	private List<EventBuilder> EventBuilderList;			//Lista para parsear las IniSections
	
	//CONSTRUCTORAS
	/**Dada una localización del fichero de entrada y una localización para el fichero de salida
	 * y la duración (numTicks) de la simulación crea los flujos de entrada/salida y el propio simulador.
	 * Lanza FileNotFoundException si no se puede crear el flujo de entrada o salida.*/
	public Controller(String loadFilePath, String saveFilePath, int numTicks) throws FileNotFoundException 
	{
		inputStream  = new FileInputStream( new File(loadFilePath));
		outputStream = new FileOutputStream(new File(saveFilePath));
		ticksSimulacion = numTicks;
		//Deberíamos en la propia constructora de la clase Contro generar totalmente el TrafficSimulator ??
		//Esto es, deberíamos también leer e insertar los eventos del fichero aquí ??
		simulador = new TrafficSimulator();
		EventBuilderList = new ArrayList<>();
		EventBuilderList.add(new NewRoadBuilder());
		EventBuilderList.add(new NewJunctionBuilder());
		EventBuilderList.add(new NewVehicleBuilder());
		EventBuilderList.add(new NewVehicleFaulty());
	}
	/**Realiza una llamada a la constructora más general con todos los parámetros por defecto.
	 * Entrada por defecto -> src/main/resources/readStr/iniFile.ini
	 * Salida por defecto  -> src/main/resources/writeStr/outFile.ini
	 * Duración simulación por defecto -> 10 ticks
	 * Lanza FileNotFoundException si no se puede crear el flujo de entrada o salida.*/
	public Controller() throws FileNotFoundException 
	{
		this(DEFAULT_READ_DIRECTORY  + DEFAULT_INI_FILE/**/, DEFAULT_WRITE_DIRECTORY + DEFAULT_OUT_FILE /**/, DEFAULT_TICKS);
	}
	/**Realiza una llamada a la constructora más general con la duraciónd de simulación por defecto
	 * Duración simulación por defecto -> 10 ticks
	 * Lanza FileNotFoundException si no se puede crear el flujo de entrada o salida.*/
	public Controller(String loadFilePath, String saveFilePath) throws FileNotFoundException 
	{
		this(loadFilePath, saveFilePath, DEFAULT_TICKS);
	}
	
	//MÉTODOS
	/**Lee el fichero .ini del flujo de entrada y parsea cada una de sus secciones en eventos que inserta en el simulador.
	 * Lanza IOException si hay algún problema en la lectura del fichero. Lanza IllegalArgumentException si no consigue
	 * parsear alguna sección.*/
	public void leerDatosSimulacion() throws IOException, IllegalArgumentException
	{
		//Cargamos todo el fichero en la variable ini (Puede lanzar IOException)
		Ini ini = new Ini(inputStream);
		
		//Parseamos uno a uno los eventos de las secciones
		for(IniSection s: ini.getSections())
				//Anidar la posible excepción lanzada ??
				simulador.insertaEvento(getEvento(s));	
		
		//Recordamos que en getEvento(s) se lanza una IllegalArgumentException si no se puede parsear alguna sección.
	}
	/***/
	public void escribirDatosSimulacion()
	{
		//Creo que esto puede morir sin problema si se pasa a TrafficSimulator.
	}
	/**Dada una IniSection la parsea para leer el evento que representa. Si no se consigue generar ningún evento de esta
	 * sección se lanza una excepción del tipo IllegalArgumentException.*/
	public Event getEvento(IniSection s) throws IllegalArgumentException
	{
		Event event;

		for(EventBuilder e: EventBuilderList)
		{
			//Parsea el evento con el correspondiente builder
			event = e.parse(s);
			
			//Si se consigue parsear correctamente lo devuelve
			if(event != null)
				return event;
		}
		
		//Si llega aquí es porque no ha podido parsear esta sección correctamente
		throw new IllegalArgumentException(new Throwable("No se ha podido parsear la sección:\n" + s.toString()));
	}
	
	
	//public void run() ??
	//{
	//	simulador.execute(outputStream, ticksSimulacion);
	//}
}






