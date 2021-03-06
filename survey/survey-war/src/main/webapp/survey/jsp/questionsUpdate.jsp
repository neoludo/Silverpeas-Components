<%--

    Copyright (C) 2000 - 2009 Silverpeas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    As a special exception to the terms and conditions of version 3.0 of
    the GPL, you may redistribute this Program in connection with Free/Libre
    Open Source Software ("FLOSS") applications as described in Silverpeas's
    FLOSS exception.  You should have received a copy of the text describing
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

<%@ page import="javax.servlet.*"%>
<%@ page import="javax.servlet.http.*"%>
<%@ page import="javax.servlet.jsp.*"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.ObjectInputStream"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.beans.*"%>

<%@ include file="checkSurvey.jsp" %>
<%@ include file="surveyUtils.jsp.inc" %>
<%--
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
--%>
<%@ taglib uri="http://www.silverpeas.com/tld/viewGenerator" prefix="view"%>


<%
//Retrieve parameter
String action = (String) request.getParameter("Action");
String surveyId = (String) request.getParameter("SurveyId");
String surveyName = "";

String m_context = GeneralPropertiesManager.getGeneralResourceLocator().getString("ApplicationURL");

//Icons
String topicAddSrc = m_context + "/util/icons/folderAdd.gif";
String mandatoryField = m_context + "/util/icons/mandatoryField.gif";

ResourceLocator settings = new ResourceLocator("com.stratelia.webactiv.survey.surveySettings", surveyScc.getLanguage());

QuestionContainerDetail survey = null;
if ("UpQuestion".equals(action)) {
      int qId = new Integer((String) request.getParameter("QId")).intValue();
      Vector qV = surveyScc.getSessionQuestions();
      Question q1 = (Question) qV.get(qId);
      Question q2 = (Question) qV.get(qId-1);
      qV.set(qId-1, q1);
      qV.set(qId, q2);
      surveyScc.setSessionQuestions(qV);
      action = "UpdateQuestions";
      
} else if ("DownQuestion".equals(action)) {
      int qId = new Integer((String) request.getParameter("QId")).intValue();
      Vector qV = surveyScc.getSessionQuestions();
      Question q1 = (Question) qV.get(qId);
      Question q2 = (Question) qV.get(qId+1);
      qV.set(qId+1, q1);
      qV.set(qId, q2);
      surveyScc.setSessionQuestions(qV);
      action = "UpdateQuestions";
} else if ("DeleteQuestion".equals(action)) {
      int qId = new Integer((String) request.getParameter("QId")).intValue();
      Vector qV = surveyScc.getSessionQuestions();
      qV.remove(qId);
      surveyScc.setSessionQuestions(qV);
      action = "UpdateQuestions";
}
if ("SendQuestions".equals(action)) {
      Vector qV = surveyScc.getSessionQuestions();
      surveyId = surveyScc.getSessionSurveyId();
      surveyScc.updateQuestions(qV, surveyId);
      action = "UpdateQuestions";
}
if ("UpdateQuestions".equals(action)) {
%>
<html>
<head>
<view:looknfeel />
<script language="javaScript1.2">
function addQuestion() {
    document.questionForm.submit();
}
</script>
</head>
<body>
<%
          Vector questionsV = null;
          if (surveyId != null) {
              surveyScc.removeSessionSurveyId();
              surveyScc.removeSessionQuestions();
              surveyScc.removeSessionSurveyName();
                            
              survey = surveyScc.getSurvey(surveyId);
              Collection questions = survey.getQuestions();
              //questions collection to questions vector
              questionsV = new Vector(questions);
              surveyScc.setSessionQuestions(questionsV);
              surveyScc.setSessionSurveyId(surveyId);
              surveyScc.setSessionSurveyName(survey.getHeader().getTitle());
          }
          questionsV = surveyScc.getSessionQuestions();
          surveyId = surveyScc.getSessionSurveyId();
          surveyName = surveyScc.getSessionSurveyName();

          Window window = gef.getWindow();

          BrowseBar browseBar = window.getBrowseBar();
          browseBar.setDomainName(surveyScc.getSpaceLabel());
          browseBar.setComponentName(surveyScc.getComponentLabel(),"surveyList.jsp?Action=View");
          browseBar.setExtraInformation(resources.getString("SurveyUpdate")+" '"+surveyName+"'");

          OperationPane operationPane = window.getOperationPane();
          operationPane.addOperation("icons/questionAdd.gif", resources.getString("QuestionAdd"), "javaScript:addQuestion()");
          
          out.println(window.printBefore());

          TabbedPane tabbedPane = gef.getTabbedPane();
          tabbedPane.addTab(resources.getString("GML.head"), "surveyUpdate.jsp?Action=UpdateSurveyHeader&SurveyId="+surveyId, action.equals("UpdateSurveyHeader"), true);
          tabbedPane.addTab(resources.getString("SurveyQuestions"), "questionsUpdate.jsp?Action=UpdateQuestions&SurveyId="+surveyId, action.equals("UpdateQuestions"), false);
          out.println(tabbedPane.print());


          
          
          out.println(displayQuestionsUpdateView(surveyScc, questionsV, gef, m_context, settings, resources));
          
%>
          <form name="questionForm" action="questionCreatorBis.jsp" method="post" enctype="multipart/form-data">
          <input type="hidden" name="Action" value="CreateQuestion">
          </form>
<%
          out.println(window.printAfter());
%>
</body>
</html>
<% } %>