// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

import java.awt.event.ActionEvent
import javax.swing.{ AbstractAction, Action, KeyStroke}, Action.{ ACCELERATOR_KEY, NAME }
import UserAction.{ ActionCategoryKey, ActionGroupKey }

class WrappedAction(base: Action, menu: String, group: String, accelerator: KeyStroke)
extends AbstractAction(base.getValue(NAME).toString) {

  putValue(ACCELERATOR_KEY,   accelerator)
  putValue(ActionCategoryKey, menu)
  putValue(ActionGroupKey,    group)

  override def actionPerformed(e: ActionEvent): Unit = {
    base.actionPerformed(e)
  }
}
