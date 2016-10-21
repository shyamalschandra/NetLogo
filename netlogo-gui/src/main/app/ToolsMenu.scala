// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

/** note that multiple instances of this class may exist as there are now multiple frames that
 each have their own menu bar and menus ev 8/25/05 */

import javax.swing.{ Action, JMenuItem }

import org.nlogo.core.{ AgentKind, I18N }
import org.nlogo.swing.UserAction
import org.nlogo.window.GUIWorkspace

class ToolsMenu(app: App, modelSaver: ModelSaver)
  extends org.nlogo.swing.Menu(I18N.gui.get("menu.tools"))
  with UserAction.Menu {
  implicit val i18nName = I18N.Prefix("menu.tools")

  setMnemonic('T')
  if (!System.getProperty("os.name").startsWith("Mac")) {
    addMenuItem(I18N.gui("preferences"), app.showPreferencesDialog _)
    addSeparator()
  }
  addMenuItem('/', app.tabs.interfaceTab.commandCenterAction)
  addSeparator()
  addMenuItem(I18N.gui("colorSwatches"), openColorDialog _)
  addMenuItem(I18N.gui("turtleShapesEditor"),
              () => app.turtleShapesManager.init(I18N.gui("turtleShapesEditor")))
  addMenuItem(I18N.gui("linkShapesEditor"),
              () => app.linkShapesManager.init(I18N.gui("linkShapesEditor")))
  addMenuItem(app.previewCommandsEditor.title, 'P', true, () =>
    app.workspace.previewCommands =
      app.previewCommandsEditor.getPreviewCommands(modelSaver.currentModel, app.workspace.getModelPath))
  addMenuItem(I18N.gui("behaviorSpace"), 'B', true, () => app.labManager.show())
  addMenuItem(I18N.gui("systemDynamicsModeler"), 'D', true, app.aggregateManager.showEditor _)
  addSeparator()
  addMenuItem(I18N.gui("hubNetClientEditor"), openHubNetClientEditor _)
  addMenuItem('H', true, app.workspace.hubNetControlCenterAction)

  def openColorDialog(): Unit = {
    if(app.colorDialog == null) {
      app.colorDialog =
        new org.nlogo.window.ColorDialog(app.frame, false)
      org.nlogo.awt.Positioning.center(app.colorDialog, app.frame)
      app.colorDialog.showDialog()
    }
    else {
      org.nlogo.awt.Positioning.center(app.colorDialog, app.frame)
      app.colorDialog.setVisible(true)
    }
  }

  def openHubNetClientEditor(): Unit = {
    app.workspace.getHubNetManager.foreach { mgr =>
      mgr.openClientEditor()
      app.frame.addLinkComponent(mgr.clientEditor)
    }
  }

  // this will need to be refined to take groups into account
  def offerAction(action: Action): Unit = {
    add(new JMenuItem(action), 0)
  }
}
