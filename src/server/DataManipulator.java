package server;

import java.util.ArrayList;
import java.util.HashSet;
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
        knowns = new HashSet();
    }

    public synchronized static DataManipulator getInstance() {
        if (instance == null) {
            instance = new DataManipulator();
        }
        return instance;
    }

    public synchronized boolean verifyLogin(String type, String code) {
        System.out.println("phoneConnect: " + phoneConnect + "\nglassConnect: " + glassConnect + "\ncode: " + code + "\n______________________________");
        if (code.equals("secret")) {
            System.out.println("Code was correct!");
            if (type.equals("phone") && !phoneConnect) {
                phoneConnect = true;
                return true;
            }
            if (type.equals("glasses") && !glassConnect) {
                glassConnect = true;
                return true;
            }
        }
        return false;
    }

    public synchronized String start() {
        System.out.println("phoneConnect: " + phoneConnect + "\nglassConnect: " + glassConnect + "\n______________________________");
        String message;
        if (phoneConnect) {
            if (glassConnect) {
                startReady = true;
                knowns.clear();
                message = "accepted";
            } else {
                message = "Connect glasses";
            }
        } else {
            message = "Connect phone";
        }
        return message;
    }

    public synchronized ArrayList<ClientQuestionOrAction> buildResonse(
            int answerId) {
        ArrayList<ClientQuestionOrAction> result = new ArrayList<>();
        knowns.add(answerId);
        for (QuestionOrAction tmp : knowledgeBase) {
            if (!knowns.contains(tmp.getId())) {
                for (int j = 0; j < tmp.getPrerequisites().size(); j++) {
                    if (!knowns.contains(tmp.getPrerequisites().get(j))) {
                        break;
                    }
                    if (j + 1 == tmp.getPrerequisites().size()) {
                        result.add(new ClientQuestionOrAction(tmp));
                    }
                }
            }
        }
        return result;
    }
}
