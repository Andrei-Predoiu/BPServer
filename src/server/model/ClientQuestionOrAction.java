package server.model;

import java.util.ArrayList;
import java.util.Random;

import server.model.serverKnowledge.Choice;
import server.model.serverKnowledge.QuestionOrAction;

public class ClientQuestionOrAction {

   private final int id;
   private final String type;
   private final String body;
   private final String reply;
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
