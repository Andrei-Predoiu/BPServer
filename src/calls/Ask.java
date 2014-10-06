package calls;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
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
import server.model.Answer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Servlet implementation class OrderServlet
 */
@WebServlet(name = "SynchClient", urlPatterns = { "/ask" })
public class Ask extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new GsonBuilder().create();

	private Configuration cfg = FreemarkerConfig.getInstance();
	DataManipulator worker = DataManipulator.getInstance();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Ask() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text;charset=UTF-8");
		PrintWriter writer = response.getWriter();
		Map<String, Serializable> root = new HashMap<String, Serializable>();
		try {

			ServletContext sc = this.getServletContext();
			System.out.println(sc.getRealPath(getServletName()));
			String r = request.getParameter("message");
			System.out.println(r);
			Answer req = gson.fromJson(r, Answer.class);
			r = gson.toJson(req);
			System.out.println(r);

			root.put("variants", worker.buildResonse(req.getId()));
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
	// TODO Auto-generated method stub
}
