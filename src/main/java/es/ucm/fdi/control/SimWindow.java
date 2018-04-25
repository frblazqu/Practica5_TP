package es.ucm.fdi.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import es.ucm.fdi.extra.graphlayout.Dot;
import es.ucm.fdi.extra.graphlayout.Edge;
import es.ucm.fdi.extra.graphlayout.Graph;
import es.ucm.fdi.extra.graphlayout.GraphComponent;
import es.ucm.fdi.extra.graphlayout.Node;
import es.ucm.fdi.extra.tablecomponent.ComponentTable;
import es.ucm.fdi.extra.texteditor.TextEditor;
import es.ucm.fdi.model.Describable;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.TrafficSimulator.UpdateEvent;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.objects.Junction;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.Vehicle;


public class SimWindow extends JFrame implements TrafficSimulator.Listener {
	
	private final static String INPUT_FILE = "C:/Users/Usuario/Desktop/Repositorios/Practica5_TP/src/main/resources/readStr/examples/basic/"
										   + "10_crossRoadMultipleVehicles.ini";
	JFileChooser fc;
	JSplitPane bottomSplit;
	JSplitPane mainPanel;
	
	JLabel statusBarReport;
	JPanel graphPanel;
	GraphComponent graphComp;
	TextEditor eventsArea;
	TextEditor reportsArea;
	ComponentTable eventsQueue;
	ComponentTable vehiclesTable;
	ComponentTable roadsTable;
	ComponentTable junctionsTable;
	JTextField timeText;
	
	Controller control;
	
