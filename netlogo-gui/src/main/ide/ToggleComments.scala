// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.ide

import java.awt.event.{ ActionEvent, KeyEvent }
import javax.swing.Action.ACCELERATOR_KEY
import javax.swing.text.{ Document, JTextComponent }

import org.nlogo.core.I18N
import org.nlogo.editor.RichDocument._
import org.nlogo.swing.UserAction,
  UserAction.{ ActionCategoryKey, ActionGroupKey, EditCategory, EditFormatGroup, KeyBindings },
    KeyBindings.keystroke

class ToggleComments
  extends DocumentAction(I18N.gui.get("menu.edit.comment") + " / " + I18N.gui.get("menu.edit.uncomment"))
  with FocusedOnlyAction {

  putValue(ACCELERATOR_KEY,   keystroke(KeyEvent.VK_SEMICOLON, withMenu = true))
  putValue(ActionGroupKey,    EditFormatGroup)
  putValue(ActionCategoryKey, EditCategory)

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
