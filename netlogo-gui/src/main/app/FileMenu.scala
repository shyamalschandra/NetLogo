// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.app

import org.nlogo.core.I18N
import org.nlogo.swing.{ Menu => SwingMenu, UserAction }

class FileMenu(frame: AppFrame)
  extends SwingMenu(I18N.gui.get("menu.file")) {

    val ExportImportGroup = "ExportImportGroup"

  implicit val i18nPrefix = I18N.Prefix("menu.file")

  setMnemonic('F')

  val subcategoryNamesAndGroups = Map(
    UserAction.FileExportSubcategory -> (I18N.gui("export") -> ExportImportGroup),
    UserAction.FileImportSubcategory -> (I18N.gui("import") -> ExportImportGroup),
    UserAction.FileRecentSubcategory -> (I18N.gui("recent") -> UserAction.FileOpenGroup))

  override def subcategoryNameAndGroup(key: String): (String, String) = {
    subcategoryNamesAndGroups.get(key).getOrElse(super.subcategoryNameAndGroup(key))
  }
}
