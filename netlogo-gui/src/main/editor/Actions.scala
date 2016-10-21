// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.editor

import org.nlogo.core.I18N

import java.awt.event.{ ActionEvent, KeyEvent }
import javax.swing.Action, Action.{ ACCELERATOR_KEY, ACTION_COMMAND_KEY }
import javax.swing.event.{ ChangeEvent, ChangeListener }
import javax.swing.text._
import javax.swing.text.DefaultEditorKit.{CutAction, CopyAction, PasteAction, InsertContentAction}

import KeyBinding._
import RichDocument._

object Actions {
  val MenuCategory = "org.nloog.editor.Actions.MenuCategory"
  val HelpMenu = "org.nloog.editor.Actions.HelpMenu"
  val EditMenu = "org.nloog.editor.Actions.EditMenu"


  val commentToggleAction = new CommentToggleAction()
  val shiftLeftAction = new ShiftLeftAction()
  val shiftRightAction = new ShiftRightAction()
  val tabKeyAction = new TabKeyAction()
  val shiftTabKeyAction = new ShiftTabKeyAction()
  val CUT_ACTION = new CutAction()
  val COPY_ACTION = new CopyAction()
  val PASTE_ACTION = new PasteAction()
  val DELETE_ACTION = new InsertContentAction() { putValue(Action.ACTION_COMMAND_KEY, "")  }

  /// default editor kit actions
  private val actionMap = new DefaultEditorKit().getActions.map{ a => (a.getValue(Action.NAME), a) }.toMap
  def getDefaultEditorKitAction(name:String) = actionMap(name)
  val SELECT_ALL_ACTION = getDefaultEditorKitAction(DefaultEditorKit.selectAllAction)

  def setEnabled(enabled:Boolean){
    List(commentToggleAction,shiftLeftAction,shiftRightAction).foreach(_.setEnabled(enabled))
  }

  class TabKeyAction extends MyTextAction("tab-key", _.indentSelection() )
  def quickHelpAction(colorizer: Colorizer) =
    new QuickHelpAction(colorizer)
    /*
    new MyTextAction(i18n("tabs.code.rightclick.quickhelp"),
      e => e.getHelpTarget(e.getSelectionStart).foreach(t => colorizer.doHelp(e, t)))
    */
  def mouseQuickHelpAction(colorizer: Colorizer, i18n: String => String) =
    new MyTextAction(i18n("tabs.code.rightclick.quickhelp"),
      e => e.getHelpTarget(e.getMousePos).foreach(t => colorizer.doHelp(e, t)))
  class MyTextAction(name:String, f: EditorArea => Unit) extends TextAction(name) {
    override def actionPerformed(e:ActionEvent){
      val component = getTextComponent(e)
      if(component.isInstanceOf[EditorArea]) f(component.asInstanceOf[EditorArea])
    }
  }


  abstract class DocumentAction(name: String) extends TextAction(name) {
    override def actionPerformed(e: ActionEvent): Unit = {
      val component = getTextComponent(e)
      try {
        perform(component, component.getDocument, e)
      } catch {
        case ex: BadLocationException => throw new IllegalStateException(ex)
      }
    }

    def perform(component: JTextComponent, document: Document, e: ActionEvent): Unit
  }

  class ShiftLeftAction extends DocumentAction(I18N.gui.get("menu.edit.shiftLeft")) {
    putValue(ACCELERATOR_KEY, keystroke(KeyEvent.VK_OPEN_BRACKET, menuShortcutMask))

    override def perform(component: JTextComponent, document: Document, e: ActionEvent): Unit = {
      val (startLine, endLine) =
        document.selectionLineRange(component.getSelectionStart, component.getSelectionEnd)

      for {
        lineNum <- startLine to endLine
      } {
        val lineStart = document.lineToStartOffset(lineNum)
        if (lineStart != -1) {
          val text = document.getText(lineStart, 1)
          if (text.length > 0 && text.charAt(0) == ' ') {
            document.remove(lineStart, 1)
          }
        }
      }
    }
  }

