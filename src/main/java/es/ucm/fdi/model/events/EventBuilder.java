package es.ucm.fdi.model.events;

import es.ucm.fdi.ini.IniSection;

/**
 * Interfaz encargada de representar la funcionalidad necesaria para poder parsear un evento a partir de
 * una sección de tipo IniSection.
 * 
 * @author Francisco Javier Blázquez Martínez
 * @version 04/05/18
 * @see IniSection
 * @see Event
 */
public interface EventBuilder
{
	/**
	 * Parsea una sección.
	 * 
	 * @param sec sección a parsear
	 * @return Evento que representa la sección, null si no se consigue parsear.
	 */
	public Event parse(IniSection sec);
}
