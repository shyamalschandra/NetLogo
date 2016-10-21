// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.editor

import javax.swing.Action

trait EditorMenu {
  def editActions(actionGroups: Seq[Seq[Action]]): Unit

  def helpActions(actions: Seq[Action]): Unit
}
