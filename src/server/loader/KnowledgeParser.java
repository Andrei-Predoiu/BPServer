package server.loader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import server.model.serverKnowledge.QASet;
import server.model.serverKnowledge.QuestionOrAction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.Charset;

public class KnowledgeParser {

    private List<String> lines;
    private Gson gson = new GsonBuilder().create();
    private String kbs = "";
    private QASet knowlegeBase;

    public KnowledgeParser() {
        this.knowlegeBase = new QASet();
        try {
            lines = Files.readAllLines(
                    Paths.get("D:\\Apps\\Workplace_oracle\\BPServer\\WebContent\\WEB-INF\\knowledge\\kb.json"),
                    Charset.defaultCharset());

            int length = lines.size();
            for (int i = 0; i < length; i++) {
                kbs += lines.get(i);
            }
            knowlegeBase.setQuestionOrActionList(gson.fromJson(kbs, new TypeToken<List<QuestionOrAction>>() {
            }.getType()));
        } catch (IOException | JsonSyntaxException e) {
            System.out.println(kbs + "\nCAN'T LOAD KNOWLEDGE BASE!!!!");
            e.printStackTrace();
        }
        System.out
                .println("LOADED KNOWLEDGE BASE, SHOULD BE DONE ONLY ONCE!!!!");
    }

    public QASet getKnowlegeBase() {
        return knowlegeBase;
    }

    public ArrayList<QuestionOrAction> getKnowlegeBaseArray() {
        return knowlegeBase.getQuestionOrActionList();
    }

}
