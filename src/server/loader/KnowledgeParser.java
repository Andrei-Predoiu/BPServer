package server.loader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import server.model.serverKnowledge.QASet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KnowledgeParser {
	private List<String> lines;
	private Gson gson = new GsonBuilder().create();
	private String kbs = "";
	private QASet knowlegeBase;

	public KnowledgeParser() {
		try {
			lines = Files
					.readAllLines(Paths
							.get("D:\\Work\\git\\BPServer\\MSS\\WebContent\\WEB-INF\\knowledge\\kb.json"));

			System.out
					.println("LOADED KNOWLEDGE BASE, SHOULD BE DONE ONLY ONCE!!!!");
			int length = lines.size();
			for (int i = 0; i < length; i++) {
				kbs += lines.get(i);
			}
			knowlegeBase = gson.fromJson(kbs, QASet.class);
		} catch (Exception e) {
			System.out.println("CAN'T LOAD KNOWLEDGE BASE!!!!");
			e.printStackTrace();
		}
	}

	public QASet getKnowlegeBase() {
		return knowlegeBase;
	}

	public void setKnowlegeBase(QASet knowlegeBase) {
		this.knowlegeBase = knowlegeBase;
	}

}