  class ShiftRightAction extends DocumentAction(I18N.gui.get("menu.edit.shiftRight")) {
    putValue(ACCELERATOR_KEY, keystroke(KeyEvent.VK_CLOSE_BRACKET, menuShortcutMask))

    override def perform(component: JTextComponent, document: Document, e: ActionEvent): Unit = {
      val (startLine, endLine) =
        document.selectionLineRange(component.getSelectionStart, component.getSelectionEnd)
      document.insertBeforeLinesInRange(startLine, endLine, " ")
    }
  }

  class ShiftTabKeyAction extends DocumentAction("shift-tab-key") {
    override def perform(component: JTextComponent, document: Document, e: ActionEvent): Unit = {
      val (startLine, endLine) =
        document.selectionLineRange(component.getSelectionStart, component.getSelectionEnd)
      for {
        lineNum <- startLine to endLine
      } {
        val lineStart = document.lineToStartOffset(lineNum)
        if (lineStart != -1) {
          val text = document.getText(lineStart, 2)
          text.length match {
            case 0 =>
            case 1 if text.charAt(0) == ' ' => document.remove(lineStart, 1)
            case _ =>
              if (text.charAt(0) == ' ' && text.charAt(1) == ' ')
                document.remove(lineStart, 2)
              else if (text.charAt(0) == ' ')
                document.remove(lineStart, 1)
          }
        }
      }
    }
  }

  class CommentToggleAction extends DocumentAction(I18N.gui.get("menu.edit.comment") + " / " + I18N.gui.get("menu.edit.uncomment")) {
    putValue(ACCELERATOR_KEY, keystroke(KeyEvent.VK_SEMICOLON, menuShortcutMask))

    override def perform(component: JTextComponent, document: Document, e: ActionEvent): Unit = {
      val (startLine, endLine) =
        document.selectionLineRange(component.getSelectionStart, component.getSelectionEnd)

      for(currentLine <- startLine to endLine) {
        val lineStart = document.lineToStartOffset(currentLine)
        val lineEnd = document.lineToEndOffset(currentLine)
        val text = document.getText(lineStart, lineEnd - lineStart)
        val semicolonPos = text.indexOf(';')
        val allSpaces = (0 until semicolonPos)
          .forall(i => Character.isWhitespace(text.charAt(i)))
        if (!allSpaces || semicolonPos == -1) {
          document.insertBeforeLinesInRange(startLine, endLine, ";")
          return
        }
      }
      // Logic to uncomment the selected section
      for (line <- startLine to endLine) {
        val lineStart = document.lineToStartOffset(line)
        val lineEnd   = document.lineToEndOffset(line)
        val text      = document.getText(lineStart, lineEnd - lineStart)
        val semicolonPos = text.indexOf(';')
        if (semicolonPos != -1) {
          val allSpaces = (0 until semicolonPos)
            .forall(i => Character.isWhitespace(text.charAt(i)))
          if (allSpaces)
            document.remove(lineStart + semicolonPos, 1)
        }
      }
    }
  }
}

import Actions._

// QuickHelpAction tracks the position of the caret and opens help when needed
class QuickHelpAction(colorizer: Colorizer)
extends Actions.DocumentAction(I18N.gui.get("tabs.code.rightclick.quickhelp"))
with ChangeListener {
  putValue(ACCELERATOR_KEY, keystroke(KeyEvent.VK_F1))
  putValue(ACTION_COMMAND_KEY, "org.nlogo.editor.quickHelp")
  putValue(MenuCategory, HelpMenu)

  private var currentOffset = -1

  override def perform(component: JTextComponent, document: Document, e: ActionEvent): Unit = {
    if (currentOffset != -1) {
      for {
        lineText <- document.getLineText(currentOffset)
        tokenString <- colorizer.getTokenAtPosition(lineText, currentOffset)
        } {
          colorizer.doHelp(component, tokenString)
        }
    }
  }

  override def stateChanged(e: ChangeEvent): Unit = {
    e.getSource match {
      case caret: Caret => currentOffset = caret.getDot
      case _ =>
    }
  }
}
