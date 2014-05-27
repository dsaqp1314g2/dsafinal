package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model;

import java.util.ArrayList;
import java.util.List;

public class UserCollection {

	private List<User> usuarios;

	public UserCollection() {
		super();
		usuarios = new ArrayList<User>();
	}

	public List<User> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<User> usuarios) {
		this.usuarios = usuarios;
	}

	public void addUser(User usuario) {
		usuarios.add(usuario);
	}
}
