package es.ucm.fdi.control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import es.ucm.fdi.extra.tablecomponent.ComponentTable;
import es.ucm.fdi.extra.texteditor.TextEditor;
import es.ucm.fdi.model.objects.SimulatedObject.Describable;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.TrafficSimulator.UpdateEvent;


public class SimWindow extends JFrame implements TrafficSimulator.Listener {
	
	private final static String INPUT_FILE = "C:/Users/Usuario/Desktop/Repositorios/Practica5_TP/src/main/resources/readStr/examples/basic/"
										   + "00_helloWorld.ini";
	JFileChooser fc;
	JSplitPane bottomSplit;
	JSplitPane topSplit;
	
	TextEditor eventsArea;
	TextEditor reportsArea;
	ComponentTable eventsQueue;
	ComponentTable vehiclesTable;
	ComponentTable roadsTable;
	ComponentTable junctionsTable;
	
	Controller control;
	
	public SimWindow() {
		super("Traffic Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("INI File","ini"));
		
		addLowerPanel();
		addUpperPanel();
		addBars();
		
		setSize(1000, 1000);
		
		pack();
		setVisible(true);
		
		topSplit.setDividerLocation(.3);
		bottomSplit.setDividerLocation(.5);
		
		control = new Controller(INPUT_FILE ,"src/main/resources/writeStr/"+ "auxiliar.ini");
	}
	
	/* MÉTODOS DE INICIALIZACIÓN. */
	/**
	 * Añade un menuBar y toolBar a la ventana con las acciones predefinidas. 
	 */
	private void addBars() {
		
		// instantiate actions
		SimulatorAction cargar = new SimulatorAction(
				"Load Events", "open.png", "Cargar eventos",
				KeyEvent.VK_L, "control L",
				()-> eventsArea.loadFile());
		
		SimulatorAction guardar = new SimulatorAction(
				"Save Events", "save.png", "Guardar cosas",
				KeyEvent.VK_S, "control S", 
				()-> eventsArea.saveFile());
		
		SimulatorAction clear = new SimulatorAction(
				"Clear Events", "clear.png", "Borrar eventos",
				KeyEvent.VK_B, "control B",
				()-> eventsArea.setText(""));	
		
		SimulatorAction insertEvents = new SimulatorAction(
				"Insert Events", "events.png", "Inserta eventos en simulador",
				KeyEvent.VK_I, "control I",
				()-> control.simulador().leerDatosSimulacion(eventsArea.flujoLectura()));
				//Aquí además habrá que hacer que lo guarde en un fichero auxiliar y
				//cargar la simulación de este. O leerlo del eventsArea.
		
		SimulatorAction executeSim = new SimulatorAction(
				"Run", "play.png", "Ejecutar simulador",
				KeyEvent.VK_E, "control E",
				()-> control.ejecutaUnPaso());
				//Aquí no debe ejecutar un paso sino que debe ejecutar tantos como haya
				//de diferencia entre el número del JSpinner y el del reloj actual.
		
		SimulatorAction restartSim = new SimulatorAction(
				"Reset Sim", "reset.png", "Reiniciar simulador",
				KeyEvent.VK_R, "control R",
				()-> control.simulador().reset());
		
		SimulatorAction report = new SimulatorAction(
				"Generate", "report.png", "Genera reports",
				KeyEvent.VK_G, "control G",
				()-> control.simulador().generaInforme(reportsArea.flujoEscritura()));
		
		SimulatorAction clearReport = new SimulatorAction(
				"Clear", "delete_report.png", "Borra reports",
				KeyEvent.VK_D, "control D",
				()-> reportsArea.setText(""));
		
		SimulatorAction saveReport = new SimulatorAction(
				"Save Report", "save_report.png", "Guarda reports",
				KeyEvent.VK_S, "control S",
				()-> reportsArea.saveFile());
		
		SimulatorAction salir = new SimulatorAction(
				"Exit", "exit.png", "Salir de la aplicacion",
				KeyEvent.VK_A, "control shift X", 
				()-> System.exit(0));
		
		JLabel steps = new JLabel(" Steps: ");
		JSpinner stepSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
		
		JLabel time = new JLabel(" Time: ");
		JTextField timeText = new JTextField("0");	
		timeText.setPreferredSize(new Dimension(75, 10));
		timeText.setEnabled(false);
		
		// add actions to toolbar, and bar to window.
		JToolBar bar = new JToolBar();
		bar.add(cargar);
		bar.add(guardar);
		bar.add(clear);
		bar.add(insertEvents);
		bar.add(executeSim);
		bar.add(restartSim);
		bar.add(steps);
		bar.add(stepSpinner);
		bar.add(time);
		bar.add(timeText);
		bar.add(report);
		bar.add(clearReport);
		bar.add(saveReport);
		bar.add(salir);
		add(bar, BorderLayout.NORTH);

		// add actions to menubar, and bar to window
		JMenu file = new JMenu("File");
		file.add(cargar);
		file.add(guardar);
		file.addSeparator();
		file.add(salir);	
		
		JMenu simulator = new JMenu("Simulator");
		simulator.add(executeSim);
		simulator.add(restartSim);
		
		JMenu reports = new JMenu("Reports");
		reports.add(report);
		reports.add(clearReport);
		
		//añadir acciones de simulator
		JMenuBar menu = new JMenuBar();
		menu.add(file);
		menu.add(simulator);
		menu.add(reports);
		setJMenuBar(menu);
	}
	/**
	 * Inicializa el panel superior con los paneles de eventos y de informes. 
	 */
	public void addUpperPanel() {
		
		JPanel upperPanel = new JPanel(new GridLayout(1, 3));
		
		topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, bottomSplit);
		
		add(topSplit);
		
		eventsArea = new TextEditor("Events", true, fc);
		eventsArea.setText(TextEditor.readFile(new File(INPUT_FILE)));
		reportsArea = new TextEditor("Reports", false, fc);
		
		List<Describable> l = new ArrayList<>();
		String[] eventsDescrib = {"#", "Time", "Type"};
		eventsQueue = new ComponentTable(eventsDescrib, l, "Events Queue");
		
		upperPanel.add(eventsArea);
		upperPanel.add(eventsQueue);
		upperPanel.add(reportsArea);
	}
	/**
	 * Inicializa el panel inferior con las tablas de los objetos y el grafo de la simulación. 
	 */
	public void addLowerPanel() {
		
		JPanel graphPanel = new JPanel();
		JPanel tablePanel = new JPanel(new GridLayout(3, 1));
		
		bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, graphPanel);
		
