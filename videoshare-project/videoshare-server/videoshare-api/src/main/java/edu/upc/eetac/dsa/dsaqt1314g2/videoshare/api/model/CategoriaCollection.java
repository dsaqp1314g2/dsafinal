package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model;



import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

public class CategoriaCollection {

	
	
	private List<Link> links;
	private List<Categoria> cat;
	
	
	public CategoriaCollection() {
		super();
		cat = new ArrayList<Categoria>();
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public List<Categoria> getCat() {
		return cat;
	}
	public void setCat(List<Categoria> cat) {
		this.cat = cat;
	}
	
	public void addCategoria(Categoria cata) {
		cat.add(cata);
	}
	
}
