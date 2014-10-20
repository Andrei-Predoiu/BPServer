package server;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import server.loader.KnowledgeParser;
import server.model.ClientQuestionOrAction;
import server.model.serverKnowledge.Choice;
import server.model.serverKnowledge.QuestionOrAction;

public class DataManipulator {

   private static DataManipulator instance = null;
   private final KnowledgeParser knowledgeLoader;
   private final ArrayList<QuestionOrAction> knowledgeBase;
   private final Logger log;
   private boolean phoneConnect = false;
   private boolean glassConnect = false;
   private boolean startReady = false;
   private final Set<Integer> knowns;

   private DataManipulator() {
      knowledgeLoader = new KnowledgeParser();
      knowledgeBase = knowledgeLoader.getKnowlegeBaseArray();
      knowns = new HashSet();
      log = new Logger("C:\\server\\log.txt");
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

   public synchronized String getReply(int id) {
      String tmp = "*empty*";
      for (QuestionOrAction qna : knowledgeBase) {
         if (qna.getId() == id) {
            tmp = new ClientQuestionOrAction(qna).getReply();
         }
      }
      return tmp;
   }

   public synchronized ArrayList<ClientQuestionOrAction> buildResonse(
           int answerId) {
      ArrayList<ClientQuestionOrAction> result = new ArrayList<>();
      if (true /*startReady*/) {
         boolean skip = false;
         if (knowns.isEmpty()) {
            if (answerId != -1) {
               skip = true;
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

   public synchronized boolean canAnswer(int id) {
      if (id == -1) {
         return true;
      }
      for (QuestionOrAction qna : knowledgeBase) {
         if (qna.getId() == id) {
            return knowns.containsAll(qna.getPrerequisites());
         }
      }
      return false;
   }

   public synchronized boolean canAnswerQuizz(int id) {
      for (QuestionOrAction qna : knowledgeBase) {
         if (qna.getId() == id && qna.getType().equals("question")) {
            return knowns.containsAll(qna.getPrerequisites());
         }
      }
      return false;
   }

   private synchronized QuestionOrAction getQoAByID(int id) {
      for (QuestionOrAction qna : knowledgeBase) {
         if (qna.getId() == id) {
            return qna;
         }
      }
      return null;
   }

   /**
    * Processes the answers, updates knowledge base and generates feedback
    *
    * @param questionID
    * @param answers
    * @return feedback
    */
   public synchronized String processQuizAnswers(int questionID, ArrayList<Integer> answers) {
      String feedback = "";
      for (int id : answers) {
         feedback += getFeedbackById(questionID, id) + "\n";

      }
      knowns.add(questionID);
      return feedback;
   }

   private synchronized String getFeedbackById(int questionID, int choiceID) {
      QuestionOrAction qna = getQoAByID(questionID);
      String result = "";
      if (qna != null) {
         if (qna.getType().equals("question")) {
            for (Choice c : qna.getChoices()) {
               if (c.getId() == choiceID) {
                  result += c.getFeedback();
                  break;
               }
            }
         }
      }
      return result;
   }
}
