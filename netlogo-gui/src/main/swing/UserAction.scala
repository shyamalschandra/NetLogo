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
  /* Key for an action to indicate it's rank within the group, expressed as a java.lang.Double.
   * Lower ranks are listed before higher ranks. Actions missing this key are assumed to have
   * the highest rank. */
  val ActionRankKey        = "org.nlogo.swing.ActionRankKey"

  val FileCategory  = "org.nlogo.swing.FileCategory"
  val TabsCategory  = "org.nlogo.swing.TabsCategory"
  val EditCategory  = "org.nlogo.swing.EditCategory"
  val ToolsCategory = "org.nlogo.swing.ToolsCategory"
  val HelpCategory  = "org.nlogo.swing.HelpCategory"

  val ToolsDialogsGroup = "org.nlogo.swing.ToolsDialogsGroup"
  val ToolsHubNetGroup  = "org.nlogo.swing.ToolsHubNetGroup"

  val HelpWebGroup   = "org.nlogo.swing.HelpWebGroup"
  val HelpAboutGroup = "org.nlogo.swing.HelpAboutGroup"

  trait Menu {
    def offerAction(action: javax.swing.Action): Unit
    def revokeAction(action: javax.swing.Action): Unit
  }

  // convenience methods
  object KeyBindings {
    import java.awt.Toolkit
    import java.awt.event.{ ActionEvent, InputEvent }
    import javax.swing.KeyStroke

    def keystrokeChar(key: Char, withMenu: Boolean = false, withShift: Boolean = false): KeyStroke = {
      val mask: Int =
        (if (withMenu) Toolkit.getDefaultToolkit.getMenuShortcutKeyMask else 0) | (if (withShift) InputEvent.SHIFT_MASK else 0)
      KeyStroke.getKeyStroke(Character.valueOf(key), mask)
    }

    def keystroke(key: Int, withMenu: Boolean = false, withShift: Boolean = false): KeyStroke = {
      val mask: Int =
        (if (withMenu) Toolkit.getDefaultToolkit.getMenuShortcutKeyMask else 0) | (if (withShift) InputEvent.SHIFT_MASK else 0)
      KeyStroke.getKeyStroke(key, mask)
    }
  }
}

