/**
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stratelia.webactiv.forums.sessionController.helpers;

import com.silverpeas.notation.model.NotationDetail;
import com.silverpeas.util.EncodeHelper;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.silverpeas.util.ResourcesWrapper;
import com.stratelia.webactiv.forums.forumsException.ForumsException;
import com.stratelia.webactiv.forums.models.Forum;
import com.stratelia.webactiv.forums.sessionController.ForumsSessionController;
import com.stratelia.webactiv.forums.url.ActionUrl;
import com.stratelia.webactiv.util.ResourceLocator;
import com.stratelia.webactiv.util.viewGenerator.html.operationPanes.OperationPane;
import java.io.IOException;
import java.util.Date;
import javax.servlet.jsp.JspWriter;

/**
 *
 * @author ehugonnet
 */
public class ForumListHelper {

  public static String navigationBar(int forumId, ResourceLocator resource, ForumsSessionController fsc)
      throws ForumsException {
    boolean loop = false;
    String result = "";
    int currentId = forumId;
    String base = "";

    if (forumId != 0) {
      result = "<a href=\"" + ActionUrl.getUrl("viewForum", -1, forumId) + "\">"
          + EncodeHelper.javaStringToHtmlString(fsc.getForumName(forumId))
          + "</a>";
      loop = true;
    }

    while (loop) {
      int forumParent = fsc.getForumParentId(currentId);
      if (forumParent == 0) {
        result = base + result;
        loop = false;
      } else {
        String parentName = fsc.getForumName(forumParent);
        String line = "<a href=\"" + ActionUrl.getUrl("viewForum", -1, forumParent) + "\">"
            + EncodeHelper.javaStringToHtmlString(parentName) + "</a> &gt; ";
        result = line + result;
        currentId = forumParent;
      }
    }
    return result;
  }

