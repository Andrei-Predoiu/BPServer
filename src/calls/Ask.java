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
import server.model.Answer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.util.ArrayList;
import server.model.ClientQuestionOrAction;

/**
 * Servlet implementation class OrderServlet
 */
@WebServlet(name = "Ask", urlPatterns = {"/ask"})
public class Ask extends HttpServlet {

   private static final long serialVersionUID = 1L;
   Gson gson = new GsonBuilder().create();

   private final Configuration cfg = FreemarkerConfig.getInstance();
   DataManipulator worker = DataManipulator.getInstance();

   /**
    * @see HttpServlet#HttpServlet()
    */
   public Ask() {
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
         Answer req = gson.fromJson(r, Answer.class);
         r = gson.toJson(req);
         System.out.println(r);
         int id = req.getId();
         /**
          * failsafe in case a question is asked before it should be possible
          */
         if (!worker.canAnswerQoA(id)) {
            id = -1;
         }
         ArrayList<ClientQuestionOrAction> x = worker.buildResonse(id, true);
         System.out.println("Printing responses");
         x.stream().forEach((ClientQuestionOrAction y) -> {
            System.out.println(y.getId());
         });

         root.put("id", id);
         root.put("reply", worker.getReply(id));
         root.put("source", "patient");
         root.put("variants", x);
         /* Get the template */
         Template temp = cfg.getTemplate("qaListResponse.ftl");

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
