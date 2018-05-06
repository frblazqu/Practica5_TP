package es.ucm.fdi.model.exceptions;

import es.ucm.fdi.ini.IniSection;

public class EventParseException extends Exception {
	public EventParseException() {
		super();
	}

	public EventParseException(IniSection s) {
		super("No se ha podido parsear la siguiente sección:\n" + s);
	}
}
