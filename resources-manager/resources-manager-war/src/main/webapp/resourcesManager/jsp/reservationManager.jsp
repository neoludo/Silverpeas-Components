<%--

    Copyright (C) 2000 - 2009 Silverpeas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    As a special exception to the terms and conditions of version 3.0 of
    the GPL, you may redistribute this Program in connection with Free/Libre
    Open Source Software ("FLOSS") applications as described in Silverpeas's
    FLOSS exception.  You should have recieved a copy of the text describing
    the FLOSS exception, and it is also available here:
    "http://repository.silverpeas.com/legal/licensing"

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.stratelia.webactiv.beans.admin.UserDetail"%>
<%@ page import="com.silverpeas.resourcesmanager.model.CategoryDetail"%>
<%@ page import="com.silverpeas.resourcesmanager.model.ResourceDetail"%>
<%@ page import="com.silverpeas.resourcesmanager.model.ReservationDetail"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>

<%@ include file="check.jsp" %>

<%
	ReservationDetail 	reservation 			= (ReservationDetail)request.getAttribute("reservation");
	List 				listResourcesProblem 	= (List)request.getAttribute("listResourcesProblem");
	String				defaultDate				= (String) request.getAttribute("DefaultDate");

	String dateBegin = "";
	String dateEnd = "";
	if (StringUtil.isDefined(defaultDate))
	{
		dateBegin 	= defaultDate;
		dateEnd		= defaultDate;
	}

	String event = "";
	String reason = "";
	String place = "";
	String minuteHourDateBegin = "";
	String minuteHourDateEnd = "";
	String reservationId="";

	if(reservation != null){
		reservationId = reservation.getId();
		event = reservation.getEvent();
		reason = reservation.getReason();
		if (reason == null)
			reason = "";
		place = reservation.getPlace();
		if (place == null)
			place = "";
		dateEnd = resource.getOutputDate(reservation.getEndDate());
		dateBegin = resource.getOutputDate(reservation.getBeginDate());
		minuteHourDateBegin = DateUtil.getFormattedTime(reservation.getBeginDate());
		minuteHourDateEnd = DateUtil.getFormattedTime(reservation.getEndDate());
	}
	//creation des boutons Valider et Annuler
	Button validateButton = gef.getFormButton(resource.getString("GML.validate"), "javaScript:verification()", false);
	Button cancelButton = gef.getFormButton(resource.getString("GML.cancel"), "Main",false);
	%>
<html>
<head>
<%
	out.println(gef.getLookStyleSheet());
%>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/animation.js"></script>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/dateUtils.js"></script>
<script type="text/javascript">

function verificationHour(hour){
	      var tab = hour.match(/\d{2}:\d{2}/)
	      if(tab==null){
	    	  return 0;
	      }
	      else
	    	  return 1;
	   }

function verificationDate(Date){
    var tab = Date.match(/\d{2}[/]\d{2}[/]\d{2}/)
    if(tab==null){
  	  return 0;
    }
    else
  	  return 1;
 }