	public SimWindow() {
		super("Traffic Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("INI File","ini"));
		
		control = new Controller(INPUT_FILE ,"src/main/resources/writeStr/"+ "auxiliar.ini");
		
		addLowerPanel();
		addUpperPanel();
		addBars();
		
		add(mainPanel);
		
		pack();
		setVisible(true);
		
		mainPanel.setDividerLocation(.3);	//30% de espacio al panel superior
		mainPanel.setResizeWeight(.3);		//A pesar de que cambiemos la ventana
		bottomSplit.setDividerLocation(.5);	//50% de espacio para tablas en el panel inferior
		bottomSplit.setResizeWeight(0.5);	//A pesar de que cambiemos la ventana
		
		control.simulador().addSimulatorListener(this);
	}
	
	public SimWindow(Controller c) {
		super("Traffic Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("INI File","ini"));
		
		control = c;
		
		addLowerPanel();
		addUpperPanel();
		addBars();
		
		add(mainPanel);
		
		pack();
		setVisible(true);
		
		mainPanel.setDividerLocation(.3);	//30% de espacio al panel superior
		mainPanel.setResizeWeight(.3);		//A pesar de que cambiemos la ventana
		bottomSplit.setDividerLocation(.5);	//50% de espacio para tablas en el panel inferior
		bottomSplit.setResizeWeight(0.5);	//A pesar de que cambiemos la ventana
		
		control.simulador().addSimulatorListener(this);
	}
	
	/* MÉTODOS DE INICIALIZACIÓN. */
	/**
	 * Añade un menuBar y toolBar a la ventana con las acciones predefinidas. 
	 */
	private void addBars() {
		
		JLabel steps = new JLabel(" Steps: ");
		JSpinner stepsSpinner = new JSpinner(new SpinnerNumberModel(control.getTicksSim(), 1, 1000, 1));
		
		JLabel time = new JLabel(" Time: ");
		JTextField timeText = new JTextField("0");	
		timeText.setPreferredSize(new Dimension(75, 10));
		timeText.setEnabled(false);
		
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
		
		SimulatorAction executeSim = new SimulatorAction(
				"Run", "play.png", "Ejecutar simulador",
				KeyEvent.VK_E, "control E",
				()-> control.ejecutaKPasos((Integer) stepSpinner.getValue()));
		
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
		
		JCheckBoxMenuItem redirectOutput = new JCheckBoxMenuItem("Redirect output");
		redirectOutput.addActionListener((e)-> {
			if(control.getOutputStream() == null)
				control.setOutputStream(reportsArea.flujoEscritura());
			else
				control.setOutputStream(null);
		});
		
		// add actions to toolbar, and bar to window.
		JToolBar bar = new JToolBar();
		bar.add(cargar);
		bar.add(guardar);
		bar.add(clear);
		bar.add(insertEvents);
		bar.add(executeSim);
		bar.add(restartSim);
		bar.add(steps);
		bar.add(stepsSpinner);
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
		simulator.add(redirectOutput);
		
		JMenu reports = new JMenu("Reports");
		reports.add(report);
		reports.add(clearReport);
		
		//añadir acciones de simulator
		JMenuBar menu = new JMenuBar();
		menu.add(file);
		menu.add(simulator);
		menu.add(reports);
		setJMenuBar(menu);
		
		//añadir statusBar al final
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));		//Aparentemente más bajo, efecto visual.
		statusBar.setPreferredSize(new Dimension(this.getWidth(), 25)); //Ajustamos dimensiones
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));//Boxlayout en el eje x. Los pone a continuación
		add(statusBar, BorderLayout.SOUTH);
		
		JLabel statusLabel = new JLabel("Status: ");					//Etiqueta con en la izquierda
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusBar.add(statusLabel);
		
		statusBarReport = new JLabel("");
		statusBar.add(statusBarReport);
	}
	/**
	 * Inicializa el panel superior con los paneles de eventos y de informes. 
	 */
	public void addUpperPanel() {
		
		JPanel upperPanel = new JPanel(new GridLayout(1, 3));
		
		mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, bottomSplit);
		
		eventsArea = new TextEditor("Events", true, fc);
		eventsArea.setText(TextEditor.readFile(new File(control.getInputPath())));
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
		
		JPanel tablePanel = new JPanel(new GridLayout(3, 1));
		String[] vehicDescrib = {"ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary"};
		String[] roadDescrib  = {"ID", "Source", "Target", "Lenght", "Max Speed", "Vehicles"};
		String[] junctDescrib = {"ID", "Green", "Red"};
		
		//Lista vacía (luego se pondría la lista de simObject respectiva)
		List<Describable> l = new ArrayList<>();		
		vehiclesTable  = new ComponentTable(vehicDescrib, l, "Vehicles");
		roadsTable     = new ComponentTable(roadDescrib, l, "Roads");
		junctionsTable = new ComponentTable(junctDescrib, l, "Junctions");
		
		tablePanel.add(vehiclesTable);
		tablePanel.add(roadsTable);
		tablePanel.add(junctionsTable);
	
		graphComp = new GraphComponent();
		graphComp.setPreferredSize(new Dimension (400,600));
		
		bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, graphComp);
	}
	
	public void generateGraph(UpdateEvent ue)
	{
		Graph g = new Graph();		
		
		Map<Junction, Node> js = new HashMap<>();
		for (Junction j : ue.getRoadMap().getJunctions()) 
		{
			Node n = new Node(j.getId());
			js.put(j, n); // <-- para convertir Junction a Node en aristas
			g.addNode(n);
		}
		for (Road r : ue.getRoadMap().getRoads()) 
		{				
			Edge e = new Edge(r.getId(), js.get(r.getJunctionIni()), js.get(r.getJunctionFin()), r.getLongitud());
			
			for(Vehicle v: r.vehicles())
				e.addDot(new Dot(v.getId(), v.getLocalizacion()));			
			
			g.addEdge(e);
		}
		
		graphComp.setGraph(g);
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
    
		List<Describable> v = (List<Describable>)(List) ue.getRoadMap().getVehicles();
		List<Describable> r = (List<Describable>)(List) ue.getRoadMap().getRoads();
		List<Describable> j = (List<Describable>)(List) ue.getRoadMap().getJunctions();
		
		vehiclesTable.setElementsList(v);
		roadsTable.setElementsList(r);
		junctionsTable.setElementsList(j);
		
		generateGraph(ue);
		
		statusBarReport.setText(" Se ha vinculado correctamente al simulador.");
	}
	public void reset(UpdateEvent ue)
	{
		List<Describable> v = (List<Describable>)(List) ue.getRoadMap().getVehicles();
		List<Describable> r = (List<Describable>)(List) ue.getRoadMap().getRoads();
		List<Describable> j = (List<Describable>)(List) ue.getRoadMap().getJunctions();
		
		vehiclesTable.setElementsList(v);
		roadsTable.setElementsList(r);
		junctionsTable.setElementsList(j);
		
		vehiclesTable.updateTable();
		roadsTable.updateTable();
		junctionsTable.updateTable();
		
		timeText.setText("0");
		reportsArea.setText("");
    generateGraph(ue);
		
		statusBarReport.setText(" Se ha reiniciado el estado de la simulación.");
	}
	public void newEvent(UpdateEvent ue)
	{
		//Aquí debemos coger el evento añadido al simulador y cargarlo en nuestras tablas
		
		List<Event> eventQueue = ue.getEvenQueue();
		Event event = eventQueue.get(eventQueue.size()-1);
		
		eventsQueue.addElement(event);
		
		statusBarReport.setText(" Se han añadido eventos al simulador.");
	}
	public void advanced(UpdateEvent ue)
	{
		vehiclesTable.updateTable();
		roadsTable.updateTable();
		junctionsTable.updateTable();
		
		String nextTime = "" + (Integer.valueOf(timeText.getText()) + 1);
		timeText.setText(nextTime);
		generateGraph(ue);

		statusBarReport.setText(" Se ha avanzado en el estado de la simulación");
	}
	public void error(UpdateEvent ue, String error)
	{
		JOptionPane.showMessageDialog(this, error + "\n Reinicie el estado del simulador para volver al funcionamiento normal.",
									  "Fallo en la simulación!", JOptionPane.ERROR_MESSAGE);
		
		statusBarReport.setText("ERROR! Reinicie el simulador para poder volver al funcionamiento normal del sistema.");
		statusBarReport.setForeground(Color.red);
	}
	
}