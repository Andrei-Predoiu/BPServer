package server;

import java.util.Set;

import server.loader.KnowledgeParser;
import server.model.serverKnowledge.QASet;

public class DataManipulator {
	private static DataManipulator instance = null;
	private KnowledgeParser knowledgeLoader;
	private QASet knowledgeBase;
	private boolean phoneConnect = false;
	private boolean glassConnect = false;
	private boolean startReady = false;
	private Set<Integer> knowns;

	private DataManipulator() {
		knowledgeLoader = new KnowledgeParser();
		knowledgeBase = knowledgeLoader.getKnowlegeBase();
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
}