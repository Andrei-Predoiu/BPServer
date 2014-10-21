package server.model.serverKnowledge;

import java.util.ArrayList;

public class QuestionOrAction {

   private final int id;
   private final ArrayList<Integer> prerequisites;
   private final boolean relevant;
   private final String type;
   private final ArrayList<String> bodies;
   private final ArrayList<String> replies;
   private final ArrayList<Choice> choices;

   public QuestionOrAction() {
      this.id = -1;
      this.prerequisites = new ArrayList<>();
      this.relevant = false;
      this.type = "none";
      this.bodies = new ArrayList<>();
      this.replies = new ArrayList<>();
      this.choices = new ArrayList<>();
   }

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