function validerNom(){
	if(document.getElementById("evenement").value == 0){
		document.getElementById('validationNom').innerHTML="Evenement obligatoire";
	}
	else {
		document.getElementById('validationNom').style.display='none';
	}
}
function isCorrectForm()
{
	var errorNb = 0;
	var errorMsg = "";
	if(document.getElementById("evenement").value == 0){
		errorNb++;
		errorMsg+="  - '<%=resource.getString("resourcesManager.evenement")%>' <%=resource.getString("GML.MustBeFilled")%>\n";
	}
	if(document.getElementById("startDate").value == 0 || (verificationDate(document.getElementById("startDate").value) == 0)){
		errorNb++;
		errorMsg+="  - '<%=resource.getString("GML.dateBegin")%>' <%=resource.getString("GML.MustBeFilled")%>\n";
	}
	if((document.getElementById("startHour").value == 0) || (verificationHour(document.getElementById("startHour").value) == 0)){
		errorNb++;
		errorMsg+="  - '<%=resource.getString("resourcesManager.beginHour")%>' <%=resource.getString("GML.MustBeFilled")%>\n";
	}
	if(document.getElementById("endDate").value == 0 ||(verificationDate(document.getElementById("endDate").value) == 0)){
		errorNb++;
		errorMsg+="  - '<%=resource.getString("GML.dateEnd")%>' <%=resource.getString("GML.MustBeFilled")%>\n";
	}
	if(document.getElementById("endHour").value == 0 ||(verificationHour(document.getElementById("endHour").value) == 0)){
		errorNb++;
		errorMsg+="  - '<%=resource.getString("resourcesManager.endHour")%>' <%=resource.getString("GML.MustBeFilled")%>\n";
	}
	var dayStartDate = extractDay(document.getElementById("startDate").value, '<%=resourcesManagerSC.getLanguage()%>');
	var monthStartDate = extractMonth(document.getElementById("startDate").value, '<%=resourcesManagerSC.getLanguage()%>');
	var yearStartDate = extractYear(document.getElementById("startDate").value, '<%=resourcesManagerSC.getLanguage()%>');
	if (!isCorrectDate(yearStartDate, monthStartDate, dayStartDate)) {
		errorNb++;
		errorMsg+="  - '<%=resource.getString("GML.dateBegin")%>' <%=resource.getString("GML.MustContainsCorrectDate")%>\n";
	}

	var dayEndDate = extractDay(document.getElementById("endDate").value, '<%=resourcesManagerSC.getLanguage()%>');
	var monthEndDate = extractMonth(document.getElementById("endDate").value, '<%=resourcesManagerSC.getLanguage()%>');
	var yearEndDate = extractYear(document.getElementById("endDate").value, '<%=resourcesManagerSC.getLanguage()%>');
	if (!isCorrectDate(yearEndDate, monthEndDate, dayEndDate )) {
		errorNb++;
		errorMsg+="  - '<%=resource.getString("GML.dateEnd")%>' <%=resource.getString("GML.MustContainsCorrectDate")%>\n";
	}

	switch(errorNb)
 	{
    	case 0 :
        	result = true;
        	break;
    	case 1 :
        	errorMsg = "<%=resource.getString("GML.ThisFormContains")%> 1 <%=resource.getString("GML.error")%> : \n" + errorMsg;
        	window.alert(errorMsg);
        	result = false;
        	break;
    	default :
        	errorMsg = "<%=resource.getString("GML.ThisFormContains")%> " + errorNb + " <%=resource.getString("GML.errors")%> :\n" + errorMsg;
        	window.alert(errorMsg);
        	result = false;
        	break;
 	}
	return result;
}

function verification(){
	if (isCorrectForm()){
		if(isCorrectDateOrder(document.getElementById("startDate").value,document.getElementById("startHour").value,document.getElementById("endDate").value,document.getElementById("endHour").value))
			document.createForm.submit();
	}
}

function calendar(indexForm, indexField) {
	SP_openWindow('<%=m_context+URLManager.getURL(URLManager.CMP_AGENDA)%>calendar.jsp?indiceForm='+indexForm+'&indiceElem='+indexField,'Calendrier',180,200,'');
}

function getResourceProblem()
{
	var beginDate = document.getElementById("startDate").value;
	var endDate = document.getElementById("endDate").value;
	var beginHour = document.getElementById("startHour").value;
	var endHour = document.getElementById("endHour").value;
	$.post("<%=m_context%>/RAjaxResourcesManagerServlet/dummy", {ComponentId:'<%=componentId%>',reservationId:'<%=reservationId%>',beginDate:beginDate,beginHour:beginHour,endDate:endDate,endHour:endHour}, function(data){
		$("#listResourceProblem").html(data);
	});
}

/* fonction permettant de v�rifier que dateBegin,hourBegin est bien inf�rieur � dateEnd,hourEnd*/
function renverseStrDate(dateIn, hourIn) { //procedure renverse date
	 var sOut = "";
	 sOut = dateIn.charAt(6) + dateIn.charAt(7) + dateIn.charAt(8)+ dateIn.charAt(9) + "/" + dateIn.charAt(3)+ dateIn.charAt(4) + "/" + dateIn.charAt(0)+ dateIn.charAt(1) + "/" + hourIn.charAt(0)+ hourIn.charAt(1)+ "/" + hourIn.charAt(3)+ hourIn.charAt(4);
	 return(sOut);
}

function isCorrectDateOrder(DateBegin, HourBegin, DateEnd, HourEnd) { // procedure du bouton v�rifier
	if (renverseStrDate(DateBegin,HourBegin) < renverseStrDate(DateEnd,HourEnd)) {
		return true;
	} else {
		alert("<%=resource.getString("resourcesManager.ordreDateReservation")%>");
		return false;
	}
}
</script>
</head>
<body>
<%
browseBar.setDomainName(spaceLabel);
browseBar.setComponentName(componentLabel,"Main");
browseBar.setPath(resource.getString("resourcesManager.reservationParametre"));

Board	board		 = gef.getBoard();
out.println(window.printBefore());
out.println(frame.printBefore());
out.println(board.printBefore());


