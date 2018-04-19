package es.ucm.fdi.model.objects;

public class RoundJunction extends Junction
{
	/*Muchos atributos protected*/
	private int minDuration;
	private int maxDuration;
	
	public RoundJunction(String junction_id, int minDurationVerde, int maxDurationVerde)
	{
		super(junction_id);
		
		minDuration = minDurationVerde;
		maxDuration = maxDurationVerde;
	}
	
}
