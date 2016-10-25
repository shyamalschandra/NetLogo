// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

import org.nlogo.core.I18N
import org.nlogo.swing.{ Menu => SwingMenu, UserAction }

class FileMenu(frame: AppFrame)
  extends SwingMenu(I18N.gui.get("menu.file")) {

  implicit val i18nPrefix = I18N.Prefix("menu.file")

  setMnemonic('F')

  override def createSubcategory(key: String): SwingMenu = {
    if (key == UserAction.FileExportSubcategory)
      new SwingMenu(I18N.gui("export"))
    else if (key == UserAction.FileImportSubcategory)
      new SwingMenu(I18N.gui("import"))
    else if (key == UserAction.FileRecentSubcategory)
      new SwingMenu(I18N.gui("recent"))
    else
      super.createSubcategory(key)
  }
}