		String[] vehicDescrib = {"ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary"};
		String[] roadDescrib  = {"ID", "Source", "Target", "Lenght", "Max Speed", "Vehicles"};
		String[] junctDescrib = {"ID", "Green", "Red"};
		
		//Lista vacía (luego se pondría la lista de simObject respectiva)
		List<Describable> l = new ArrayList<>();		
		vehiclesTable = new ComponentTable(vehicDescrib, l, "Vehicles");
		roadsTable = new ComponentTable(roadDescrib, l, "Roads");
		junctionsTable = new ComponentTable(junctDescrib, l, "Junctions");
		
		tablePanel.add(vehiclesTable);
		tablePanel.add(roadsTable);
		tablePanel.add(junctionsTable);
		
	}
	
	/* MAIN */
	public static void main(String ... args) {
		SwingUtilities.invokeLater(() -> new SimWindow());
	}
	
	/* MÉTODOS DE LISTENER */
	public void update(UpdateEvent ue, String error)
	{
		switch(ue.getType())
		{
		case ADVANCED: 		advanced(ue);		break;
		case NEW_EVENT:		newEvent(ue);		break;
		case ERROR:			error(ue, error);   break;
		case REGISTERED:	registered(ue); 	break;
		case RESET:			reset(ue);			break;
		}
	}
	public void registered(UpdateEvent ue)
	{
		//control.simulador().addSimulatorListener(this);
	}
	public void reset(UpdateEvent ue)
	{
		//Aquí tenemos que refrescar las tablas y el grafo
	}
	public void newEvent(UpdateEvent ue)
	{
		//Aquí debemos coger el evento añadido al simulador y cargarlo en nuestras tablas
	}
	public void advanced(UpdateEvent ue)
	{
		//Aquí debemos refrescar las tablas
	}
	public void error(UpdateEvent ue, String error)
	{
		//Aquí debemos notificar el error y rezar
	}
	
}