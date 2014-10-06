package server.model;

import java.util.ArrayList;
import java.util.Random;

import server.model.serverKnowledge.Choice;
import server.model.serverKnowledge.QuestionOrAction;

public class ClientQuestionOrAction {
	private int id;
	private String type;
	private String body;
	private String reply;
	private ArrayList<Choice> choices;

	public ClientQuestionOrAction(QuestionOrAction qoA) {
		Random rand = new Random();
		this.id = qoA.getId();
		this.type = qoA.getType();
		this.body = qoA.getBodies().get(rand.nextInt(qoA.getBodies().size()));
		this.reply = qoA.getBodies().get(rand.nextInt(qoA.getReplies().size()));
		if (qoA.getChoices() != null) {
			this.choices = qoA.getChoices();
		}
		rand = null;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getBody() {
		return body;
	}

	public String getReply() {
		return reply;
	}

	public ArrayList<Choice> getChoices() {
		return choices;
	}

}
