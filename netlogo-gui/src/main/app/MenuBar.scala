// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

import javax.swing.{ Action, JMenuBar }

import org.nlogo.editor.EditorMenu
import org.nlogo.swing.UserAction, UserAction.{ ActionCategoryKey, ToolsCategory }

class MenuBar(fileMenu: FileMenu, editMenu: EditMenu, toolsMenu: ToolsMenu, tabs: Tabs)
  extends JMenuBar
  with EditorMenu
  with UserAction.Menu {

  add(fileMenu)
  add(editMenu)
  add(toolsMenu)
  add(new ZoomMenu)
  add(tabs.tabsMenu)

  private var helpMenu = Option.empty[HelpMenu]

  def addHelpMenu(newHelpMenu: HelpMenu): Unit = {
    helpMenu = Some(newHelpMenu)
  }

  def editActions(actionGroups: Seq[Seq[Action]]): Unit = {
  }

  override def helpActions(actions: Seq[Action]): Unit = {
    helpMenu.foreach(_.addEditorActions(actions))
  }

  def offerAction(action: javax.swing.Action): Unit = {
    if (action.getValue(ActionCategoryKey) == ToolsCategory) {
      toolsMenu.offerAction(action)
    }
  }
}

