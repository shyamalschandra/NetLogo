// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

object UserAction {
  /* Key denoting the I18n localization of a given action, from which the name can be looked up */
  val I18nKey              = "org.nlogo.swing.I18nKey"
  /* Key denoting in which menu an action ought to be included */
  val ActionCategoryKey    = "org.nlogo.swing.ActionCategoryKey"
  /* Key for an action to denote what actions it should be grouped with. Allows like actions to be grouped together. */
  val ActionGroupKey       = "org.nlogo.swing.ActionSubcategoryKey"
  /* Key for an action to share a submenu with */
  val ActionSubcategoryKey = "org.nlogo.swing.ActionSubcategoryKey"

  val FileCategory  = "org.nlogo.swing.FileCategory"
  val EditCategory  = "org.nlogo.swing.EditCategory"
  val ToolsCategory = "org.nlogo.swing.ToolsCategory"
  val HelpCategory  = "org.nlogo.swing.HelpCategory"

  val ToolsVisualsGroup = "org.nlogo.swing.ToolsVisualsGroup"

  trait Menu {
    def offerAction(action: javax.swing.Action): Unit
  }
}

