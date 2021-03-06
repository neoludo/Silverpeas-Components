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
package com.silverpeas.resourcesmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJBException;

import com.silverpeas.resourcesmanager.control.ejb.ResourcesManagerBm;
import com.silverpeas.resourcesmanager.control.ejb.ResourcesManagerBmHome;
import com.silverpeas.resourcesmanager.model.ReservationDetail;
import com.stratelia.silverpeas.silverstatistics.control.ComponentStatisticsInterface;
import com.stratelia.silverpeas.silverstatistics.control.UserIdCountVolumeCouple;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.EJBUtilitaire;

public class ResourcesManagerStatistics implements ComponentStatisticsInterface {
  @Override
  public Collection<UserIdCountVolumeCouple> getVolume(String spaceId, String componentId)
      throws Exception {
    List<UserIdCountVolumeCouple> volumes = new ArrayList<UserIdCountVolumeCouple>();

    List<ReservationDetail> allReservations = getResourcesManagerBm().getReservations(componentId);
    for (Iterator<ReservationDetail> iterator = allReservations.iterator(); iterator.hasNext();) {
      ReservationDetail reservationDetail = iterator.next();

      UserIdCountVolumeCouple myCouple = new UserIdCountVolumeCouple();

      myCouple.setUserId(reservationDetail.getUserId());
      myCouple.setCountVolume(1);
      volumes.add(myCouple);
    }

    return volumes;

  }

  private ResourcesManagerBm getResourcesManagerBm() {
    try {
      ResourcesManagerBm bm =
          ((ResourcesManagerBmHome) EJBUtilitaire.getEJBObjectRef("ejb/ResourcesManagerBm",
          ResourcesManagerBmHome.class)).create();
      return bm;
    } catch (Exception e) {
      SilverTrace.error("resourcesManager", "ResourcesManagerStatistics.getResourcesManagerBm",
          "root.MSG_EJB_CREATE_FAILED", e);
      throw new EJBException(e);
    }
  }
}
