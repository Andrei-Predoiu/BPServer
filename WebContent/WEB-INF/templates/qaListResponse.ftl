[
	<#list variants as v>
		<#if v_index != 0>,</#if>{       
		"id": "${v.getId()}",
		"type": "${v.getType()}",
      "body": "${v.getBody()}"
      <#if v.getType().equesls("question")>,
      "choices" :[
      <#list choices as c>         
         <#if c_index != 0>,</#if>{
            "id":"c.getId()",
            "body":"c.getBody()"
         }         
      </#list>
      ]
      </#if>{       
	}
	</#list>
]