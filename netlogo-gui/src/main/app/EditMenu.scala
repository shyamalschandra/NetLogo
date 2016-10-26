// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

/** note that multiple instances of this class may exist as there are now multiple frames that each
 have their own menu bar and menus ev 8/25/05 */

import java.awt.event.ActionEvent
import java.util.prefs.Preferences
import javax.swing.{ AbstractAction, Action, JCheckBoxMenuItem, JMenuItem }

import org.nlogo.api.Refreshable
import org.nlogo.app.common.{ Events => AppEvents }
import org.nlogo.editor.Actions
import org.nlogo.core.I18N

class EditMenu(app: App) extends org.nlogo.swing.Menu(I18N.gui.get("menu.edit"))
with AppEvents.SwitchedTabsEvent.Handler
with org.nlogo.window.Events.AboutToQuitEvent.Handler
{

  implicit val i18nName = I18N.Prefix("menu.edit")

  private var refreshables = Set.empty[Refreshable]

  val prefs = Preferences.userNodeForPackage(getClass)
  val lineNumbersKey = "line_numbers"

  val lineNumbersAction = new AbstractAction(I18N.gui("showLineNumbers")) {
    def actionPerformed(e: ActionEvent) =
      app.tabs.lineNumbersVisible = !app.tabs.lineNumbersVisible
  }

  /*
  //TODO i18n - do we need to change the shortcut keys too?
  setMnemonic('E')
  addMenuItem('Z', org.nlogo.editor.UndoManager.undoAction)
  addMenuItem('Y', org.nlogo.editor.UndoManager.redoAction)
  addSeparator()
  addMenuItem(I18N.gui("selectAll"), 'A', Actions.SELECT_ALL_ACTION)
  addSeparator()
  addMenuItem(I18N.gui("find"), 'F', org.nlogo.app.common.FindDialog.FIND_ACTION)
  addMenuItem(I18N.gui("findNext"), 'G', org.nlogo.app.common.FindDialog.FIND_NEXT_ACTION)
  addSeparator()

  //TODO: Move this out of the menu
  val lineNumberPreference = prefs.get(lineNumbersKey, "false").toBoolean
  app.tabs.lineNumbersVisible = lineNumberPreference

  private val lineNumbersItem = addCheckBoxMenuItem(I18N.gui("showLineNumbers"), lineNumberPreference, lineNumbersAction)
  addSeparator()
  val contextualMenuStart = getComponentCount - 1
  add(new JMenuItem(org.nlogo.editor.Actions.shiftLeftAction))
  add(new JMenuItem(org.nlogo.editor.Actions.shiftRightAction))
  addMenuItem(I18N.gui("format"), (java.awt.event.KeyEvent.VK_TAB).toChar, org.nlogo.editor.Actions.tabKeyAction, false)
  addSeparator()
  add(new JMenuItem(org.nlogo.editor.Actions.commentToggleAction))
  addSeparator()
  */


  /*
  lineNumbersAction.setEnabled(false)
  if (lineNumbersItem.isSelected) lineNumbersAction.actionPerformed(null)
  */

  addMenuListener(new javax.swing.event.MenuListener() {
    override def menuSelected(e: javax.swing.event.MenuEvent): Unit = {
      refreshables.foreach(_.refresh())
    }

    override def menuDeselected(e: javax.swing.event.MenuEvent): Unit = {
    }

    override def menuCanceled(e: javax.swing.event.MenuEvent): Unit = {
    }
  })

  final def handle(e: AppEvents.SwitchedTabsEvent) {
    // snapAction.setEnabled(e.newTab == app.tabs.interfaceTab)
    lineNumbersAction.setEnabled(e.newTab != app.tabs.interfaceTab && e.newTab != app.tabs.infoTab)
  }

  override def offerAction(action: Action): Unit = {
    action match {
      case refreshable: Refreshable => refreshables = refreshables + refreshable
      case _ =>
    }
    super.offerAction(action)
  }

  def handle(e: org.nlogo.window.Events.AboutToQuitEvent) = {
    // prefs.put(lineNumbersKey, lineNumbersItem.isSelected.toString)
  }
}
