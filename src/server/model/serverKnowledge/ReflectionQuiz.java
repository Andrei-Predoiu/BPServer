/*
 * The MIT License
 *
 * Copyright 2014 2Xmatch.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package server.model.serverKnowledge;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author 2Xmatch
 */
public class ReflectionQuiz implements Serializable {

   private int id;
   private String body;
   private ArrayList<Integer> prerequisites;
   private ArrayList<Choice> choices;

   public ReflectionQuiz(int id, String body, ArrayList<Choice> choices, ArrayList<Integer> prerequisites) {
      this.id = id;
      this.body = body;
      this.choices = choices;
      this.prerequisites = prerequisites;
   }

   public ReflectionQuiz() {
      this.id = -1;
      this.body = "**empty**";
      this.choices = new ArrayList<>();
      this.prerequisites = new ArrayList<>();
   }

   public void addChoice(Choice choice) {
      this.choices.add(choice);
   }

   public int getId() {
      return id;
   }

   public String getBody() {
      return body;
   }

   public ArrayList<Choice> getChoices() {
      return choices;
   }

   public void setId(int id) {
      this.id = id;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public void setChoices(ArrayList<Choice> choices) {
      this.choices = choices;
   }

   public ArrayList<Integer> getPrerequisites() {
      return prerequisites;
   }

}
