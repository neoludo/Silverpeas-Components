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
<%@page import="com.stratelia.webactiv.forums.sessionController.helpers.ForumListHelper"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    response.setHeader("Cache-Control", "no-store"); //HTTP 1.1
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0
    response.setDateHeader("Expires", -1); //prevents caching at the proxy server
%>
<%@ include file="checkForums.jsp"%>
<%
    int messageId = getIntParameter(request, "params");
    Message message = fsc.getMessage(messageId);
    int forumId = message.getForumId();
    String text = message.getText();
    String title = message.getTitle();

    ResourceLocator settings = fsc.getSettings();
    String configFile = SilverpeasSettings.readString(settings, "configFile",
        URLManager.getApplicationURL() + "/wysiwyg/jsp/javaScript/myconfig.js");
%>
<html>
<head>
    <title></title>
<%
    out.println(graphicFactory.getLookStyleSheet());
%>
    <script type="text/javascript" src="<%=context%>/util/javaScript/checkForm.js"></script>
    <script type="text/javascript" src="<%=context%>/forums/jsp/javaScript/forums.js"></script>
    <script type="text/javascript" src="<%=context%>/wysiwyg/jsp/FCKeditor/fckeditor.js"></script>
    <script type="text/javascript">
        function init()
        {
        	var oFCKeditor = new FCKeditor("messageText");
            oFCKeditor.Width = "500";
            oFCKeditor.Height = "300";
            oFCKeditor.BasePath = "<%=URLManager.getApplicationURL()%>/wysiwyg/jsp/FCKeditor/";
            oFCKeditor.DisplayErrors = true;
            oFCKeditor.Config["AutoDetectLanguage"] = false;
            oFCKeditor.Config["DefaultLanguage"] = "<%=fsc.getLanguage()%>";
            oFCKeditor.Config["CustomConfigurationsPath"] = "<%=configFile%>";
            oFCKeditor.ToolbarSet = "quickinfo";
            oFCKeditor.Config["ToolbarStartExpanded"] = true;
            oFCKeditor.ReplaceTextarea();
        }

        function validateMessage()
        {
            if (document.forms["forumsForm"].elements["messageTitle"].value == "")
            {
                alert('<%=resource.getString("emptyMessageTitle")%>');
            }
            else if (!isTextFilled())
            {
                alert('<%=resource.getString("emptyMessageText")%>');
            }
            else
            {
                document.forms["forumsForm"].submit();
            }
        }
    </script>
</head>

<body marginheight="5" marginwidth="5" leftmargin="5" topmargin="5" bgcolor="#FFFFFF" <%addBodyOnload(out, fsc, "init()");%>>
<%
    Window window = graphicFactory.getWindow();
    Frame frame=graphicFactory.getFrame();

    BrowseBar browseBar = window.getBrowseBar();
    browseBar.setDomainName(fsc.getSpaceLabel());
    browseBar.setComponentName(fsc.getComponentLabel(), ActionUrl.getUrl("main", -1, forumId));
    browseBar.setPath(ForumListHelper.navigationBar(forumId, resource, fsc));

    out.println(window.printBefore());
    out.println(frame.printBefore());

    String formAction = "";
%>
    <center>
        <table width="98%" border="0" cellspacing="0" cellpadding="0" class="intfdcolor4">
        <form name="forumsForm" action="viewMessage" method="post">
            <tr>
                <td valign="top">
                    <table width="100%" border="0" cellspacing="0" cellpadding="5" class="contourintfdcolor">
                        <tr>
                            <td valign="top">
                                <table border="0" cellspacing="0" cellpadding="5" width="100%">
                                    <tr>
                                        <td colspan="2"><span class="txtnav"><!-- <img src="icons/fo_flechebas.gif" width="11" height="6">&nbsp;<%=resource.getString("repondre")%> --></span></td>
                                    </tr>
                                    <tr>
                                        <td align="left" valign="top"><span class="txtlibform"><%=resource.getString("messageTitle")%> :&nbsp;</span></td>
                                        <td valign="top"><input type="text" name="messageTitle" value="<%=title%>" size="88" maxlength="<%=DBUtil.getTextFieldLength()%>"></td>
                                    </tr>
                                    <tr>
                                        <td align="left" valign="top"><span class="txtlibform"><%=resource.getString("messageText")%> :&nbsp;</span></td>
                                        <td valign="top"><font size=1><textarea name="messageText" id="messageText"><%=text%></textarea></font></td>
                                    </tr>
                                    <tr>
                                        <td align="left" valign="top"><span class="txtlibform"><%=resource.getString("subscribeMessage")%> :&nbsp;</span></td>
                                        <td valign="top"><input type="checkbox" name="subscribeMessage"></td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                    <br>
                    <center><%

    String backUrl = ActionUrl.getUrl("viewMessage", "viewForum", 1, messageId, forumId);
    ButtonPane buttonPane = graphicFactory.getButtonPane();
    buttonPane.addButton(graphicFactory.getFormButton(
        resource.getString("valider"), "javascript:validateMessage();", false));
    buttonPane.addButton(graphicFactory.getFormButton(
        resource.getString("annuler"), backUrl, false));
    buttonPane.setHorizontalPosition();
    out.println(buttonPane.print());
%>
                    </center>
                </td>
            </tr>
            <input type="hidden" name="action" value="8"/>
            <input type="hidden" name="forumId" value="<%=message.getForumId()%>"/>
            <input type="hidden" name="params" value="<%=messageId%>"/>
            <input type="hidden" name="parentId" value="<%=message.getParentId()%>"/>
        </form>
        </table>
    </center><%

    out.println(frame.printMiddle());
    out.println(frame.printAfter());
    out.println(window.printAfter());
%>
</body>
</html>
