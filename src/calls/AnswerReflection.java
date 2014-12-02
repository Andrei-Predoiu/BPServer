package calls;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.DataManipulator;
import server.FreemarkerConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.util.ArrayList;
import server.Logger;
import server.model.ClientQuestionOrAction;
import server.model.QuizzAnswer;
import server.model.ReflectionResponse;
import server.model.serverKnowledge.ReflectionQuiz;

/**
 * Servlet implementation class OrderServlet
 */
@WebServlet(name = "AnswerReflection", urlPatterns = {"/reflection"})
public class AnswerReflection extends HttpServlet {

   private static final long serialVersionUID = 1L;
   Gson gson = new GsonBuilder().create();

   private final Configuration cfg = FreemarkerConfig.getInstance();
   DataManipulator worker = DataManipulator.getInstance();

   /**
    * @see HttpServlet#HttpServlet()
    */
   public AnswerReflection() {
      super();
   }

   /**
    * @param request
    * @param response
    * @throws javax.servlet.ServletException
    * @throws java.io.IOException
    * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
    * response)
    */
   @Override
   protected void doGet(HttpServletRequest request,
           HttpServletResponse response) throws ServletException, IOException {
   }

   /**
    * @param request
    * @param response
    * @throws javax.servlet.ServletException
    * @throws java.io.IOException
    * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
    * response)
    */
   @Override
   protected void doPost(HttpServletRequest request,
           HttpServletResponse response) throws ServletException, IOException {
      response.setContentType("text;charset=UTF-8");
      PrintWriter writer = response.getWriter();
      Map<String, Serializable> root = new HashMap<>();
      try {

         String r = request.getParameter("message");
         System.out.println(r);
         ReflectionResponse req = gson.fromJson(r, ReflectionResponse.class);
         int refId = req.getQuizId();
         int choiceId = req.getChoiceId();
         Template temp;
         /**
          * failsafe in case a question is asked before it should be possible
          */
         if (!worker.canAnswerFeedback(refId, choiceId)) {
            root.put("reply", "**empty**");
            root.put("source", "teacherG");
            root.put("quiz", new ReflectionQuiz());
            /* Get the template */
         } else {
            Map<String, String> results = worker.getReflectionFeedback(refId, choiceId, req.getFollowUp());
            root.put("reply", results.get("feedback"));

            switch (results.get("correct")) {
               case ("true"):
                  root.put("source", "teacherG");
                  break;
               case ("false"):
                  root.put("source", "teacherR");
                  break;
            }
            ReflectionQuiz x = worker.processReflection(refId, choiceId);
            root.put("quiz", x);
         }
         temp = cfg.getTemplate("feedbackResponse.ftl");
         /* Merge data-model with template */
         try {
            temp.process(root, writer);
         } catch (TemplateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      } finally {
         writer.close();
      }
   }
}
