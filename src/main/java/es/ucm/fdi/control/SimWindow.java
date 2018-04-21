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
import java.util.Scanner;

import javax.swing.*;

import es.ucm.fdi.extra.texteditor.TextEditor;

//Prueba
public class SimWindow extends JFrame {
	
	TextEditor eventsArea;
	TextEditor reportsArea;
	JFileChooser fc;
	JSplitPane bottomSplit;
	JSplitPane topSplit;
	
	public SimWindow() {
		super("Traffic Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fc = new JFileChooser();
		
		addPanels();
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
				"Execute Sim", "play.png", "Ejecutar simulador",
				KeyEvent.VK_E, "control E",
				()-> System.out.println("Ejecutando..."));
		
		SimulatorAction restartSim = new SimulatorAction(
				"Reset Sim", "reset.png", "Reiniciar simulador",
				KeyEvent.VK_R, "control R",
				()-> System.out.println("Reiniciando..."));
		
		SimulatorAction salir = new SimulatorAction(
				"Exit", "exit.png", "Salir de la aplicacion",
				KeyEvent.VK_A, "control shift X", 
				()-> System.exit(0));
		
		// add actions to toolbar, and bar to window.
		JToolBar bar = new JToolBar();
		bar.add(cargar);
		bar.add(guardar);
		bar.add(clear);
		bar.add(insertEvents);
		bar.add(executeSim);
		bar.add(restartSim);
		bar.add(salir);
		add(bar, BorderLayout.NORTH);

		// add actions to menubar, and bar to window
		JMenu file = new JMenu("File");
		file.add(cargar);
		file.add(guardar);
		file.addSeparator();
		file.add(salir);		
		JMenu simulator = new JMenu("Simulator");
		//añadir acciones de simulator
		JMenuBar menu = new JMenuBar();
		menu.add(file);
		menu.add(simulator);
		setJMenuBar(menu);
	}
	
	//Establece el layout básico de la práctica
	public void addPanels() {
		
		JPanel upperPanel = new JPanel(new GridLayout(1, 3));
		
		JPanel graphPanel = new JPanel();
		
		JPanel tablePanel = new JPanel(new GridLayout(3, 1));
		
		bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, graphPanel);
		topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, bottomSplit);
		
		add(topSplit);
		
		eventsArea = new TextEditor("Events", true, fc);
		TextEditor events1 = new TextEditor("Events Queue", false, fc);
		reportsArea = new TextEditor("Reports", false, fc);
		TextEditor events3 = new TextEditor("Events", false, fc);
		TextEditor events4 = new TextEditor("Events", false, fc);
		TextEditor events5 = new TextEditor("Events", false, fc);
		tablePanel.add(events3);
		tablePanel.add(events4);
		tablePanel.add(events5);
		upperPanel.add(eventsArea);
		upperPanel.add(events1);
		upperPanel.add(reportsArea);
	
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