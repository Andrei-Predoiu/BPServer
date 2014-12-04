package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import server.loader.KnowledgeParser;
import server.model.ClientQuestionOrAction;
import server.model.serverKnowledge.Choice;
import server.model.serverKnowledge.QuestionOrAction;
import server.model.serverKnowledge.ReflectionTracker;
import server.model.serverKnowledge.ReflectionQuiz;

public class DataManipulator {

   private static DataManipulator instance = null;
   private final KnowledgeParser knowledgeLoader;
   private final ArrayList<QuestionOrAction> knowledgeBase;
   private final ArrayList<ReflectionQuiz> reflectionBase;
   private final Logger log;
   private boolean phoneConnect = false;
   private boolean glassConnect = false;
   private boolean startReady = false;
   private boolean reflectionOnAction = true;
   private final Set<Integer> knowns;
   private Map<Integer, ReflectionTracker> reflectionQuizAnswers;
   private Map<String, Integer> categories;

   private DataManipulator() {
      knowledgeLoader = new KnowledgeParser();
      knowledgeBase = knowledgeLoader.getKnowlegeBaseArray();
      reflectionBase = knowledgeLoader.getReflectionBaseArray();
      reflectionQuizAnswers = new HashMap<>();
      categories = new HashMap<>();
      knowns = new HashSet();
      log = Logger.getInstance("C:\\server\\log.txt");
      for (QuestionOrAction qoa : knowledgeBase) {
         try {
            categories.put(qoa.getCategory(), 0);
         } catch (NullPointerException e) {

         }
      }
      resetReflectionAnswers();
   }

   private void resetReflectionAnswers() {
      for (ReflectionQuiz rq : reflectionBase) {
         reflectionQuizAnswers.put(rq.getId(), new ReflectionTracker(rq.getId()));
      }
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
      knowns.clear();
      resetReflectionAnswers();
      return "accepted";
   }

   public synchronized String getReply(int id) {
      String tmp = "**empty**";
      if (id != -1) {
         for (QuestionOrAction qna : knowledgeBase) {
            if (qna.getId() == id) {
               tmp = new ClientQuestionOrAction(qna).getReply();
               break;
            }
         }
      }
      if (!tmp.equals("**empty**")) {
         Logger.write(1, tmp);
      }
      return tmp;
   }

   public synchronized ClientQuestionOrAction getClientQna(int id) {
      for (QuestionOrAction qna : knowledgeBase) {
         if (qna.getId() == id) {
            return new ClientQuestionOrAction(qna);
         }
      }
      return new ClientQuestionOrAction(new QuestionOrAction());
   }

   private synchronized QuestionOrAction getQoAByID(int id) {
      for (QuestionOrAction qna : knowledgeBase) {
         if (qna.getId() == id) {
            return qna;
         }
      }
      return null;
   }

   public synchronized ArrayList<ClientQuestionOrAction> buildResonse(
           int answerId, boolean log) {
      if ((!getClientQna(answerId).getBody().equals("**empty**")) && log) {
         Logger.write(2, getClientQna(answerId).getBody());
      }
      ArrayList<ClientQuestionOrAction> result = new ArrayList<>();
      if (true /*startReady*/) {
         boolean skip = false;
         if (knowns.isEmpty()) {
            if (answerId != -1) {
               skip = true;
            }
         }
         if (getClientQna(answerId).getType().equals("question")) {
            answerId = -1;
         }
         if (answerId == -2) {
            reflectionOnAction = true;
            skip = true;
         }

         if (!skip) {
            int count = 0;
            knowns.add(answerId);
            System.out.println("added id: " + answerId);
            for (QuestionOrAction tmp : knowledgeBase) {
               if (!knowns.contains(tmp.getId())) {
                  if (tmp.getPrerequisites().size() > 0) {
                     for (int j = 0; j < tmp.getPrerequisites().size(); j++) {
                        if (!knowns.contains(tmp.getPrerequisites().get(j))) {
                           System.out.println("missing preprequireiste: " + tmp.getPrerequisites().get(j) + "dropping " + tmp.getId());
                           break;
                        }
                        if ((j == tmp.getPrerequisites().size() - 1) && (count < 7)) {
                           result.add(new ClientQuestionOrAction(tmp));
                           count++;
                        }
                     }
                  } else if (count < 7) {
                     result.add(new ClientQuestionOrAction(tmp));
                     count++;
                  }
               }
            }
         }
      }

      return result;
   }

