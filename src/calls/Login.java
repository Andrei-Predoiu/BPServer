package calls;

import calls.gcm.GcmSender;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.DataManipulator;
import server.FreemarkerConfig;
import server.model.LoginData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Servlet implementation class OrderServlet
 */
@WebServlet(urlPatterns = {"/login"})
public class Login extends HttpServlet {

   private static final long serialVersionUID = 1L;
   Gson gson = new GsonBuilder().create();
   GcmSender gcm = GcmSender.getInstance();
   private Configuration cfg = FreemarkerConfig.getInstance();
   DataManipulator worker = DataManipulator.getInstance();

   /**
    * @see HttpServlet#HttpServlet()
    */
   public Login() {
      super();
   }

   /**
    * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
    * response)
    */
   protected void doGet(HttpServletRequest request,
           HttpServletResponse response) throws ServletException, IOException {
   }

   /**
    * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
    * response)
    */
   protected void doPost(HttpServletRequest request,
           HttpServletResponse response) throws ServletException, IOException {
      response.setContentType("text;charset=UTF-8");
      PrintWriter writer = response.getWriter();
      Map<String, String> root = new HashMap<String, String>();
      root.put("message", "failed");
      try {

         ServletContext sc = this.getServletContext();
         String r = request.getParameter("message");
         System.out.println("login call Raw data:\n" + r + "\n__________________________________________");
         LoginData req = gson.fromJson(r, LoginData.class);
         r = gson.toJson(req);
         System.out.println("Login recieved:\nType: " + req.getType() + "\nCode: " + req.getCode() + "\n__________________________________________");

         if (worker.verifyLogin(req.getType(), req.getCode())) {
            root.put("message", "accepted");
            gcm.registerDevice(req.getType(), req.getGcmRegID());
         } else {
            gcm.registerDevice(req.getType(), req.getGcmRegID());
            root.put("message", "accepted");
         }

         /* Get the template */
         Template temp = cfg.getTemplate("response.ftl");

         /* Merge data-model with template */
         try {
            temp.process(root, writer);
         } catch (TemplateException e) {
            e.printStackTrace();
         }
      } finally {
         writer.close();
      }

   }
}
