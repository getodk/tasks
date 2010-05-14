<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List,
				 java.util.Date,
				 java.text.DateFormat,
				 com.google.appengine.api.datastore.Text,
				 org.odk.tasker.dao.PhoneUser,
				 org.odk.tasker.dao.UserTask,
				 org.odk.tasker.dao.DAOUtil,				 
				 org.odk.tasker.IConstants" %>       
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%!
	List<PhoneUser> users = null;
	List<UserTask> tasks = null;
	UserTask newTask = null;
	PhoneUser selectedUser = null;
	String errorMsg = null;
%>

<%
	users = (List<PhoneUser>)request.getAttribute( IConstants.SERVLET_PHONE_USERS );
	tasks = (List<UserTask>)request.getAttribute( IConstants.SERVLET_USER_TASKS );
	newTask = (UserTask)request.getAttribute( IConstants.SERVLET_NEW_TASK );
	selectedUser = (PhoneUser)request.getAttribute( IConstants.NAME_USER );
	errorMsg = (String)request.getAttribute(IConstants.ERROR_MSG);
%>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link type="text/css" rel="stylesheet" href="/main.css" />

<title>Task upload</title>

<script language="JavaScript1.2" >

var dateRegEx = /\s*[0123]\d\/(0\d|1[012])\/20\d\d\s*/;
var invalidArray = new Array();

function /*void*/ validateDate( dateElement ){

	if( dateElement.value ){
		var valid = dateRegEx.test( dateElement.value );

		dateElement.style.background = valid ? "#FFFFFF" : "#FF6666";
	
		invalidArray[dateElement.name] = valid ? null : "true";
	
		validateSave( valid );
	}else{
		validateSave( true );
	}
}

function /*void*/ validateSave( callingElementValid ){
	var valid = callingElementValid;
	
	if( valid ){ //check other elements
		for( var member in invalidArray ){
			if( invalidArray[member] == "true" ){
				valid = false;
				break;
			}
		}
	}
	
	document.getElementById( "updateButton" ).disabled = !valid;
	document.getElementById( "addButton" ).disabled = !valid;
}

function /*void*/ setArchiveBox( index ){

	var elementName = "<%= IConstants.NAME_DONE %>" + index;
	var doneChecked = document.getElementById( elementName).checked;
	
	elementName = "<%= IConstants.NAME_ARCHIVE %>" + index; 
	var archiveElement = document.getElementById( elementName );
	archiveElement.disabled = !doneChecked;
	 
	if( !doneChecked ){
		archiveElement.checked = false;		
	}
}
</script>


</head>
<body>
<h1>Task upload</h1>

<h2>Select users</h2>
<form method="get" action="/uploadmanager" name="UserSelection">

<select name='<%= IConstants.NAME_USER_ID %>' >
	<% for( PhoneUser user : users){ %>
		<option value='<%= user.getId() %>'><%= user.getName() + " -- " + user.getLocation() %></option>
	<%} %>
</select>
<input type="submit" value="View Tasks for User" name="viewButton" />
</form><br/>

