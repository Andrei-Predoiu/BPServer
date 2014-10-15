package server.model.serverKnowledge;

import java.util.ArrayList;

public class QASet {

    private ArrayList<QuestionOrAction> questionOrActionList;

    public ArrayList<QuestionOrAction> getQuestionOrActionList() {
        return questionOrActionList;
    }

    public void setQuestionOrActionList(ArrayList<QuestionOrAction> questions) {
        questionOrActionList = questions;
    }

}