   public synchronized ReflectionQuiz processReflection(int refId, int choice) {
      ReflectionTracker currentQuiz;
      System.out.println("Can answer: " + canAnswerFeedback(refId, choice));
      if (canAnswerFeedback(refId, choice)) {
         if (refId == -1) {
            return buildQuiz();
         }
         currentQuiz = reflectionQuizAnswers.get(refId);
         reflectionQuizAnswers.put(refId, currentQuiz);
         System.out.println("CurrentQuizBefore2:" + reflectionQuizAnswers.get(refId).getChoices().size() + reflectionQuizAnswers.get(refId).isCompleted());
      } else {
         System.out.println("test1");
         return buildQuiz();
      }
      if (currentQuiz.getChoices().contains(choice)) {
         System.out.println("test2");
         return buildQuiz();
      } else {
         if (reflectionBase.get(refId).getChoices().get(choice) != null) {
            currentQuiz.addChoice(choice);
            System.out.println("Added choice: " + choice);
            for (Choice c : reflectionBase.get(refId).getChoices()) {
               if (!(currentQuiz.getChoices().contains(c.getId())) && (c.isCorrect())) {
                  System.out.println("refID: " + refId + " choiceId: " + c.getId() + "\n" + (!(currentQuiz.getChoices().contains(c.getId()))) + " : " + c.isCorrect());
                  currentQuiz.setCompleted(false);
                  break;
               }
               currentQuiz.setCompleted(true);
               System.out.println("Setting Completed Reflection ID: " + refId);
            }
         }
      }
      System.out.println("CurrentQuizAfter:" + reflectionQuizAnswers.get(refId).getChoices().size() + reflectionQuizAnswers.get(refId).isCompleted());
      return buildQuiz();
      /*
       ReflectionQuiz result = new ReflectionQuiz();
       if ((!reflectionOnAction) || (!canAnswerFeedback(refId, choice))) {
       return result;
       }
       if (refId == -1) {
       return reflectionBase.get(0);
       }
       if (reflectionQuizAnswers.get(refId) == null) {
       reflectionQuizAnswers.put(refId, new ReflectionTracker(refId));
       }
       if (!reflectionQuizAnswers.get(refId).getChoices().contains(choice)) {
       System.out.println("refId:" + refId + " choice:" + choice);
       reflectionQuizAnswers.get(refId).addChoice(choice);
       for (int i : reflectionQuizAnswers.get(refId).getChoices()) {
       System.out.println("i:" + i);
       }
       result = recursiveBuildQuiz(buildQuiz(refId));
       }
       return result;*/
   }
   /*
    private ReflectionQuiz recursiveBuildQuiz(ReflectionQuiz quiz) {
    boolean next = true;
    for (Choice c : quiz.getChoices()) {
    if (!c.isCorrect()) {
    next = false;
    }
    }
    if (next) {
    System.out.println("trying to build quiz with id:" + (quiz.getId() + 1));
    quiz = recursiveBuildQuiz(buildQuiz(quiz.getId() + 1));
    }
    return quiz;
    }
    */

   private ReflectionQuiz buildQuiz() {
      ReflectionQuiz result = new ReflectionQuiz();
      ArrayList<Choice> resultChoices = new ArrayList<>();
      int refId;
      if (reflectionQuizAnswers.isEmpty()) {
         return reflectionBase.get(0);
      }
      for (Integer key : reflectionQuizAnswers.keySet()) {
         if (!reflectionQuizAnswers.get(key).isCompleted()) {
            refId = key;
            resultChoices.clear();
            for (Choice choice : reflectionBase.get(key).getChoices()) {
               if (!reflectionQuizAnswers.get(key).getChoices().contains(choice.getId())) {
                  resultChoices.add(choice);
               }
            }
            result = new ReflectionQuiz(refId, reflectionBase.get(refId).getBody(), resultChoices, reflectionBase.get(refId).getCategory());
            break;
         }
      }
      /*
       ReflectionTracker currentQuiz;
       result.setId(refId);
       result.setBody(reflectionBase.get(refId).getBody());
       currentQuiz = reflectionQuizAnswers.get(refId);
       // for (int i : currentQuiz.getChoices()) {
       //System.out.println("i:" + i);
       //}
       if (currentQuiz == null) {
       return reflectionBase.get(refId);
       }
       System.out.println("RefID:" + refId);
       for (Choice c : reflectionBase.get(refId).getChoices()) {
       if (!currentQuiz.getChoices().contains(c.getId())) {
       System.out.println(c.getId());
       result.addChoice(c);
       }
       }*/

      return result;
   }

   public Map<String, String> getReflectionFeedback(int refId, int choice, String followUp) {
      Map<String, String> result = new HashMap<>();

      try {
         if (canAnswerFeedback(refId, choice)) {
            if (reflectionBase.get(refId).getChoices().get(choice) != null) {
               result.put("feedback", reflectionBase.get(refId).getChoices().get(choice).getFeedback());
               result.put("correct", String.valueOf(reflectionBase.get(refId).getChoices().get(choice).isCorrect()));
               if ((followUp.length() > 1) || !(followUp.equals("**empty**"))) {
                  Logger.write(2, "Student Followup explanation:" + "\n" + followUp);
               }
               Logger.write(0, reflectionBase.get(refId).getBody());
               Logger.write(2, reflectionBase.get(refId).getChoices().get(choice).getBody() + "");
               Logger.write(0, "Feedback:\n" + result.get("feedback"));
            }
         } else {
            result.put("feedback", "**empty**");
            result.put("correct", "false");
         }
      } catch (ArrayIndexOutOfBoundsException e) {
         result.put("feedback", "**empty**");
         result.put("correct", "false");

      }
      return result;
   }