<% if( selectedUser != null ){ %>
<h2><%=  selectedUser.getName() + "  --  "  + selectedUser.getLocation() %></h2>
<form method="post" action="/uploadmanager" name="TaskManagement">
<input type="hidden" name='<%= IConstants.NAME_USER_ID %>' value='<%= selectedUser.getId() %>' />
<table border="1" cellspacing="0" cellpadding="4" width="100%" >
	<tr>
		<th >Done</th>
		<th >Task</th>
		<th >Notes</th>
		<th >Assign date <br/>(dd/mm/yyyy)</th>
		<th >Due date <br/>(dd/mm/yyyy)</th>
		<th >Alert date <br/>(dd/mm/yyyy)</th>
		<th >Done date <br/>(dd/mm/yyyy)</th>
		<th >&nbsp;</th>
		<th >Archive</th>
	</tr>
	<tr>
		<td>&nbsp</td>
		<td>
			<textarea name='<%=IConstants.NAME_USER_TASK %>'
					  cols="40" ><%= newTask.getTask() %></textarea>
		</td>
		<td><textarea 
				   class="readOnly"
				   cols="40" 
				   readonly="readonly"  ></textarea></td>
		<td><%= DAOUtil.getDisplayForDate(new Date()) %></td>
		<td><input type="text" 
				   name='<%=IConstants.NAME_DUE_DATE %>' 
				   value='<%= DAOUtil.getDisplayForDate(newTask.getDueDate()) %>'
				   size="10" 
				   id="dueDateId" onchange='validateDate( this );' >
		</td>
		<td><input type="text" 
				   name='<%=IConstants.NAME_ALERT_DATE %>'
				   value='<%= DAOUtil.getDisplayForDate(newTask.getAlertDate()) %>'
				   size="10"  
				   id="alertDateId" onchange='validateDate( this );' >
		</td>
		<td>&nbsp;</td>
		<td><input type="submit" value='<%=IConstants.VALUE_BUTTON_ADD %>' name='<%=IConstants.NAME_ACTION_BUTTON %>' id="addButton" /></td>
		<td>&nbsp;</td>
	</tr>
	<% for( int i = 0; i < tasks.size(); i++ ){ 
		UserTask task = tasks.get(i); %>
	<tr>
		<td><input type="hidden" 
				   name='<%= IConstants.NAME_USER_TASK_ID + i %>' 
				   value='<%= Long.toString(task.getId()) %>' /> 
			<input type="checkbox" 
				   id='<%= IConstants.NAME_DONE + i %>' 
				   name='<%= IConstants.NAME_DONE + i %>' <%= task.isDone() ? "checked=\"checked\"" : " " %> 
				   onchange="setArchiveBox(<%= i %>)"/>
		</td>
		<td>
			<textarea name='<%=IConstants.NAME_USER_TASK  + i %>'  
					  cols="40" ><%= task.getTask() == null ? "" : task.getTask() %></textarea>
		</td>
		<td>
			<textarea name='<%=IConstants.NAME_USER_NOTES  + i %>'  
				   class="readOnly"
				   cols="40" 
				   readonly="readonly"  ><%= task.getTask() == null ? "" : task.getNotes() %></textarea>
		</td>
		<td><input type="text" 
				   name='<%=IConstants.NAME_ASSIGN_DATE + i %>'
				   class="readOnly" 
				   value='<%= DAOUtil.getDisplayForDate(task.getAssignDate()) %>'
				   size="10" 
				   readOnly="readOnly" >
		</td>
		<td><input type="text" 
				   name='<%=IConstants.NAME_DUE_DATE + i %>' 
				   value='<%= DAOUtil.getDisplayForDate(task.getDueDate()) %>'
				   size="10"  
				   onchange='validateDate( this );' >
		</td>
		<td><input type="text" 
				   name='<%=IConstants.NAME_ALERT_DATE + i %>' 
				   value='<%= DAOUtil.getDisplayForDate(task.getAlertDate()) %>'
				   size="10"   
				   onchange='validateDate( this );' >
		</td>
		<td><input type="text" 
				   name='<%= IConstants.NAME_DONE_DATE + i %>'
				   class="readOnly"
				   size="10"   
				   value='<%= DAOUtil.getDisplayForDate(task.getDoneDate()) %>' 
				   readOnly="readOnly" > 
		</td>
		<td>&nbsp;</td>
		<td><input type="checkbox" 
				   name='<%= IConstants.NAME_ARCHIVE + i %>' 
				   id='<%= IConstants.NAME_ARCHIVE + i %>' 
				   <%= task.isDone() ? " " : "disabled=\"disabled\"" %> />
		</td>
	</tr>
	<% } %>
	
</table>
<br/><br/>
<input type="submit" value='<%=IConstants.VALUE_BUTTON_UPDATE %>' name='<%=IConstants.NAME_ACTION_BUTTON %>' id="updateButton" />
</form>
<% } %>
<br/>
<a href="/">Main Menu</a>

<% if( errorMsg != null ){ %>
<script language="JavaScript1.2" >

function /*void*/ handleError(){
	var dueDateElement = document.getElementById( "dueDateId" );
	var alertDateElement = document.getElementById( "alertDateId" );

	if( dueDateElement.value ){
		dueDateElement.style.background = "#FF6666";
	}

	if( alertDateElement.value && alertDateElement.value.length > 0  ){
		alertDateElement.style.background = "#FF6666";
	}
	
	alert( '<%= errorMsg %> ');
}

handleError();
</script>
<% } %>

</body>
</html>