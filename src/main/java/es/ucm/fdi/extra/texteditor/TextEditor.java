package es.ucm.fdi.extra.texteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;

import es.ucm.fdi.control.SimulatorAction;

public class TextEditor extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private final String LOAD = "load";
	private final String SAVE = "save";
	private final String CLEAR = "clear";
	private final String QUIT = "quit";

	private JFileChooser fc;			
	private JTextArea textArea;
	String name;
	boolean editable;
	
	public TextEditor(String n, boolean e, JFileChooser f){
		super(new BorderLayout());
		name = n;
		editable = e;
		fc = f;
		initGUI();
	}
	
	private void initGUI(){
		this.setPreferredSize(new Dimension(300, 200));
		
		textArea = new JTextArea("");
		textArea.setEnabled(editable);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		Border b = BorderFactory.createLineBorder(Color.black, 2);
		textArea.setBorder(BorderFactory.createTitledBorder(b, name));
		
		if(editable) {
			JPopupMenu optionsPopup = new JPopupMenu();
		
			SimulatorAction loadOption = new SimulatorAction("Load", "Cargar eventos",
					KeyEvent.VK_L, ()-> loadFile());
		
			SimulatorAction saveOption = new SimulatorAction("Save", "Guardar eventos",
					KeyEvent.VK_S, ()-> saveFile());

			SimulatorAction clearOption = new SimulatorAction("Clear", "Vaciar eventos",
					KeyEvent.VK_B, ()-> textArea.setText(""));
		
			optionsPopup.add(loadOption);
			optionsPopup.add(saveOption);
			optionsPopup.add(clearOption);
		
			textArea.addMouseListener(new MouseListener() {

				@Override
				public void mousePressed(MouseEvent e) {
					showPopup(e);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					showPopup(e);
				}

				private void showPopup(MouseEvent e) {
					if (e.isPopupTrigger() && optionsPopup.isEnabled()) {
						optionsPopup.show(e.getComponent(), e.getX(), e.getY());
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});
		
		}
		
		JScrollPane scroll = new JScrollPane(textArea);
		this.add(scroll);
	}

	private void saveFile() {
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			writeFile(file, textArea.getText());
		}
	}

	private void loadFile() {
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String s = readFile(file);
			textArea.setText(s);
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

	public static void writeFile(File file, String content) {
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.print(content);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setText(String s){
		textArea.setText(s);
	}
	
	public String getText(){
		return textArea.getText();
	}
}
