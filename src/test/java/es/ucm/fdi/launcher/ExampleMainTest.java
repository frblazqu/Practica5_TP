package es.ucm.fdi.launcher;

import org.junit.Test;
import static org.junit.Assert.*;
import es.ucm.fdi.launcher.ExampleMain;

import java.io.*;
import java.util.*;

public class ExampleMainTest 
{
	
	//Comprobar que rellena los atributos del main con los campos introducidos por teclado
	@Test
	public void entradaSalidaTest()
	{
		String comandos = "--help";
		String[] com = comandos.split(" ");
		
		ExampleMain.parseArgs(com);
		
		assertEquals(null, ExampleMain._inFile);
		assertEquals(null, ExampleMain._outFile);
		assertTrue("No coincide el tiempo", ExampleMain._timeLimit == 10);
	}
	
	//Comprobar y ver como se muestra el mensaje de ayuda del ExampleMain
	//@Test
	public void helpTest()
	{
		
		;
	}
	
	//Rellenar los camos default de iniFile y outFile de ExampleMain y comprobar su correcto funcionamiento
	//@Test 
	public void defaultEntryTest()
	{
		;
		
		
	}
	
}