ButtonPane buttonPane = gef.getButtonPane();
buttonPane.addButton(validateButton);
buttonPane.addButton(cancelButton);


%>
<form NAME="createForm" method="post" action="GetAvailableResources">
<TABLE ALIGN="CENTER" CELLPADDING="3" CELLSPACING="0" BORDER="0" WIDTH="100%">
	<tr>
		<TD class="txtlibform" nowrap="nowrap"><% out.println(resource.getString("resourcesManager.evenement"));%> : </TD>
		<TD width="100%"><input type="text" name="evenement" size="60" maxlength="60" id="evenement" onChange="validerNom()" value="<%=event%>" >&nbsp;<span id="validationNom" style="color:red"></span><IMG src="<%=resource.getIcon("resourcesManager.obligatoire")%>" width="5" height="5" border="0"></TD>
	</tr>

	<tr>
		<td class="txtlibform" nowrap="nowrap"><%=resource.getString("GML.dateBegin")%>&nbsp;:&nbsp;</td>
		<td valign="baseline">
		<input type="text" class="dateToPick" name="startDate" size="14" id="startDate" maxlength="<%=DBUtil.getDateFieldLength()%>" value="<%=dateBegin%>" onBlur="getResourceProblem();">&nbsp;<a href="javascript:calendar('0', '1');" onBlur="getResourceProblem();"><img src="<%=resource.getIcon("resourcesManager.calendrier")%>" border="0" align="middle" alt="Afficher le calendrier" title="Afficher le calendrier"></a>&nbsp;<span class="txtnote">(<%=resource.getString("GML.dateFormatExemple")%>)</span>
		<span class="txtlibform">&nbsp;</span><input type="text" name="startHour" id="startHour" size="5" maxlength="5" value="<%=minuteHourDateBegin%>" onBlur="getResourceProblem();">&nbsp;<span class="txtnote">(hh:mm)</span>&nbsp;<img src="<%=resource.getIcon("resourcesManager.obligatoire")%>" width="5" height="5">
	</tr>

	<tr>
		<td class="txtlibform" nowrap="nowrap"><%=resource.getString("GML.dateEnd")%>&nbsp;:&nbsp;</td>
		<td valign="baseline">
		<input type="text" class="dateToPick" name="endDate" id="endDate" size="14" maxlength="<%=DBUtil.getDateFieldLength()%>" value="<%=dateEnd%>" onBlur="getResourceProblem();" >&nbsp;<a href="javascript:calendar('0', '3');" onBlur="getResourceProblem();"><img src="<%=resource.getIcon("resourcesManager.calendrier")%>" border="0" align="middle" alt="Afficher le calendrier" title="Afficher le calendrier"></a>&nbsp;<span class="txtnote">(<%=resource.getString("GML.dateFormatExemple")%>)</span>
		<span class="txtlibform">&nbsp;</span><input type="text" name="endHour" id="endHour" size="5" maxlength="5" value="<%=minuteHourDateEnd%>" onBlur="getResourceProblem();">&nbsp;<span class="txtnote">(hh:mm)</span>&nbsp;<img src="<%=resource.getIcon("resourcesManager.obligatoire")%>" width="5" height="5">
	</tr>

	<tr>
		<TD class="txtlibform" nowrap="nowrap"><% out.println(resource.getString("resourcesManager.raisonReservation"));%> : </TD>
		<TD><textarea name="raison" rows="6" cols="57" ><%=reason%></textarea></TD>
	</tr>


	<tr>
		<TD class="txtlibform" nowrap="nowrap"><% out.println(resource.getString("resourcesManager.lieuReservation"));%> : </TD>
		<TD><input type="text" name="lieu" size="60" maxlength="60" value="<%=place%>">&nbsp;</TD>
	</tr>


	<tr>
		<td colspan="2">( <img border="0" src=<%=resource.getIcon("resourcesManager.obligatoire")%> width="5" height="5"/> : <%=resource.getString("GML.requiredField")%> )</td>
	</tr>
	<!-- si le champs cach� n est pas vide, cela signifie qu on est en train de modifier la reservation -->
	<%if (reservation != null)
	{
		//out.println(reservationId);
		%><input type="HIDDEN" name="reservationId" value="<%=reservationId%>"/>
  <%}%>
</TABLE>
</form>

<div id="listResourceProblem"></div>

<SCRIPT>document.createForm.evenement.focus();</SCRIPT>
<%
out.println(board.printAfter());
out.println("<BR><center>"+buttonPane.print()+"</center><BR>");
out.println(frame.printAfter());
out.println(window.printAfter());
%>
</body>
</html>