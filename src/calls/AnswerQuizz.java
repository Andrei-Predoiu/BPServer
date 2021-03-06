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
import server.model.ClientQuestionOrAction;
import server.model.QuizzAnswer;

/**
 * Servlet implementation class OrderServlet
 */
@WebServlet(name = "AnswerQuizz", urlPatterns = {"/quiz"})
public class AnswerQuizz extends HttpServlet {

   private static final long serialVersionUID = 1L;
   Gson gson = new GsonBuilder().create();

   private final Configuration cfg = FreemarkerConfig.getInstance();
   DataManipulator worker = DataManipulator.getInstance();

   /**
    * @see HttpServlet#HttpServlet()
    */
   public AnswerQuizz() {
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
         QuizzAnswer req = gson.fromJson(r, QuizzAnswer.class);
         r = gson.toJson(req);
         System.out.println(r);
         int id = req.getId();
         Template temp;
         root.put("id", id);
         /**
          * failsafe in case a question is asked before it should be possible
          */
         if (!worker.canAnswerQuizz(id)) {
            id = -1;
            root.put("reply", worker.getReply(id));
            root.put("source", "patient");
            /* Get the template */
         } else {
            Map<String, String> results = worker.processQuizAnswers(id, req.getAnswers());
            String feedback = results.get("feedback");
            root.put("reply", feedback);

            switch (results.get("status")) {
               case ("great"):
                  root.put("source", "teacherG");
                  break;
               case ("sufficient"):
                  root.put("source", "teacherO");
                  break;
               case ("failed"):
                  root.put("source", "teacherR");
                  break;
            }
         }
         ArrayList<ClientQuestionOrAction> x = worker.buildResonse(id, false);
         root.put("variants", x);
         temp = cfg.getTemplate("qaListResponse.ftl");
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
