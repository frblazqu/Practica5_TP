package es.ucm.fdi.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.control.SimWindow;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.model.exceptions.SimulationFailedException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ExampleMain {

	// ATRIBUTOS POR DEFECTO
	private final static Integer DEFAULT_TIME_VALUE = 10;
	private final static String DEFAULT_READ_DIRECTORY = "src/main/resources/readStr/";
	private final static String DEFAULT_WRITE_DIRECTORY = "src/main/resources/writeStr/";
	private final static String DEFAULT_INI_FILE = "iniFile.ini";
	private final static String DEFAULT_OUT_FILE = "outFile.ini";
	private final static String DEFAULT_SIM_MODE = "batch";

	// ATRIBUTOS DE LA CLASE
	public static Integer _timeLimit = DEFAULT_TIME_VALUE; // Duración de las
															// simulaciones a
															// ejecutar
	public static String _inFile = DEFAULT_INI_FILE; // Fichero de entrada del
														// que leer los datos
	public static String _outFile = DEFAULT_OUT_FILE; // Fichero en el que
														// escribir los datos
	public static String _simMode = DEFAULT_SIM_MODE; // Modo de ejecución

	// MÉTODOS
	/**
	 * DUDAS:
	 *
	 * ¿Por qué parsea un array y no un string? ¿Presupone split? ¿Clase
	 * Options? ¿Clase CommandLineParser? ¿Clase CommandLine?
	 *
	 * Ya no es que me interese saber exactamente que hacen estos métodos pero
	 * si que quiero saber por qúe no se ha tenido que implementar el parser, lo
	 * ha cogido ya hecho. ¿De dónde lo ha sacado? ¿Cómo puedo verlo? En
	 * definitiva buscar las librerías org.apache.commons y ver que se cuece por
	 * ahí. ¿Se puede hacer esto sin maven? Dudas para el bueno de ManuFreire.
	 */
	public static void parseArgs(String[] args) {
		// SI HAY ALGÚN ERROR DE PARSEO HACE QUE TODO TERMINE (VER CATCH FINAL)

		// define the valid command line options
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		CommandLineParser parser = new DefaultParser();
		try {
			// Dados los argumentos los parsea con sus comandos correspondientes
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseOutFileOption(line);
			parseStepsOption(line);
			parseModeOption(line);

			// Aquí ya está todo parseado, es solo que no haya basura y
			// controlar casos raros

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";

				for (String o : remaining)
					error += (" " + o);

				throw new ParseException(error);
			}

		} catch (ParseException e) {
			// new Piece(...) might throw GameError exception
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}
	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// Definimos las que van a ser las opciones/comandos válidos:
		cmdLineOptions.addOption(
				Option.builder("h").longOpt("help").desc("Print this message").build());
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg()
				.desc("Events input file").build());
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg()
				.desc("Output file, where reports are written.").build());
		cmdLineOptions.addOption(Option.builder("t").longOpt("ticks").hasArg()
				.desc("Ticks to execute the simulator's main loop (default value is "
						+ DEFAULT_TIME_VALUE + ").")
				.build());
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc(
				"’batch’ for batch mode and ’gui’ for GUI mode (default value is ’batch’)")
				.build());

		return cmdLineOptions;
	}
	/**
	 * Si la linea de comandos introducidos contiene la opción de solicitar
	 * ayuda, muestra el mensaje de ayuda y termina la ejecución.
	 */
	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(ExampleMain.class.getCanonicalName(), cmdLineOptions,
					true);
			System.exit(0);
		}
	}
	/**
	 * Parsea el nombre de fichero del que leer los datos. Presupone que este se
	 * sitúa en {@value #DEFAULT_READ_DIRECTORY}. Inicializa {@link #_inFile} a
	 * la ruta del fichero. Si no se introduce un nombre de fichero se toma por
	 * defecto {@value #DEFAULT_INI_FILE}.
	 * 
	 * @throws ParseException
	 *             Si no se parsea correctamente el fichero de entrada.
	 */
	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = DEFAULT_READ_DIRECTORY + line.getOptionValue("i", DEFAULT_INI_FILE);
		if (_inFile == null) {
			throw new ParseException("An events file is missing");
		}
	}
	/**
	 * Parsea el nombre de fichero de escritura. Presupone que este se sitúa en
	 * {@value #DEFAULT_WRITE_DIRECTORY}. Inicializa {@link #_outFile} a la ruta
	 * del fichero. Si no se introduce un nombre de fichero se toma por defecto
	 * {@value #DEFAULT_OUT_FILE}.
	 * 
	 * @throws ParseException
	 *             Si no se parsea correctamente el fichero de SALIDA.
	 */
	private static void parseOutFileOption(CommandLine line) throws ParseException {
		_outFile = DEFAULT_WRITE_DIRECTORY + line.getOptionValue("o", DEFAULT_OUT_FILE);
	}
	/**
	 * Parsea el tiempo de la simulación. Inicializa {@link #_timeLimit} a
	 * {@value #DEFAULT_TIME_VALUE} por defecto si no se introduce un tiempo en
	 * los comandos.
	 * 
	 * @throws ParseException
	 *             Si no se parsea bien el tiempo.
	 */
	private static void parseStepsOption(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", DEFAULT_TIME_VALUE.toString());
		try {
			_timeLimit = Integer.parseInt(t);
			assert (_timeLimit > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time limit: " + t);
		}
	}
	/**
	 * Parsea el modo de ejecución de la simulación. Si no se introduce nada lo
	 * inicializa a default ("batch").
	 * 
	 * @throws ParseException
	 *             Si no se parsea bien el modo.
	 *
	 */
	public static void parseModeOption(CommandLine line) throws ParseException {
		String m = line.getOptionValue("m", DEFAULT_SIM_MODE);
		if (m.equals(DEFAULT_SIM_MODE) && m.equals("gui")) {
			throw new ParseException("Invalid value for Sim Mode: " + m);
		} else {
			_simMode = m;
		}
	}
	/**
	 * This method run the simulator on all files that ends with .ini if the
	 * given path, and compares that output to the expected output. It assumes
	 * that for example "example.ini" the expected output is stored in
	 * "example.ini.eout". The simulator's output will be stored in
	 * "example.ini.out"
	 * 
	 * @throws IOException
	 */
	private static void test(String path) throws IOException {

		File dir = new File(path);

		if (!dir.exists()) {
			throw new FileNotFoundException(path);
		}

		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".ini");
			}
		});

		for (File file : files) {
			test(file.getAbsolutePath(), file.getAbsolutePath() + ".out",
					file.getAbsolutePath() + ".eout", DEFAULT_TIME_VALUE);
		}

	}
	/**
	 * Ejecuta la simulación partiendo de un fichero ".ini" y compara el fichero
	 * de salida ".out" con su homónimo pero terminado en ".eout". Muestra por
	 * controla si la salida esperada (eout) es igual a la salida de la
	 * simulación (out).
	 * 
	 * Presupone que el fichero a leer, el de salida generado y el de salida
	 * esperada están en el mismo repositorio.
	 * 
	 */
	private static void test(String inFile, String outFile, String expectedOutFile,
			int timeLimit) throws IOException {
		// Parámetros para el controlador que vamos a crear
		_outFile = outFile;
		_inFile = inFile;
		_timeLimit = timeLimit;

		try {
			// Ejecutamos la simulación
			startBatchMode();

			// Comparamos la salida generada y la esperada
			boolean equalOutput = (new Ini(_outFile)).equals(new Ini(expectedOutFile));

			// Mostramos el resultado obtenido
			System.out.println(
					"Result for: '" + _inFile.substring(_inFile.lastIndexOf('\\') + 1)
							+ "':\n" + "-> " + (equalOutput ? "OK!" : ("FAIL! :(")));

		} catch (SimulationFailedException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			// Si está aquí no es por startBatchMode, ha fallado la comparación
			// o en especial
			// _inFile.substring(_inFile.lastIndexOf('\\') + 1) (ver última
			// línea del try)

			System.out.println(
					"\nHa ocurrido un error comprobando la corrección de la simulación:\n");
			e.printStackTrace();
		}
	}
	/**
	 * Run the simulator in batch mode.
	 * 
	 * @throws SimulationFailedException
	 *             If the simulation ended abruptly to notice the cause.
	 */
	private static void startBatchMode() throws SimulationFailedException {
		try {
			Controller controller = new Controller(_inFile, _outFile, _timeLimit);
			controller.leerDatosSimulacion();
			controller.run();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SimulationFailedException(_inFile, _outFile, _timeLimit, e);

			/*
			 * Construye una excepción en la que pone: Mira con estos parámetros
			 * no hemos podido ejecutar la simulación. El motivo ha sido este: y
			 * pinta el mensaje de la excepción.
			 */
		}
	}
	/**
	 * Run the simulator in GUI mode.
	 * 
	 * @throws SimulationFailedException
	 *             If the simulation ended abruptly to notice the cause.
	 */
	private static void startGUIMode() throws SimulationFailedException {
		try {
			Controller controller = new Controller(_inFile, _timeLimit);
			SwingUtilities.invokeLater(() -> new SimWindow(controller));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SimulationFailedException(_inFile, _outFile, _timeLimit, e);

			/*
			 * Construye una excepción en la que pone: Mira con estos parámetros
			 * no hemos podido ejecutar la simulación. El motivo ha sido este: y
			 * pinta el mensaje de la excepción.
			 */
		}
	}
	/**
	 * Parsea los argumentos introducidos y ejecuta la simulación partiendo de
	 * estos.
	 * 
	 * @throws IllegalArgumentException
	 *             Si no se parsean correctamente estos argumentos.
	 * @throws IOException
	 *             Si no se pueden leer los eventos en la ruta especificada.
	 */
	private static void start(String[] args) {
		try {
			parseArgs(args);
			if (_simMode.equals("batch")) {
				startBatchMode();
			} else if (_simMode.equals("gui")) {
				startGUIMode();
			}
		} catch (SimulationFailedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(
					"Se ha producido un fallo durante el parseo de argumentos de entrada:\n"
							+ args);
		}
	}

	public static void main(String[] args)
			throws IOException, InvocationTargetException, InterruptedException {
		// example command lines:
		//
		// -i resources/examples/events/basic/ex1.ini
		// -i resources/examples/events/basic/ex1.ini -o ex1.out
		// -i resources/examples/events/basic/ex1.ini -t 20
		// -i resources/examples/events/basic/ex1.ini -o ex1.out -t 20
		// --help
		//

		// Call test in order to test the simulator on all examples in a
		// directory.
		//
		test(DEFAULT_READ_DIRECTORY + "examples/basic/");
		test(DEFAULT_READ_DIRECTORY + "examples/advanced/");

		// Call start to start the simulator from command line, etc.
		// start(args);
	}

	// MÉTODOS QUE SOLO DEBEN SER USADOS PARA EL TESTEO
	public static int getTime() {
		return _timeLimit;
	}
	public static String getInFile() {
		return _inFile;
	}
	public static String getOutFile() {
		return _outFile;
	}
}
