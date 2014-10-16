package server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import server.loader.KnowledgeParser;
import server.model.ClientQuestionOrAction;
import server.model.serverKnowledge.QuestionOrAction;

public class DataManipulator {

    private static DataManipulator instance = null;
    private final KnowledgeParser knowledgeLoader;
    private final ArrayList<QuestionOrAction> knowledgeBase;
    private boolean phoneConnect = false;
    private boolean glassConnect = false;
    private boolean startReady = false;
    private final Set<Integer> knowns;

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
        if (true /*startReady*/) {
            boolean skip = false;
            if (knowns.isEmpty()) {
                if (answerId != -1) {
                    skip = true;
                } else {
                    System.out.println("Got -1 and list is empty, giving starting batch!\nKnowns size:" + knowns.size() + "\nskip is: " + skip);
                }
            }
            //To be removed start
            String tempOutout = "";
            for (int x : knowns) {
                tempOutout += x + ", ";
            }
            System.out.println("Knowns:\n" + tempOutout);
            //To be removed stop

            if (!skip) {
                knowns.add(answerId);
                System.out.println("added id: " + answerId);
                for (QuestionOrAction tmp : knowledgeBase) {
                    if (!knowns.contains(tmp.getId())) {
                        if (tmp.getPrerequisites().size() > 0) {
                            for (int j = 0; j < tmp.getPrerequisites().size(); j++) {
                                if (!knowns.contains(tmp.getPrerequisites().get(j))) {
                                    break;
                                }
                                if (j == tmp.getPrerequisites().size() - 1) {
                                    result.add(new ClientQuestionOrAction(tmp));
                                }
                            }
                        } else {
                            result.add(new ClientQuestionOrAction(tmp));
                        }
                    }
                }
            }
        }
        return result;
    }
}
