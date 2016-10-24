// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

// TODO i18n lot of work needed here...

import java.awt.event.ActionEvent
import javax.swing.{ Action, AbstractAction, JTabbedPane }
import UserAction._

object TabsMenu {
  def tabActions(tabs: JTabbedPane): Seq[Action] =
    for (i <- 0 until tabs.getTabCount)
      yield new AbstractAction(tabs.getTitleAt(i)) {
        putValue(ActionCategoryKey, TabsCategory)
        putValue(ActionRankKey,     Double.box(i))
        putValue(Action.ACCELERATOR_KEY, KeyBindings.keystrokeChar(('1' + i).toChar, withMenu = true))
        override def actionPerformed(e: ActionEvent) {
          tabs.setSelectedIndex(i)
        }
      }
}

class TabsMenu(name: String, initialActions: Seq[Action]) extends Menu(name) {
  setMnemonic('A')

  initialActions.foreach(offerAction)

  def this(name: String) =
    this(name, Seq())

  def this(name: String, tabs: JTabbedPane) =
    this(name, TabsMenu.tabActions(tabs))
}
