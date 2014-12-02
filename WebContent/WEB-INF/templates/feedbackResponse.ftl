{
    "reply": "${reply}",
    "source": "${source}",
    "quiz":{
      "id": "${quiz.getId()}",
      "body": "${quiz.getBody()}",
      "choices" :[<#list quiz.getChoices() as c><#if c_index != 0>,</#if>
          {
              "id": "${c.getId()}",
              "body": "${c.getBody()}"
          }</#list>
      ]
   }    
}