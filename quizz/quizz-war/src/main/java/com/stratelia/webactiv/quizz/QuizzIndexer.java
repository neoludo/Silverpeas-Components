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
 * FLOSS exception.  You should have received a copy of the text describing
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

package com.stratelia.webactiv.quizz;

import java.util.Collection;
import java.util.Iterator;

import com.stratelia.silverpeas.peasCore.ComponentContext;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.webactiv.applicationIndexer.control.ComponentIndexerInterface;
import com.stratelia.webactiv.quizz.control.QuizzSessionController;
import com.stratelia.webactiv.util.questionContainer.model.QuestionContainerHeader;

public class QuizzIndexer implements ComponentIndexerInterface {

  private QuizzSessionController scc = null;

  public void index(MainSessionController mainSessionCtrl,
      ComponentContext context) throws QuizzException {
    try {
      scc = new QuizzSessionController(mainSessionCtrl, context);
      Collection quizzes = scc.getAdminQuizzList();
      Iterator itQ = quizzes.iterator();
      while (itQ.hasNext()) {
        QuestionContainerHeader quizzHeader = (QuestionContainerHeader) itQ
            .next();
        scc.updateQuizzHeader(quizzHeader, quizzHeader.getPK().getId());
      }
    } catch (Exception e) {
      throw new QuizzException("QuizzIndexer.index", QuizzException.WARNING,
          "Quizz.EX_CANNOT_UPDATE_QUIZZ_HEADER", e);
    }
  }
}