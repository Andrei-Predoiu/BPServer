package server.model.clientKnowledge;

import java.util.ArrayList;

public class ResponseLists {
	private int id;
	private String body;
	private ArrayList<String> questions;
	private ArrayList<String> actions;

	public ResponseLists(String body, ArrayList<String> questions,
			ArrayList<String> actions) {

		this.body = body;
		this.questions = questions;
		this.actions = actions;
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

	public ArrayList<String> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<String> questions) {
		this.questions = questions;
	}

	public void addQuestion(String question) {
		if (this.questions == null) {
			this.questions = new ArrayList<String>();
		} else {
			this.questions.add(question);

		}
	}

	public ArrayList<String> getActions() {
		return actions;
	}

	public void setActions(ArrayList<String> actions) {
		this.actions = actions;
	}

	public void addAction(String action) {
		if (this.actions == null) {
			this.actions = new ArrayList<String>();
		} else {
			this.actions.add(action);

		}
	}
}
