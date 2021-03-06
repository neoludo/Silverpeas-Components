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
/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

package com.stratelia.webactiv.yellowpages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJBException;

import com.stratelia.silverpeas.silverstatistics.control.ComponentStatisticsInterface;
import com.stratelia.silverpeas.silverstatistics.control.UserIdCountVolumeCouple;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.OrganizationController;
import com.stratelia.webactiv.util.EJBUtilitaire;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.contact.model.ContactDetail;
import com.stratelia.webactiv.util.exception.SilverpeasRuntimeException;
import com.stratelia.webactiv.util.node.control.NodeBm;
import com.stratelia.webactiv.util.node.control.NodeBmHome;
import com.stratelia.webactiv.util.node.model.NodeDetail;
import com.stratelia.webactiv.util.node.model.NodePK;
import com.stratelia.webactiv.yellowpages.control.ejb.YellowpagesBm;
import com.stratelia.webactiv.yellowpages.control.ejb.YellowpagesBmHome;
import com.stratelia.webactiv.yellowpages.model.TopicDetail;
import com.stratelia.webactiv.yellowpages.model.UserContact;
import com.stratelia.webactiv.yellowpages.model.YellowpagesRuntimeException;

/**
 * Class declaration
 * @author
 */
public class YellowpagesStatistics implements ComponentStatisticsInterface {
  private YellowpagesBm kscEjb = null;
  private NodeBm currentNodeBm = null;

  /**
   * Method declaration
   * @param spaceId
   * @param componentId
   * @return
   * @throws Exception
   * @see
   */
  public Collection<UserIdCountVolumeCouple> getVolume(String spaceId, String componentId)
      throws Exception {
    List<UserIdCountVolumeCouple> myArrayList = new ArrayList<UserIdCountVolumeCouple>();

    Collection<NodeDetail> nodes = getNodeBm().getAllNodes(new NodePK("useless", componentId));
    if (nodes != null && !nodes.isEmpty()) {
      Collection<UserContact> c = getContacts("0", spaceId, componentId);

      if (c == null) {
        return null;
      } else {
        for (UserContact contact : c) {
          ContactDetail detail = contact.getContact();
          UserIdCountVolumeCouple myCouple = new UserIdCountVolumeCouple();

          myCouple.setUserId(detail.getCreatorId());
          myCouple.setCountVolume(1);
          myArrayList.add(myCouple);
        }
      }
    }

    return myArrayList;
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  private YellowpagesBm getYellowpagesBm() {
    if (kscEjb == null) {
      try {
        YellowpagesBmHome kscEjbHome = (YellowpagesBmHome) EJBUtilitaire
            .getEJBObjectRef(JNDINames.YELLOWPAGESBM_EJBHOME,
            YellowpagesBmHome.class);

        kscEjb = kscEjbHome.create();
      } catch (Exception e) {
        throw new EJBException(e);
      }
    }
    return kscEjb;
  }

  /**
   * Method declaration
   * @param topicId
   * @param spaceId
   * @param componentId
   * @return
   * @throws Exception
   * @see
   */
  private Collection<UserContact> getContacts(String topicId, String spaceId,
      String componentId) throws Exception {
    Collection<UserContact> c = new ArrayList<UserContact>();
    if (topicId == null)
      return c;

    OrganizationController myOrga = new OrganizationController();

    if (topicId.startsWith("group_")) {
      int nbUsers = myOrga.getAllSubUsersNumber(topicId.substring("group_"
          .length()));
      for (int n = 0; n < nbUsers; n++) {
        ContactDetail detail = new ContactDetail("useless", "useless",
            "useless", "useless", "useless", "useless", "useless", new Date(),
            "0");
        UserContact contact = new UserContact();
        contact.setContact(detail);
        c.add(contact);
      }
    } else {
      getYellowpagesBm().setActor(myOrga.getUserDetail("0"));
      getYellowpagesBm().setPrefixTableName(spaceId);
      getYellowpagesBm().setComponentId(componentId);

      TopicDetail topic = null;

      try {
        topic = getYellowpagesBm().goTo(topicId);
        if (topic != null) {
          c.addAll(topic.getContactDetails());
        }
      } catch (Exception ex) {
        topic = null;
        SilverTrace.info("silverstatistics",
            "YellowpagesStatistics.getContacts()", "root.MSG_GEN_PARAM_VALUE",
            ex);
      }
      // treatment of the nodes of current topic
      if (topic != null) {
        Collection<NodeDetail> subTopics = topic.getNodeDetail().getChildrenDetails();

        for (NodeDetail node : subTopics) {
          if (!(node.getNodePK().getId().equals("0")
              || node.getNodePK().getId().equals("1") || node.getNodePK()
              .getId().equals("2"))) {
            c.addAll(getContacts(node.getNodePK().getId(), spaceId,
                componentId));
          }
        }
      }
    }
    return c;
  }

  private NodeBm getNodeBm() {
    if (currentNodeBm == null) {
      try {
        NodeBmHome nodeBmHome = (NodeBmHome) EJBUtilitaire.getEJBObjectRef(
            JNDINames.NODEBM_EJBHOME, NodeBmHome.class);
        currentNodeBm = nodeBmHome.create();
      } catch (Exception re) {
        throw new YellowpagesRuntimeException("YellowpagesBmEJB.getNodeBm()",
            SilverpeasRuntimeException.ERROR,
            "yellowpages.EX_GET_NODEBM_HOME_FAILED", re);
      }
    }
    return currentNodeBm;
  }

}
