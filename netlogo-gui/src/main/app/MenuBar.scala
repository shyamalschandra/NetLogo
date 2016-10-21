// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

import javax.swing.JMenuBar

class MenuBar(fileMenu: FileMenu, editMenu: EditMenu, toolsMenu: ToolsMenu, tabs: Tabs) extends JMenuBar {
  add(fileMenu)
  add(editMenu)
  add(toolsMenu)
  add(new ZoomMenu)
  add(tabs.tabsMenu)
}