   public synchronized boolean canAnswerFeedback(int refId, int choice) {
      try {
         if (refId == -1) {
            return true;
         }
         boolean oldref = false;
         try {
            oldref = reflectionQuizAnswers.get(refId - 1).isCompleted();
         } catch (NullPointerException e) {
            oldref = true;
         }
         if ((oldref) && !(reflectionQuizAnswers.get(refId).isCompleted())) {
            return true;
         }
      } catch (IndexOutOfBoundsException | NullPointerException e) {
         return false;
      }
      return false;
   }

   public synchronized boolean canAnswerQoA(int id) {
      if (id == -1) {
         return true;
      }
      for (QuestionOrAction qna : knowledgeBase) {
         if (qna.getId() == id) {
            if (qna.getType().equals("question")) {
               return false;
            }
            return knowns.containsAll(qna.getPrerequisites());
         }
      }
      return false;
   }

   public synchronized boolean canAnswerQuizz(int id) {
      if (!knowns.contains(id)) {
         for (QuestionOrAction qna : knowledgeBase) {
            if (qna.getId() == id && qna.getType().equals("question")) {
               return knowns.containsAll(qna.getPrerequisites());
            }
         }
      }
      return false;
   }

   /**
    * Processes the answers, updates knowledge base and generates feedback
    *
    * @param questionID
    * @param answers
    * @return feedback
    */
   public synchronized Map<String, String> processQuizAnswers(int questionID, ArrayList<Integer> answers) {
      QuestionOrAction tempQuiz = getQoAByID(questionID);
      if (tempQuiz.getType().equals("question")) {
         String feedback = "";
         int correct = 0, incorrect = 0, missingCorrect = 0;
         ArrayList<Integer> prunedAnswers = new ArrayList<>();
         for (Integer i : answers) {
            if (!prunedAnswers.contains(i)) {
               prunedAnswers.add(i);
            }
         }

         String logPrepare = tempQuiz.getBodies().get(0) + "\n";

         //tracking the answer for reflection on action
         categories.put(tempQuiz.getCategory(), categories.get(tempQuiz.getCategory()) + 1);

         for (Choice c : tempQuiz.getChoices()) {
            if (prunedAnswers.contains(c.getId())) {
               logPrepare += c.getBody() + ", (" + c.isCorrect() + ")\n";
               if (c.isCorrect()) {
                  correct++;
               } else {
                  incorrect++;
               }
            } else {
               if (c.isCorrect()) {
                  logPrepare += c.getBody() + ", (missing)\n";
                  missingCorrect++;
               } else {
                  correct++;
               }

            }
         }
         System.out.println("Feedback " + correct + ":" + incorrect + ":" + missingCorrect);
         Map<String, String> partialResult = generateQuickFeedback(correct, incorrect, missingCorrect);
         if (!partialResult.get("status").equals("failed")) {
            System.out.println("Status is " + partialResult.get("status"));
            knowns.add(questionID);
         }
         Logger.write(2, logPrepare);
         Logger.write(0, "FEEDBACK\n" + partialResult.get("feedback"));
         return partialResult;
      }
      return null;
   }

   private Map<String, String> generateQuickFeedback(int correct, int incorrect, int missingCorrect) {
      String feedback = "Your answer is";
      int total = correct + incorrect + missingCorrect;
      int percentCorrect = 100 * correct / total;
      int percentIncorrect = 100 * incorrect / total;
      int percentMissingCorrect = 100 * missingCorrect / total;

      String status = "great";
      boolean skip = false;

      if (percentCorrect == 0) {
         feedback += " completely incorrect.";
         status = "failed";
      } else if (percentCorrect == 100) {
         feedback += " completely correct.";
      } else {
         if ((percentCorrect < 50) && (percentCorrect > 0)) {
            feedback += " partially correct";
            status = "sufficient";
         } else if (percentCorrect > 50) {
            feedback += " mostly correct";
         }
         if (percentMissingCorrect > 0) {
            feedback += " but incomplete";
         } else if (percentIncorrect > 0) {
            feedback += " but some of it is wrong. Try again.";
            status = "failed";
            skip = true;
         }

         if ((percentIncorrect > 0) && (!skip)) {
            feedback += " and some of it is wrong. Try again.";
            status = "failed";
         } else {
            feedback += ".";
         }
      }
      Map<String, String> result = new HashMap<String, String>();
      result.put("feedback", feedback);
      result.put("status", status);
      return result;
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
