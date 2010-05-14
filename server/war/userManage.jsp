<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List,
				 org.odk.tasker.dao.PhoneUser,
				 org.odk.tasker.IConstants" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%!
	List<PhoneUser> users = null;
%>

<%
	users = (List<PhoneUser>)request.getAttribute( IConstants.SERVLET_PHONE_USERS );
 %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User Manger</title>

</head>
<body>

<form name="userSpecs" action="/usermanager" method="post" >
<table border="1" cellspacing="0" cellpadding="4" width="50%">
	<tr>
		<th width="30%" > Name</th>
		<th width="30%" >Phone Number</th>
		<th width="30%" >Location</th>
		<th width="5%" >Not Available</th>
		<th width="5%" >Inactive</th>
	</tr>

	<% if(users != null ){for(int i = 0; i < users.size(); i++){ PhoneUser user = (PhoneUser)users.get(i); %>
	<tr>
		<td><input name='<%= IConstants.NAME_NAME + i %>' type="text" value='<%=user.getName() %>' />
			<input name='<%= IConstants.NAME_ID + i %>' type="hidden" value='<%=user.getId() %>' />
		</td>
		<td><%=user.getPhoneNumber() %>
			<input name='<%= IConstants.NAME_USER_PHONE_NUMBER + i %>' type="hidden" value='<%=user.getPhoneNumber() %>' />
			<input name='<%= IConstants.NAME_IMEI + i %>' type="hidden" value='<%=user.getIMEI() %>' />
			<input name='<%= IConstants.NAME_IMSI + i %>' type="hidden" value='<%=user.getIMSI() %>' />
			<input name='<%= IConstants.NAME_USER_USERID + i %>' type="hidden" value='<%=user.getUserId() %>' />
			<input name='<%= IConstants.NAME_SIM + i %>' type="hidden" value='<%=user.getSIM() %>' />
		</td>
		<td><input name='<%= IConstants.NAME_LOCATION + i %>' type="text" value='<%=user.getLocation() %>' /></td>
		<td><input name='<%= IConstants.NAME_AVAILABLE + i %>' type="checkbox" <%= user.isAvailable()? "checked=\"checked\"" : ""%> /></td>
		<td><input name='<%= IConstants.NAME_ACTIVE + i %>' type="checkbox" <%= user.isActive()? "checked=\"checked\""  : "" %> /></td>
	</tr>
	<% } }%>
</table>
<input type="submit" value="Update" name="updateButton" />
</form>

<br/><br/>
<a href="/">Main Menu</a>
</html>