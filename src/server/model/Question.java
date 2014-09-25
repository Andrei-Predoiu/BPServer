package server.model;

import java.util.ArrayList;

public class Question {
	private int id;
	private String body;
	private ArrayList<String> choices;

	public Question(String body, ArrayList<String> choices) {

		this.body = body;
		this.choices = choices;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public ArrayList<String> getChoices() {
		return choices;
	}

	public void setChoices(ArrayList<String> choices) {
		this.choices = choices;
	}

	public void addCoice(String choice) {
		if (this.choices == null) {
			this.choices = new ArrayList<String>();
		} else {
			this.choices.add(choice);

		}
	}

}
