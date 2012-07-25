<%@ page contentType="text/html;charset=gbk" language="java"%>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tld/oscache.tld" prefix="oscache" %>
<%@ page import="java.util.*"%>
<%@ page import="com.glaf.base.modules.todo.*"%>
<%@ page import="com.glaf.base.modules.todo.model.*"%>
<%@ page import="com.glaf.base.modules.todo.service.*"%>
<%@ page import="com.glaf.base.utils.*"%>
<%@ page import="com.glaf.base.modules.sys.*"%>
<%@ page import="com.glaf.base.modules.sys.model.*"%>
<%@ page import="com.glaf.base.modules.sys.service.*"%>
<%@ page import="org.jpage.util.*" %>
<%@ page import="org.jpage.core.query.paging.*" %>
<%@ page import="org.jpage.jbpm.task.*" %>
<%@ page import="org.jpage.jbpm.model.*" %>
<%@ page import="org.jpage.jbpm.service.ProcessContainer" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
    String context = request.getContextPath();
    SysUser user = (SysUser) request.getSession().getAttribute(SysConstants.LOGIN);
	TodoJobBean bean = (TodoJobBean)BaseDataManager.getInstance().getBean("todoJobBean");
%>

 <oscache:cache key="tms_todo_index" scope="session" time="3600"> 

<%
	 try{
		     Collection agentIds = ProcessContainer.getContainer().getAgentIds(user.getAccount());
		     bean.createTasksFromWorkflow(user.getAccount());
			 if(agentIds != null && agentIds.size() > 0){
				 Iterator iter = agentIds.iterator();
				 while(iter.hasNext()){
					 String agentId = (String)iter.next();
					 bean.createTasksFromWorkflow(agentId);
				 }
			 }
			 System.out.println("---------------------已经更新用户‘"+user.getName()+"’ 的工作流待办任务。");
	 } catch(Exception ex){
		 ex.printStackTrace();
	 }

%>

 </oscache:cache>

<%
    Collection rows = bean.getTodoInstances(user.getAccount());
%>
<table width="620" border="0" cellspacing="1" cellpadding="0" class="list-box">
    <tr class="list-title">
            <td align="center">事&nbsp;&nbsp;项</td>
            <td width="95" align="center">PastDue</td>
            <td width="95" align="center">Caution</td>
            <td width="95" align="center">OK</td>
          </tr>
		  <%if(rows != null && rows.size()> 0){
			  Iterator iterator008 = rows.iterator();
			  while(iterator008.hasNext()){
	               ToDoInstance tdi = (ToDoInstance) iterator008.next();
				   pageContext.setAttribute("todo", tdi.getToDo());
				   pageContext.setAttribute("tdi",tdi);
			  %>
          <tr class="list-a">
            <td height="20" align="left">
			<a href="<%=context%><c:out value="${todo.listLink}" escapeXml="true"/>&todoId=<c:out value="${todo.id}"/>" 
			     title="<c:out value="${todo.title}"/>   <c:out value="${todo.content}"/>">
			     <c:out value="${todo.title}"/>
			</a>
			</td>
            <td align="center" class="red">
			     <c:out value="${tdi.qty03}"/>
			</td>
            <td align="center" class="yellow">
			     <c:out value="${tdi.qty02}"/>
			</td>
            <td align="center" class="green">
			     <c:out value="${tdi.qty01}"/>
			</td>
          </tr>
		  <%     }
				  }
		  %>
 </table>