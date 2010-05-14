<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List,
				 java.util.Map, 
				 java.util.Date,
				 java.text.DateFormat,
				 com.google.appengine.api.datastore.Text,
				 org.odk.tasker.dao.ArchivedUserTask,
				 org.odk.tasker.dao.DAOUtil,				 
				 org.odk.tasker.IConstants" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%!
	List<ArchivedUserTask> tasks = null;
	int index = 0;
%>

<%
	tasks = (List<ArchivedUserTask>)request.getAttribute( IConstants.SERVLET_USER_TASKS );
	Integer startingIndex = (Integer)request.getAttribute(IConstants.SERVLET_STARTING_INDEX);
  	index = startingIndex != null ? startingIndex.intValue() : 0; 
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Task Archive</title>

<script language="JavaScript1.2">

function /*void*/ nextPage( nextIndex ){
	document.getElementById('<%= IConstants.NAME_PAGE_INDEX %>').value = nextIndex > 0 ? nextIndex : 0;
    document.nextPageForm.submit();
}    	
    	
</script>
</head>
<body>
<h1>Archived Tasks</h1>

<form name="nextPageForm" action="/reviewmanager" method="get"  >
  	<input type="hidden" id='<%= IConstants.NAME_PAGE_INDEX %>' name='<%= IConstants.NAME_PAGE_INDEX %>' value='0' />
</form>
<input id="previousButton" 
   		  type="button" 
   		  value="<< Previous"
   		  style='<%= index == 0 ? "display:none" : "display:inline" %>' 
   		  onclick="nextPage(<%= index - IConstants.PAGE_SIZE %>)"  />
<input id="nextButton" 
   		  type="button" value="Next >>"
   		  style='<%= ((Boolean)request.getAttribute(IConstants.SERVLET_MORE_PAGES))  ? "display:inline" : "display:none" %>'     
   		  onclick="nextPage(<%= index + IConstants.PAGE_SIZE  %>)"  />
   
   <br/><br/>
<table border="1" cellspacing="0" cellpadding="4" width="90%" >
	<tr>
		<th width="15%" >User</th>
		<th width="35%" >Task</th>
		<th width="20%" >Notes</th>
		<th width="10%" >Assign date (dd/mm/yyyy)</th>
		<th width="10%" >Due date (dd/mm/yyyy)</th>
		<th width="10%" >Alert date (dd/mm/yyyy)</th>
		<th width="10%" >Done date (dd/mm/yyyy)</th>
	</tr>
	<% for( int i = 0; i < tasks.size(); i++ ){ 
		ArchivedUserTask task = tasks.get(i); %> 
	<tr>
		<td>
			<%= task.getUserName() %>
		</td>
		<td>
			<%= task.getTask() %>
		</td>
		<td>
			<%= task.getNotes() %>
		</td>
		<td>
			 <%= DAOUtil.getDisplayForDate(task.getAssignDate()) %>
		</td>
		<td>
			<%= DAOUtil.getDisplayForDate(task.getDueDate()) %>
		</td>
		<td>
			<%= DAOUtil.getDisplayForDate(task.getAlertDate()) %>
		</td>
		<td>
			<%= DAOUtil.getDisplayForDate(task.getDoneDate()) %>
		</td>
	</tr>
	<% } %>
</table>
<br/>
<a href="/">Main Menu</a>
 </body>
</html>