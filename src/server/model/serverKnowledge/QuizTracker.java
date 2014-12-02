/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model.serverKnowledge;

import java.util.ArrayList;

/**
 *
 * @author 2Xmatch
 */
public class QuizTracker {

   private int quizID;
   private ArrayList<Integer> choices;

   public QuizTracker(int quizID, ArrayList<Integer> choices) {
      choices = new ArrayList<>();
      this.quizID = quizID;
      this.choices = choices;
   }

   public QuizTracker(ArrayList<Choice> choices, int quizID) {
      choices = new ArrayList<>();
      this.quizID = quizID;
      for (Choice c : choices) {
         this.choices.add(c.getId());
      }

   }

   public QuizTracker(int quizID) {
      choices = new ArrayList<>();
      this.quizID = quizID;
   }

   public void addChoice(int choiceID) {
      choices.add(choiceID);
   }

   public int getQuizID() {
      return quizID;
   }

   public ArrayList<Integer> getChoices() {
      return choices;
   }
}
