package server.model.serverKnowledge;

import java.util.ArrayList;

public class QuestionOrAction {
	private int id;
	private ArrayList<Integer> prerequisites;
	private boolean relevant;
	private String type;
	private ArrayList<String> bodies;
	private ArrayList<String> replies;
	private ArrayList<Choice> choices;

	public int getId() {
		return id;
	}

	public ArrayList<Integer> getPrerequisites() {
		return prerequisites;
	}

	public boolean isRelevant() {
		return relevant;
	}

	public String getType() {
		return type;
	}

	public ArrayList<String> getBodies() {
		return bodies;
	}

	public ArrayList<String> getReplies() {
		return replies;
	}

	public ArrayList<Choice> getChoices() {
		return choices;
	}

}
