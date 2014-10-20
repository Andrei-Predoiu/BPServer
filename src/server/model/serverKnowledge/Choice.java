package server.model.serverKnowledge;

public class Choice {

   private int id;
   private boolean correct;
   private String body;
   private String feedback;

   public String getFeedback() {
      return feedback;
   }

   public int getId() {
      return id;
   }

   public boolean isCorrect() {
      return correct;
   }

   public String getBody() {
      return body;
   }

}
