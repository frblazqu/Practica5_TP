package es.ucm.fdi.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.Border;

import es.ucm.fdi.extra.texteditor.TextEditor;
import es.ucm.fdi.extra.texteditor.TextEditorExample;

/**
 * Esto es sólo para empezar a jugar con las interfaces
 * de la P5. 
 * 
 * El código <i>no</i> está bien organizado, y meter toda
 * la funcionalidad aquí sería un disparate desde un punto
 * de vista de mantenibilidad.
 */

public class SimWindow extends JFrame {
	public SimWindow() {
		super("Traffic Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addBars();
		addPanels();
		
		setSize(1000, 1000);
		
		pack();
		setVisible(true);
	}
	
	private void addBars() {
		// instantiate actions
		SimulatorAction cargar = new SimulatorAction(
				"Load Events", "open.png", "Cargar eventos",
				KeyEvent.VK_L, "control L",
				()-> System.err.println("cargando..."));
		SimulatorAction guardar = new SimulatorAction(
				"Save Events", "save.png", "Guardar cosas",
				KeyEvent.VK_S, "control S", 
				()-> System.err.println("guardando..."));
		SimulatorAction clear = new SimulatorAction(
				"Clear Events", "clear.png", "Borrar eventos",
				KeyEvent.VK_B, "control B",
				()-> System.err.println("borrando..."));		
		SimulatorAction salir = new SimulatorAction(
				"Exit", "exit.png", "Salir de la aplicacion",
				KeyEvent.VK_A, "control shift X", 
				()-> System.exit(0));
		
		// add actions to toolbar, and bar to window.
		JToolBar bar = new JToolBar();
		bar.add(cargar);
		bar.add(guardar);
		bar.add(clear);
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
	
	//Apañar mucho
	public void addPanels() {
		
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));
		
		JPanel graphPanel = new JPanel();
		
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
		
		JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, graphPanel);
		bottomSplit.setDividerLocation(.5);
		JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, bottomSplit);
		
		add(topSplit);
		
		TextEditor events = new TextEditor("Events", true);
		TextEditor events1 = new TextEditor("Events Queue", false);
		TextEditor events2 = new TextEditor("Reports", false);
		TextEditor events3 = new TextEditor("Events", false);
		TextEditor events4 = new TextEditor("Events", false);
		TextEditor events5 = new TextEditor("Events", false);
		tablePanel.add(events3);
		tablePanel.add(events4);
		tablePanel.add(events5);
		upperPanel.add(events);
		upperPanel.add(events1);
		upperPanel.add(events2);
		
		
		
		
		
		
		
		
		
	}
	
	public static void main(String ... args) {
		SwingUtilities.invokeLater(() -> new SimWindow());
	}
}