  public static void displayForumLine(Forum forum, ResourcesWrapper resources, JspWriter out,
      int currentPage, String call, boolean admin, boolean moderator, boolean reader, int depth,
      boolean hasChildren, boolean deployed, ForumsSessionController fsc) {
    try {
      int forumId = forum.getId();
      String forumName = forum.getName();
      String forumDescription = forum.getDescription();
      boolean forumActive = forum.isActive();

      String nbSubjects = Integer.toString(fsc.getNbSubjects(forumId));
      String nbMessages = Integer.toString(fsc.getNbMessages(forumId));

      int lastMessageId = -1;
      Date dateLastMessage = null;
      String lastMessageDate = "";
      String lastMessageUser = "";
      Object[] lastMessage = fsc.getLastMessage(forumId);
      if (lastMessage != null) {
        lastMessageId = Integer.parseInt((String) lastMessage[0]);
        lastMessageDate = ForumHelper.convertDate((Date) lastMessage[1], resources);
        lastMessageUser = (String) lastMessage[2];
      }

      out.println("<tr>");

      // 1ère colonne : état des messages (lus / non lus)
      out.print("<td class=\"ArrayCell\">");

      if (!fsc.isExternal() || !reader) {
        // rechercher si l'utilisateur a des messages non lus sur ce forum
        boolean isNewMessage = fsc.isNewMessageByForum(fsc.getUserId(), forumId);
        out.print(
            "<img src=\"icons/" + (isNewMessage ? "newMessage" : "noNewMessage") + ".gif\">");
      }

      // Icone de deploiement
      out.print("<img src=\"icons/1px.gif\">");
      if (depth > 0) {
        out.print("<img src=\"icons/1px.gif\" width=\"" + (depth * 10) + "\" height=\"1\">");
      }
      out.println("</td>");

      // 2ème colonne : nom et description
      out.print("<td class=\"ArrayCell\" width=\"100%\" >");
      out.print("<a href=\"" + ActionUrl.getUrl("viewForum", call, forumId) + "\">");
      out.print("<span class=\"titreForum\">");
      out.print(EncodeHelper.javaStringToHtmlString(forumName));
      out.print("</span>");
      out.print("<br>");
      // description du forum
      out.print("<span class=\"descriptionForum\">");
      out.print(EncodeHelper.javaStringToHtmlString(forumDescription));
      out.print("</span>");
      out.print("</a>");
      out.println("</td>");

      // 3ème colonne : nombre de sujets
      out.print("<td class=\"ArrayCell\" ><span class=\"txtnote\">");
      out.print(EncodeHelper.javaStringToHtmlString(nbSubjects));
      out.println("</span></td>");

      // 4ème colonne : nombre de sujets
      out.print("<td class=\"ArrayCell\" ><span class=\"txtnote\">");
      out.print(EncodeHelper.javaStringToHtmlString(nbMessages));
      out.println("</span></td>");

      // 5ème colonne : dernier message
      out.print(
          "<td nowrap=\"nowrap\" class=\"ArrayCell\" ><span class=\"txtnote\">");
      if (lastMessageDate != null) {
        out.print("<a href=\"" + ActionUrl.getUrl(
            "viewMessage", call, 1, lastMessageId, forumId, true, false) + "\">");
        out.print(EncodeHelper.javaStringToHtmlString(lastMessageDate));
        out.print("<br/>");
        out.print(EncodeHelper.javaStringToHtmlString(lastMessageUser));
        out.print("</a>");
      }
      out.println("</span></td>");

      // 6ème colonne : notation
      NotationDetail notation = fsc.getForumNotation(forumId);
      int globalNote = notation.getRoundGlobalNote();
      int userNote = notation.getUserNote();
      String cellLabel = notation.getNotesCount() + " " + resources.getString("forums.note");
      if (userNote > 0) {
        cellLabel += " - " + resources.getString("forums.yourNote") + " : " + userNote;
      }
      out.print("<td class=\"ArrayCell\" title=\"" + cellLabel + "\"><span class=\"txtnote\">");
      for (int i = 1; i <= 5; i++) {
        out.print("<img class=\"notation_" + (i <= globalNote ? "on" : "off")
            + "\" src=\"" + ForumHelper.IMAGE_NOTATION_EMPTY + "\"/>");
      }
      out.println("</span></td>");

      // 7ème colonne : boutons d'admin
      if (admin || moderator) {
        out.print("<td class=\"ArrayCell\" nowrap>");

        // icone de modification
        out.print("<a href=\"");
        out.print(ActionUrl.getUrl("editForumInfo", call, 2, forumId, currentPage));
        out.println("\"><img src=\"" + ForumHelper.IMAGE_UPDATE + "\" border=\"0\" align=\"middle\" alt=\""
            + resources.getString("editForum") + "\" title=\""
            + resources.getString("editForum") + "\"/></a>");
        out.print("&nbsp;");
        // icone de suppression
        out.print("<a href=\"javascript:confirmDeleteForum('" + forumId + "');\">");
        out.print("<img src=\"" + ForumHelper.IMAGE_DELETE + "\" border=\"0\" align=\"middle\" alt=\""
            + resources.getString("deleteForum") + "\" title=\""
            + resources.getString("deleteForum") + "\"/></a>");
        out.print("&nbsp;");
        // icone de verrouillage
        out.print("<a href=\"");
        out.print(ActionUrl.getUrl((currentPage > 0 ? "viewForum" : "main"), call,
            (forumActive ? 5 : 6), forumId, currentPage));
        out.print("\">");
        if (forumActive) {
          out.print("<img src=\"" + ForumHelper.IMAGE_UNLOCK + "\" border=\"0\" align=\"middle\" alt=\"");
          out.print(resources.getString("lockForum"));
          out.print("\" title=\"" + resources.getString("lockForum") + "\"/>");
        } else {
          out.print("<img src=\"" + ForumHelper.IMAGE_LOCK + "\" border=\"0\" align=\"middle\" alt=\"");
          out.print(resources.getString("unlockForum"));
          out.print("\" title=\"" + resources.getString("unlockForum") + "\"/>");
        }
        out.print("</a>");

        out.println("</td>");
      }
      out.println("</tr>");
    } catch (IOException ioe) {
      SilverTrace.info(
          "forums", "JSPforumsListManager.displayForumLine()", "root.EX_NO_MESSAGE", null, ioe);
    }
  }

  public static void displayForumsList(JspWriter out, ResourcesWrapper resources, boolean admin,
      boolean moderator, boolean reader, int currentForumId, String call,
      ForumsSessionController fsc,
      String categoryId, String nom, String description) {
    try {
      Forum[] forums = fsc.getForumsListByCategory(categoryId);

      if (forums != null) {
        out.println("<tr>");
        out.print("<td colspan=\"6\" class=\"titreCateg\">" + nom);
        if (description != null && description.length() > 0) {
          out.print(" - <i>" + description + "<i>");
        }
        out.println("</td>");

        // boutons d'admin
        if (admin || moderator) {
          out.print("<td class=\"titreCateg\" align=\"center\" nowrap>");
          if (categoryId != null) {
            out.print("<a href=\"EditCategory?CategoryId=" + categoryId + "\">");
            out.print("<img src=\"" + ForumHelper.IMAGE_UPDATE + "\" border=\"0\" align=\"middle\" alt=\""
                + resources.getString("forums.editCategory") + "\" title=\""
                + resources.getString("forums.editCategory") + "\"/></a>");
            out.print("&nbsp;&nbsp;");
            out.print("<a href=\"javascript:confirmDeleteCategory('"
                + String.valueOf(categoryId) + "');\">");
            out.print("<img src=\"" + ForumHelper.IMAGE_DELETE + "\" border=\"0\" align=\"middle\" alt=\""
                + resources.getString("forums.deleteCategory") + "\" title=\""
                + resources.getString("forums.deleteCategory") + "\"/></a>");
            out.print("&nbsp;");
            out.print(
                "<img src=\"icons/1px.gif\" width=\"15\" height=\"15\" border=\"0\" align=\"middle\"/>");
          }
          out.println("</td>");
        }
        out.println("</tr>");

        scanForum(forums, resources, out, currentForumId, call, admin, moderator, reader,
            currentForumId, 0, fsc);
      }
    } catch (IOException ioe) {
      SilverTrace.info("forums", "JSPforumsListManager.displayForumsList()",
          "root.EX_NO_MESSAGE", null, ioe);
    }
  }

