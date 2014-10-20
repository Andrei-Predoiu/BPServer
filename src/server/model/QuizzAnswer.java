package server.model;

import java.util.ArrayList;

public class QuizzAnswer {

   private int id;
   private ArrayList<Integer> answers;

   public ArrayList<Integer> getAnswers() {
      return answers;
   }

   public int getId() {
      return id;
   }
}
