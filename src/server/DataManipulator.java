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
import server.model.serverKnowledge.QuizTracker;
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
   private Map<Integer, QuizTracker> quizAnswers;
   private Map<Integer, QuizTracker> reflectionQuizAnswers;
   
   private DataManipulator() {
      knowledgeLoader = new KnowledgeParser();
      knowledgeBase = knowledgeLoader.getKnowlegeBaseArray();
      reflectionBase = knowledgeLoader.getReflectionBaseArray();
      reflectionQuizAnswers = new HashMap<Integer, QuizTracker>();
      quizAnswers = new HashMap();
      knowns = new HashSet();
      log = Logger.getInstance("C:\\server\\log.txt");
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
      Logger.write(1, tmp);
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
           int answerId) {
      Logger.write(2, getClientQna(answerId).getBody());
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
      ReflectionQuiz result = new ReflectionQuiz();
      if ((!reflectionOnAction) || (!canAnswerFeedback(refId, choice))) {
         return result;
      }
      if (refId == -1) {
         return reflectionBase.get(0);
      }
      if (reflectionQuizAnswers.get(refId) == null) {
         reflectionQuizAnswers.put(refId, new QuizTracker(refId));
      }
      if (!reflectionQuizAnswers.get(refId).getChoices().contains(choice)) {
         System.out.println("refId:" + refId + " choice:" + choice);
         reflectionQuizAnswers.get(refId).addChoice(choice);
         for (int i : reflectionQuizAnswers.get(refId).getChoices()) {
            System.out.println("i:" + i);
         }
         result = recursiveBuildQuiz(buildQuiz(refId));
      }
      return result;
   }
   
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
   
   private ReflectionQuiz buildQuiz(int refId) {
      ReflectionQuiz result = new ReflectionQuiz();
      QuizTracker currentQuiz;
      result.setId(refId);
      result.setBody(reflectionBase.get(refId).getBody());
      currentQuiz = reflectionQuizAnswers.get(refId);
      /* for (int i : currentQuiz.getChoices()) {
       System.out.println("i:" + i);
       }*/
      if (currentQuiz == null) {
         return reflectionBase.get(refId);
      }
      System.out.println("RefID:" + refId);
      for (Choice c : reflectionBase.get(refId).getChoices()) {
         if (!currentQuiz.getChoices().contains(c.getId())) {
            System.out.println(c.getId());
            result.addChoice(c);
         }
      }
      return result;
   }
   
   public Map<String, String> getReflectionFeedback(int refId, int choice, String followUp) {
      Map<String, String> result = new HashMap<>();
      try {
         if (reflectionBase.get(refId).getChoices().get(choice) != null) {
            result.put("feedback", reflectionBase.get(refId).getChoices().get(choice).getFeedback());
            result.put("body", reflectionBase.get(refId).getChoices().get(choice).getBody());
            result.put("correct", String.valueOf(reflectionBase.get(refId).getChoices().get(choice).isCorrect()));
            Logger.write(2, result.get("body"));
            Logger.write(2, "Student Followup explanation:" + "\n" + followUp);
            Logger.write(1, "Feedback:\n" + result.get("feedback"));
            
         }
      } catch (ArrayIndexOutOfBoundsException e) {
         result.put("feedback", "**empty**");
         result.put("body", "**empty**");
         result.put("correct", "false");
         
      }
      return result;
   }
   
   public synchronized boolean canAnswerFeedback(int refId, int choice) {
      try {
         if (refId == -1) {
            return true;
         }
         for (ReflectionQuiz ref : reflectionBase) {
            if (ref.getId() == refId) {
               if (ref.getChoices().get(choice) != null) {
                  return true;
               }
            }
         }
         return false;
      } catch (IndexOutOfBoundsException e) {
         return false;
      }
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
         quizAnswers.put(questionID, new QuizTracker(questionID, answers));
         
         for (Choice c : tempQuiz.getChoices()) {
            logPrepare += c.getBody() + ", (" + c.isCorrect() + ")\n";
            if (prunedAnswers.contains(c.getId())) {
               if (c.isCorrect()) {
                  correct++;
               } else {
                  incorrect++;
               }
            } else {
               if (c.isCorrect()) {
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
