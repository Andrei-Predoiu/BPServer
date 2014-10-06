package server;

import java.util.ArrayList;
import java.util.Set;

import server.loader.KnowledgeParser;
import server.model.ClientQuestionOrAction;
import server.model.serverKnowledge.QuestionOrAction;

public class DataManipulator {
	private static DataManipulator instance = null;
	private KnowledgeParser knowledgeLoader;
	private ArrayList<QuestionOrAction> knowledgeBase;
	private boolean phoneConnect = false;
	private boolean glassConnect = false;
	private boolean startReady = false;
	private Set<Integer> knowns;

	private DataManipulator() {
		knowledgeLoader = new KnowledgeParser();
		knowledgeBase = knowledgeLoader.getKnowlegeBaseArray();
	}

	public synchronized static DataManipulator getInstance() {
		if (instance == null) {
			instance = new DataManipulator();
		}
		return instance;
	}

	public synchronized boolean verifyLogin(String type, String code) {
		if (type.equals("phone") || !phoneConnect) {
			phoneConnect = true;
			return true;
		}
		if (type.equals("glasses") || !glassConnect) {
			glassConnect = true;
			return true;
		}
		return false;
	}

	public synchronized String start() {
		String message;
		if (glassConnect) {
			startReady = true;
			knowns.clear();
			message = "accepted";
		} else {
			message = "connect glasses";
		}
		return message;
	}

	public synchronized ArrayList<ClientQuestionOrAction> buildResonse(
			int answerId) {
		ArrayList<ClientQuestionOrAction> result = new ArrayList<ClientQuestionOrAction>();
		knowns.add(answerId);
		for (int i = 0; i < knowledgeBase.size(); i++) {
			QuestionOrAction tmp = knowledgeBase.get(i);

			if (!knowns.contains(tmp.getId())) {
				for (int j = 0; j < tmp.getPrerequisites().size(); j++) {
					if (!knowns.contains(tmp.getPrerequisites().get(j)))
						break;
					if (j + 1 == tmp.getPrerequisites().size()) {
						result.add(new ClientQuestionOrAction(tmp));
					}
				}
			}
		}
		return result;
	}
}