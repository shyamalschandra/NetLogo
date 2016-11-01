// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.window

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.{ ActionEvent, KeyEvent }
import javax.swing.{ AbstractAction, Action, KeyStroke}, Action.{ ACCELERATOR_KEY, ACTION_COMMAND_KEY, NAME }
import javax.swing.text.{ BadLocationException, DefaultEditorKit, Document, JTextComponent, TextAction }
import javax.swing.text.DefaultEditorKit.{ CutAction, CopyAction, PasteAction, InsertContentAction }

import org.nlogo.api.Refreshable
import org.nlogo.core.I18N
import org.nlogo.editor.{ Actions, Colorizer, DocumentAction, QuickHelpAction }
import org.nlogo.swing.UserAction.{ ActionGroupKey, ActionCategoryKey,
  EditCategory, EditClipboardGroup, EditSelectionGroup, HelpCategory, KeyBindings },
    KeyBindings.keystroke

object TextMenuActions {
  /// default editor kit actions
  private val actionMap =
    new DefaultEditorKit().getActions.map{ a => (a.getValue(Action.NAME), a) }.toMap

  private def getDefaultEditorKitAction(name:String) = actionMap(name)

  val CutAction       =
    new WrappedAction(Actions.CutAction, EditCategory, EditClipboardGroup, keystroke('X', withMenu = true))
  val CopyAction      =
    new WrappedAction(Actions.CopyAction, EditCategory, EditClipboardGroup, keystroke('C', withMenu = true))
  val PasteAction     = new WrappedPasteAction(Actions.PasteAction)
  val DeleteAction    =
    new WrappedAction(
      Actions.DeleteAction, EditCategory, EditClipboardGroup, keystroke(KeyEvent.VK_DELETE))
  val SelectAllAction =
    new WrappedAction(
      Actions.SelectAllAction, EditCategory, EditSelectionGroup, keystroke('A', withMenu = true))

  def keyboardQuickHelp(colorizer: Colorizer) =
    new KeyboardQuickHelpAction(colorizer)


  class WrappedAction(base: Action, menu: String, group: String, accelerator: KeyStroke)
    extends AbstractAction(base.getValue(NAME).toString) {

    putValue(ACCELERATOR_KEY,   accelerator)
    putValue(ActionCategoryKey, menu)
    putValue(ActionGroupKey,    group)

    override def actionPerformed(e: ActionEvent): Unit = {
      base.actionPerformed(e)
    }
  }

  class WrappedPasteAction(base: Action)
    extends WrappedAction(base, EditCategory, EditClipboardGroup, keystroke('V', withMenu = true))
    with Refreshable {

    def refresh(): Unit = {
      setEnabled(Toolkit.getDefaultToolkit.getSystemClipboard
        .isDataFlavorAvailable(DataFlavor.stringFlavor))
    }
  }

  class KeyboardQuickHelpAction(val colorizer: Colorizer)
    extends DocumentAction(I18N.gui.get("menu.help.lookUpInDictionary"))
    with QuickHelpAction {

    putValue(ActionCategoryKey,  HelpCategory)
    putValue(ACCELERATOR_KEY,    KeyBindings.keystroke(KeyEvent.VK_F1))

    override def perform(component: JTextComponent, document: Document, e: ActionEvent): Unit = {
      val targetOffset = component.getSelectionEnd
      doHelp(component.getDocument, targetOffset, component)
    }
  }
}
