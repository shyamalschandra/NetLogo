// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.editor

import java.awt.Component
import java.awt.event.{ ActionEvent, KeyEvent }
import javax.swing.{ AbstractAction, Action }, Action.{ ACCELERATOR_KEY, ACTION_COMMAND_KEY, NAME }
import javax.swing.event.{ ChangeEvent, ChangeListener }
import javax.swing.text._
import javax.swing.text.DefaultEditorKit.{ CutAction, CopyAction, PasteAction, InsertContentAction, SelectAllAction }

import org.nlogo.api.Refreshable
import org.nlogo.core.I18N
import org.nlogo.swing.UserAction //TODO: Depend won't like this...

import KeyBinding._
import RichDocument._

object Actions {
  /// default editor kit actions
  private val actionMap =
    new DefaultEditorKit().getActions.map{ a => (a.getValue(Action.NAME), a) }.toMap

  val CutAction           = new NetLogoCutAction()
  val CopyAction          = new NetLogoCopyAction()
  val PasteAction         = new NetLogoPasteAction()
  val DeleteAction        = new NetLogoDeleteAction()
  val SelectAllAction     = new NetLogoSelectAllAction()

  def getDefaultEditorKitAction(name:String) = actionMap(name)

  abstract class DocumentAction(name: String) extends TextAction(name) {
    override def actionPerformed(e: ActionEvent): Unit = {
      Option(getTextComponent(e)).foreach { component =>
        try {
          perform(component, component.getDocument, e)
        } catch {
          case ex: BadLocationException => throw new IllegalStateException(ex)
        }
      }
    }

    def perform(component: JTextComponent, document: Document, e: ActionEvent): Unit
  }

  class NetLogoPasteAction extends PasteAction {
    putValue(NAME, I18N.gui.get("menu.edit.paste"))
  }

  class NetLogoCopyAction extends CopyAction {
    putValue(NAME, I18N.gui.get("menu.edit.copy"))
  }

  class NetLogoCutAction extends CutAction {
    putValue(NAME, I18N.gui.get("menu.edit.cut"))
  }

  class NetLogoDeleteAction extends InsertContentAction {
    putValue(NAME, I18N.gui.get("menu.edit.delete"))
    putValue(ACTION_COMMAND_KEY, "")
  }

  class NetLogoSelectAllAction extends AbstractAction {
    putValue(NAME, I18N.gui.get("menu.edit.selectAll"))

    val defaultAction =
      getDefaultEditorKitAction(DefaultEditorKit.selectAllAction)

    override def actionPerformed(event: ActionEvent): Unit = {
      defaultAction.actionPerformed(event)
    }
  }
}

import Actions._

trait QuickHelpAction {
  def colorizer: Colorizer

  def doHelp(document: Document, offset: Int, component: Component): Unit = {
    if (offset != -1) {
      val lineNumber = document.offsetToLine(offset)
      for {
        lineText    <- document.getLineText(document.offsetToLine(offset))
        tokenString <- colorizer.getTokenAtPosition(lineText, offset - document.lineToStartOffset(lineNumber))
      } {
        colorizer.doHelp(component, tokenString)
      }
    }
  }
}

class MouseQuickHelpAction(val colorizer: Colorizer)
  extends AbstractAction(I18N.gui.get("tabs.code.rightclick.quickhelp"))
  with EditorAwareAction
  with QuickHelpAction {

  putValue(UserAction.ActionCategoryKey, UserAction.HelpCategory)

  override def actionPerformed(e: ActionEvent): Unit = {
    doHelp(editor.getDocument, documentOffset, editor)
  }
}

class KeyboardQuickHelpAction(val colorizer: Colorizer)
  extends Actions.DocumentAction(I18N.gui.get("menu.help.lookUpInDictionary"))
  with QuickHelpAction {

  putValue(UserAction.ActionCategoryKey, UserAction.HelpCategory)
  putValue(ACCELERATOR_KEY, keystroke(KeyEvent.VK_F1))
  putValue(ACTION_COMMAND_KEY, "org.nlogo.editor.quickHelp")

  override def perform(component: JTextComponent, document: Document, e: ActionEvent): Unit = {
    val targetOffset = component.getSelectionEnd
    doHelp(component.getDocument, targetOffset, component)
  }
}
