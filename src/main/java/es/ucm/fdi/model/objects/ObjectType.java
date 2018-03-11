package es.ucm.fdi.model.objects;

public enum ObjectType
{
	VEHICLE, ROAD, JUNCTION, OBJECT_ERROR;
	
	/**Devuelve la representación escrita de los distintos objetos.*/
	public String toString()
	{
		switch(this)
		{
		case VEHICLE:	return "vehicle";
		case ROAD:		return "road";	
		case JUNCTION:	return "junction";
		default:		return "";										//Crear una excepción que se lance si no se pasa por parámetro un objeto válido
		}
	}
	
	/**Dada una palabra devuelve el tipo del objeto que representa (no distingue mayúsculas y minúsculas).*/
	public static ObjectType objectType(String name)
	{
		name.toLowerCase();
		
		switch(name)
		{
		case "vehicle":			return ObjectType.VEHICLE;
		case "road":			return ObjectType.ROAD;
		case "junction":		return ObjectType.JUNCTION;
		default:				return ObjectType.OBJECT_ERROR;			//Lanzar aquí tambien la excepción de objeto de tipo inválido.
		}
	}
}
