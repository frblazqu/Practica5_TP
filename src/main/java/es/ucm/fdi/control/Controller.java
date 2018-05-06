package es.ucm.fdi.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import es.ucm.fdi.model.TrafficSimulator;

/**
 * Controla toda la simulación que se va a ejecutar. Tiene las siguientes
 * responsabilidades: -> Crear el simulador. -> Leer los eventos de la
 * simulación. -> Manejo de gran parte de excepciones lanzadas en niveles
 * inferiores.
 * 
 * @author Francisco Javier Blázquez
 * @version 23/03/18
 */
public class Controller {
	// ATRIBUTOS POR DEFECTO
	private final static int DEFAULT_TICKS = 10;
	private final static String DEFAULT_INI_FILE = "iniFile.ini";
	private final static String DEFAULT_OUT_FILE = "outFile.ini";
	private final static String DEFAULT_READ_DIRECTORY = "src/main/resources/readStr/";
	private final static String DEFAULT_WRITE_DIRECTORY = "src/main/resources/writeStr/";

	// ATRIBUTOS DE LA CLASE
	private int ticksSimulacion; // Duración de la simulación
	private OutputStream outputStream; // Flujo de salida de informes de la
										// simulación
	private InputStream inputStream; // Flujo de entrada de datos para la
										// simulación
	private TrafficSimulator simulador; // Simulador a controlar
	private String inputPath;
	// private EventFactory eventBuilder; //Para parsear iniSection como events

	// CONSTRUCTORAS
	/**
	 * Crea un nuevo simulador y nuevos flujos de entrada salida con los
	 * parámetros recibidos.
	 * 
	 * @param loadFilePath
	 *            localización del fichero de entrada de eventos
	 * @param saveFilePath
	 *            localización del fichero de escritura de informes
	 * @param numTicks
	 *            duración de la simulación a ejecutar
	 * 
	 */
	public Controller(String loadFilePath, String saveFilePath, int numTicks) {
		try {
			inputPath = loadFilePath;
			inputStream = new FileInputStream(new File(loadFilePath));
			outputStream = new FileOutputStream(new File(saveFilePath));
			simulador = new TrafficSimulator();
			ticksSimulacion = numTicks;
		} catch (Exception e) {
			System.err.println("Error al crear el controlador.");
		}
	}
	/**
	 * Crea un nuevo simulador con entrada de eventos, flujo de salida y
	 * duración de la simulación por defecto. Llama a la constructora más
	 * general con los parámetros por defecto. Entrada por defecto ->
	 * src/main/resources/readStr/iniFile.ini Salida por defecto ->
	 * src/main/resources/writeStr/outFile.ini Duración simulación por defecto
	 * -> 10
	 * 
	 * @see #Controller(String, String, int)
	 */
	public Controller() {
		this(DEFAULT_READ_DIRECTORY + DEFAULT_INI_FILE/**/, DEFAULT_WRITE_DIRECTORY
				+ DEFAULT_OUT_FILE /**/, DEFAULT_TICKS);
	}
	/**
	 * Llama a la constructora más general con el tiempo por defecto. Duración
	 * simulación por defecto -> 10 ticks
	 * 
	 * @see #Controller(String, String, int)
	 * @throws FileNotFoundException
	 */
	public Controller(String loadFilePath, String saveFilePath) {
		this(loadFilePath, saveFilePath, DEFAULT_TICKS);
	}

	/**
	 * Crea el controlador para la simulación en modo GUI. Si no se especifica
	 * el fichero de eventos a cargar, no se inicializa el inputStream.
	 * 
	 * @param loadFilePath
	 *            path del fichero a cargar
	 * @param numTicks
	 *            número de ticks al que inicializar el simulador
	 */
	public Controller(String loadFilePath, int numTicks) {
		try {
			inputPath = loadFilePath;
			if (!loadFilePath.equals(DEFAULT_READ_DIRECTORY + DEFAULT_INI_FILE)) {
				inputStream = new FileInputStream(new File(loadFilePath));
			} else {
				inputStream = null;
			}
			outputStream = null;
			simulador = new TrafficSimulator();
			ticksSimulacion = numTicks;
		} catch (Exception e) {
			System.err.println("Error al cargar el controlador");
		}
	}

	// MÉTODOS
	/**
	 * Lee el fichero .ini del flujo de entrada y parsea cada una de sus
	 * secciones en eventos que inserta en el simulador. Dejamos que se lancen
	 * las excepciones sin ser modificadas para que lleguen a donde se crea el
	 * objeto controller.
	 * 
	 * @throws IOException
	 *             Si no se consigue leer correctamente el fichero de entrada.
	 * @throws IllegalArgumentException
	 *             Si alguna sección no se consigue parsear bien.
	 */
	public void leerDatosSimulacion() {
		try {
			simulador.leerDatosSimulacion(inputStream);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Ejecuta la simulación.
	 * 
	 * @see TrafficSimulator#ejecuta(int, OutputStream)
	 * @throws IOException
	 *             si hay problemas de escritura de reports de la simulación.
	 * @throws IllegalArgumentException
	 *             si algún parámetro de la simulación no es válido.
	 * */
	public void run() {
		simulador.ejecuta(ticksSimulacion, outputStream);
	}

	public TrafficSimulator simulador() {
		return simulador;
	}
	public void ejecutaKPasos(int k) {
		simulador.ejecuta(k, outputStream);
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public OutputStream getOutputStream() {
		return outputStream;
	}
	public int getTicksSim() {
		return ticksSimulacion;
	}
	public String getInputPath() {
		return inputPath;
	}
	public void setOutputStream(OutputStream flujoEscritura) {
		outputStream = flujoEscritura;
	}
}
