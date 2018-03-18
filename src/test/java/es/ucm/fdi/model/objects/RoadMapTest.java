package es.ucm.fdi.model.objects;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import es.ucm.fdi.model.objects.*;

public class RoadMapTest {
	RoadMap mapa = new RoadMap();
	
	@Test
	public void getClassTest(){
		Road r = new Road("r1", 60, 100);
		Road[] it = new Road[1];
		it[0] = r;
		Vehicle v = new Vehicle("v1", 50, it);
		Junction j = new Junction("j1");
		
		mapa.addJunction(j);
		mapa.addRoad(r);
		mapa.addVehicle(v);
		
		Vehicle v1 = mapa.getVehicle("v1");
		Road r1 = mapa.getRoad("r1");
		
		assertEquals(0, v1.getLocalizacion());
		assertEquals(100, r1.getLongitud());
	}
}
