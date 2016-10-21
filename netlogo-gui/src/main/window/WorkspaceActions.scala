// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.window

import java.awt.Toolkit
import java.awt.event.{ ActionEvent, InputEvent, KeyEvent }

import javax.swing.{ AbstractAction, Action, KeyStroke }

import org.nlogo.core.{ I18N, AgentKind }
import org.nlogo.swing.UserAction._

object WorkspaceActions {
  implicit val i18nName = I18N.Prefix("menu.tools")

  val HaltGroup     = "org.nlogo.window.WorkspaceActions.Halt"
  val MonitorsGroup = "org.nlogo.window.WorkspaceActions.Monitors"

  def apply(workspace: GUIWorkspace): Seq[Action] = {
    Seq(
      new SimpleGUIWorkspaceAction(I18N.gui("halt"), HaltGroup, workspace, _.halt),
      new SimpleGUIWorkspaceAction(I18N.gui("globalsMonitor"), MonitorsGroup, workspace, _.inspectAgent(AgentKind.Observer)),
      new SimpleGUIWorkspaceAction(I18N.gui("turtleMonitor"), MonitorsGroup, workspace, _.inspectAgent(AgentKind.Turtle)),
      new SimpleGUIWorkspaceAction(I18N.gui("patchMonitor"), MonitorsGroup, workspace, _.inspectAgent(AgentKind.Patch)),
      new SimpleGUIWorkspaceAction(I18N.gui("linkMonitor"), MonitorsGroup, workspace, _.inspectAgent(AgentKind.Link)),
      new SimpleGUIWorkspaceAction(I18N.gui("closeAllAgentMonitors"), MonitorsGroup, workspace, _.closeAgentMonitors),
      new Open3DViewAction(workspace))

  }

  class GUIWorkspaceAction(name: String, workspace: GUIWorkspace) extends AbstractAction(name) {
    def performAction(workspace: GUIWorkspace): Unit = {}

    override def actionPerformed(e: ActionEvent): Unit = {
      performAction(workspace)
    }
  }

  class SimpleGUIWorkspaceAction(name: String, group: String, workspace: GUIWorkspace, action: GUIWorkspace => Unit) extends GUIWorkspaceAction(name, workspace) {
    putValue(ActionCategoryKey, ToolsCategory)
    putValue(ActionGroupKey,    group)

    override def performAction(workspace: GUIWorkspace): Unit = {
      action(workspace)
    }
  }

  class Open3DViewAction(workspace: GUIWorkspace) extends GUIWorkspaceAction(I18N.gui.get("menu.tools.3DView.switch"), workspace) {
    putValue(ActionCategoryKey,      ToolsCategory)
    putValue(ActionGroupKey,         ToolsVisualsGroup)
    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit.getMenuShortcutKeyMask | InputEvent.SHIFT_MASK))

    override def performAction(workspace: GUIWorkspace): Unit = {
      try {
        workspace.glView.open()
        workspace.set2DViewEnabled(false)
      }
      catch {
        case ex: org.nlogo.window.JOGLLoadingException =>
          org.nlogo.swing.Utils.alert("3d", ex.getMessage, "" + ex.getCause, I18N.gui.get("common.buttons.continue") )
      }
    }
  }
}
