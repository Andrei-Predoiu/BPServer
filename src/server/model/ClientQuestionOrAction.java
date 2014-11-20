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
      if (qoA.getBodies().size() > 0) {
         this.body = qoA.getBodies().get(rand.nextInt(qoA.getBodies().size()));
      } else {
         this.body = "**empty**";
      }
      if (qoA.getReplies().size() > 0) {
         this.reply = qoA.getReplies().get(rand.nextInt(qoA.getReplies().size()));
      } else {
         this.reply = "**empty**";
      }
      if (qoA.getChoices() != null) {
         this.choices = qoA.getChoices();
      }
      rand = null;
   }

   public void setId(int id) {
      this.id = id;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public void setReply(String reply) {
      this.reply = reply;
   }

   public void setChoices(ArrayList<Choice> choices) {
      this.choices = choices;
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
