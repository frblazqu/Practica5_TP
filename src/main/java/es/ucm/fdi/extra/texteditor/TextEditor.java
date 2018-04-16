package es.ucm.fdi.extra.texteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;

public class TextEditor extends JScrollPane implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	private final String LOAD = "load";
	private final String SAVE = "save";
	private final String CLEAR = "clear";
	private final String QUIT = "quit";

	private JFileChooser fc;
	private JTextArea textArea;
	String name;
	boolean editable;
	
	public TextEditor(String n, boolean e){
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		name = n;
		editable = e;
		initGUI();
	}
	
	private void initGUI(){
		setPreferredSize(new Dimension(300, 200));
		
		textArea = new JTextArea("");
		textArea.setEnabled(editable);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		
		
		Border b = BorderFactory.createLineBorder(Color.black, 2);
		textArea.setBorder(BorderFactory.createTitledBorder(b, name));
		
		this.setViewportView(textArea);
		
		fc = new JFileChooser();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (LOAD.equals(e.getActionCommand()))
			loadFile();
		else if (SAVE.equals(e.getActionCommand()))
			saveFile();
		else if (CLEAR.equals(e.getActionCommand()))
			textArea.setText("");
		else if (QUIT.equals(e.getActionCommand()))
			System.exit(0);
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
}
