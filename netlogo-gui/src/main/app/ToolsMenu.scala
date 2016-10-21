// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

/** note that multiple instances of this class may exist as there are now multiple frames that
 each have their own menu bar and menus ev 8/25/05 */

import javax.swing.{ Action, JMenuItem, JSeparator }

import org.nlogo.core.{ AgentKind, I18N }
import org.nlogo.swing.UserAction
import org.nlogo.window.GUIWorkspace

class ToolsMenu
  extends org.nlogo.swing.Menu(I18N.gui.get("menu.tools"))
  with UserAction.Menu {
  implicit val i18nName = I18N.Prefix("menu.tools")

  setMnemonic('T')

  var groups: Map[String, Range] = Map()

  def offerAction(action: Action): Unit = {
    val actionGroup = action.getValue(UserAction.ActionGroupKey) match {
      case s: String => s
      case _         => "UndefinedGroup"
    }
    if (groups.isEmpty) {
      add(new JMenuItem(action), 0)
      groups = groups + (actionGroup -> (0 to 0))
    } else if (! groups.isDefinedAt(actionGroup)) {
      addSeparator()
      val index = getMenuComponentCount
      add(new JMenuItem(action), index)
      groups = groups + (actionGroup -> (index to index))
    } else {
      val range = groups(actionGroup)
      add(new JMenuItem(action), range.end)
      groups = groups.map {
        case (k, v) if k == actionGroup      => k -> (range.start to range.end + 1)
        case (k, v) if v.start > range.start => k -> ((v.start + 1) to (v.end + 1))
        case kv                              => kv
      }
    }
  }
}
