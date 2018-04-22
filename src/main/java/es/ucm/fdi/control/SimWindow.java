package es.ucm.fdi.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;

import es.ucm.fdi.extra.tablecomponent.ComponentTable;
import es.ucm.fdi.extra.texteditor.TextEditor;
import es.ucm.fdi.model.objects.SimulatedObject.Describable;


public class SimWindow extends JFrame {
	
	TextEditor eventsArea;
	TextEditor reportsArea;
	JFileChooser fc;
	JSplitPane bottomSplit;
	JSplitPane topSplit;
	ComponentTable vehiclesTable;
	ComponentTable roadsTable;
	ComponentTable junctionsTable;
	
	
	public SimWindow() {
		super("Traffic Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fc = new JFileChooser();
		
		addLowerPanel();
		addUpperPanel();
		addBars();
		
		setSize(1000, 1000);
		
		pack();
		setVisible(true);
		
		topSplit.setDividerLocation(.3);
		bottomSplit.setDividerLocation(.5);
	}
	
	private void addBars() {
		
		// instantiate actions
	
		SimulatorAction cargar = new SimulatorAction(
				"Load Events", "open.png", "Cargar eventos",
				KeyEvent.VK_L, "control L",
				()-> loadFile(eventsArea));
		
		SimulatorAction guardar = new SimulatorAction(
				"Save Events", "save.png", "Guardar cosas",
				KeyEvent.VK_S, "control S", 
				()-> saveFile(eventsArea));
		
		SimulatorAction clear = new SimulatorAction(
				"Clear Events", "clear.png", "Borrar eventos",
				KeyEvent.VK_B, "control B",
				()-> eventsArea.setText(""));	
		
		SimulatorAction insertEvents = new SimulatorAction(
				"Insert Events", "events.png", "Inserta eventos en simulador",
				KeyEvent.VK_I, "control I",
				()-> System.out.println("Insertando..."));
		
		SimulatorAction executeSim = new SimulatorAction(
				"Run", "play.png", "Ejecutar simulador",
				KeyEvent.VK_E, "control E",
				()-> System.out.println("Ejecutando..."));
		
		SimulatorAction restartSim = new SimulatorAction(
				"Reset Sim", "reset.png", "Reiniciar simulador",
				KeyEvent.VK_R, "control R",
				()-> System.out.println("Reiniciando..."));
		
		SimulatorAction report = new SimulatorAction(
				"Generate", "report.png", "Genera reports",
				KeyEvent.VK_G, "control G",
				()-> System.out.println("Generando..."));
		
		SimulatorAction clearReport = new SimulatorAction(
				"Clear", "delete_report.png", "Borra reports",
				KeyEvent.VK_D, "control D",
				()-> reportsArea.setText(""));
		
		SimulatorAction saveReport = new SimulatorAction(
				"Save Report", "save_report.png", "Guarda reports",
				KeyEvent.VK_S, "control S",
				()-> System.out.println("Guardando..."));
		
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
	
	//Establece el layout básico de la práctica
	public void addUpperPanel() {
		
		JPanel upperPanel = new JPanel(new GridLayout(1, 3));
		
		topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, bottomSplit);
		
		add(topSplit);
		
		eventsArea = new TextEditor("Events", true, fc);
		TextEditor events1 = new TextEditor("Events Queue", false, fc);
		reportsArea = new TextEditor("Reports", false, fc);

		upperPanel.add(eventsArea);
		upperPanel.add(events1);
		upperPanel.add(reportsArea);
	
	}
	
	public void addLowerPanel() {
		
		JPanel graphPanel = new JPanel();
		JPanel tablePanel = new JPanel(new GridLayout(3, 1));
		
		bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, graphPanel);
		
		String[] vehicDescrib = {"ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary"};
		String[] roadDescrib = {"ID", "Source", "Target", "Lenght", "Max Speed", "Vehicles"};
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
	
	private void loadFile(TextEditor text) {
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String s = readFile(file);
			text.setText(s);
		}
	}

	public static String readFile(File file) {
		String s = "";
		try {
			s = new Scanner(file).useDelimiter("\\A").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return s;
	}
	
	private void saveFile(TextEditor text) {
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			writeFile(file, text.getText());
		}
	}
	
	public static void writeFile(File file, String content) {
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.print(content);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String ... args) {
		SwingUtilities.invokeLater(() -> new SimWindow());
	}
	
}