<%@ page contentType="text/html;charset=gbk" language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ page import="java.util.*"%>
<%@ page import="com.glaf.base.modules.sys.*"%>
<%@ page import="com.glaf.base.modules.sys.model.*"%>
<%@ page import="com.glaf.base.utils.*"%>
<%
int parent = ParamUtil.getIntParameter(request, "id", 0);
List list = (List)request.getAttribute("list");
if(list!=null && list.size()>0){
  int i=1;
  Iterator iter=list.iterator();
  while(iter.hasNext()){
    SysTree bean=(SysTree)iter.next();
%>
node.childNodes[node.childNodes.length] = new Node(<%=bean.getId()%>, <%=parent%>, "<%=bean.getName()%>", "", "<%=bean.getName()%>");
<%
    i++;
  }
}
%>