  public static void displayForums(JspWriter out, ResourcesWrapper resources, boolean admin,
      boolean moderator, boolean reader, int currentForumId, String call,
      ForumsSessionController fsc,
      String categoryId) {
    Forum[] forums = fsc.getForumsListByCategory(categoryId);
    scanForum(forums, resources, out, currentForumId, call, admin, moderator, reader,
        currentForumId, 0, fsc);
  }
  
  public static void displayChildForums(JspWriter out, ResourcesWrapper resources, boolean admin,
      boolean moderator, boolean reader, int currentForumId, String call, ForumsSessionController fsc) {
    int[] forumIds = fsc.getForumSonsIds(currentForumId);
    for(int forumId : forumIds) {
      Forum forum = fsc.getForum(forumId);
        displayForumLine(forum, resources, out, 0, call, admin, moderator, reader, 0, false, false, fsc);
    }
  }

  public static void scanForum(Forum[] forums, ResourcesWrapper resources, JspWriter out, int currentPage,
      String call, boolean admin, boolean moderator, boolean reader, int currentForumId, int depth,
      ForumsSessionController fsc) {
    Forum forum;
    for (int i = 0; i < forums.length; i++) {
      forum = forums[i];
      int forumParent = forum.getParentId();
      if (forumParent == currentForumId) {
        int forumId = forum.getId();
        boolean hasChildren = hasChildren(forums, forumId);
        boolean isDeployed = fsc.forumIsDeployed(forumId);
        displayForumLine(forum, resources, out, currentPage, call, admin, moderator, reader,
            depth, hasChildren, isDeployed, fsc);
        if (hasChildren && isDeployed) {
          scanForum(forums, resources, out, currentPage, call, admin, moderator, reader,
              forumId, depth + 1, fsc);
        }
      }
    }
  }

  public static boolean hasChildren(Forum[] forums, int currentForumId) {
    int i = 0;
    while (i < forums.length) {
      if (forums[i].getParentId() == currentForumId) {
        return true;
      }
      i++;
    }
    return false;
  }

  public static void displayForumsAdminButtons(boolean moderator, OperationPane operationPane,
      int currentFolderId, String call, ResourceLocator resource) {
    operationPane.addOperation(ForumHelper.IMAGE_ADD_FORUM, resource.getString("newForum"),
        ActionUrl.getUrl("editForumInfo", call, 1, currentFolderId, currentFolderId));
  }

  public static void displayForumsAdminButtonsMain(boolean moderator, OperationPane operationPane,
      int currentFolderId, String call, ResourceLocator resource) {
    operationPane.addOperation(ForumHelper.IMAGE_ADD_FORUM, resource.getString("newForum"),
        ActionUrl.getUrl("editForumInfo", call, 1, currentFolderId, currentFolderId));
    operationPane.addOperation(ForumHelper.IMAGE_ADD_CATEGORY, resource.getString("forums.addCategory"),
        "NewCategory");
  }

  public static int[] displayForumNotation(JspWriter out, ResourcesWrapper resources, int forumId,
      ForumsSessionController fsc, boolean reader) {
    try {
      NotationDetail notation = fsc.getForumNotation(forumId);
      int globalNote = notation.getRoundGlobalNote();
      int userNote = notation.getUserNote();
      out.print("<span class=\"txtnote\">" + resources.getString("forums.forumNote") + " : ");
      for (int i = 1; i <= 5; i++) {
        out.print("<img");
        if (!reader) {
          out.print(" id=\"notationImg" + i + "\"");
        }
        out.print(" style=\"margin-bottom: 0px\" class=\"notation_" + (i <= globalNote ? "on" : "off")
            + "\" src=\"" + ForumHelper.IMAGE_NOTATION_EMPTY + "\"/>");
      }
      out.print(" (" + notation.getNotesCount() + " " + resources.getString("forums.note"));
      if (userNote > 0) {
        out.print(" - " + resources.getString("forums.yourNote") + " : " + userNote);
      }
      out.println(")</span>");
      return new int[]{globalNote, userNote};
    } catch (IOException ioe) {
      SilverTrace.info("forums", "JSPforumsListManager.displayForumNotation()",
          "root.EX_NO_MESSAGE", null, ioe);
      return new int[0];
    }
  }

  private ForumListHelper() {
  }
}
