package server.model;

import java.util.ArrayList;

public class Data {
	private String action;
	private ArrayList<Area> areas;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ArrayList<Area> getAreas() {
		return areas;
	}

	public void setAreas(ArrayList<Area> areas) {
		this.areas = areas;
	}
}
