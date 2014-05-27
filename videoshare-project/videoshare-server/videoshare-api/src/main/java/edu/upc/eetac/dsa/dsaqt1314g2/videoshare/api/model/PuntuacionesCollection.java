package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model;

import java.util.ArrayList;
import java.util.List;

public class PuntuacionesCollection {
	List<Puntuaciones> punt;

	public PuntuacionesCollection() {
		super();
		punt = new ArrayList<Puntuaciones>();
	}
	public void addPuntuacion(Puntuaciones p){
		punt.add(p);
	}
	public List<Puntuaciones> getPunt() {
		return punt;
	}
	public void setPunt(List<Puntuaciones> punt) {
		this.punt = punt;
	}
}
