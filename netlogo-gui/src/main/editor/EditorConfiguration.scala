// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.editor

import java.awt.Font
import java.awt.event.{ InputEvent, KeyEvent, TextEvent, TextListener }

import javax.swing.{ Action, KeyStroke }
import javax.swing.text.TextAction

import org.nlogo.awt.Fonts.platformMonospacedFont

object EditorConfiguration {
  val defaultFont = new Font(platformMonospacedFont, Font.PLAIN, 12)

  protected val emptyListener =
    new TextListener() { override def textValueChanged(e: TextEvent) { } }

  def default(rows: Int, columns: Int, colorizer: Colorizer) =
    EditorConfiguration(rows, columns, defaultFont, emptyListener, colorizer, Map(), Seq(), false, false)
}

case class EditorConfiguration(
  rows: Int,
  columns: Int,
  font: Font,
  listener: TextListener,
  colorizer: Colorizer,
  additionalActions: Map[KeyStroke, TextAction],
  menuItems: Seq[Action],
  enableFocusTraversal: Boolean,
  highlightCurrentLine: Boolean) {

    def withFont(font: Font) =
      copy(font = font)
    def withListener(listener: TextListener) =
      copy(listener = listener)
    def withFocusTraversalEnabled(isEnabled: Boolean) =
      copy(enableFocusTraversal = isEnabled)
    def withCurrentLineHighlighted(isHighlighted: Boolean) =
      copy(highlightCurrentLine = isHighlighted)
    def withMenuItems(actions: Seq[Action]) =
      copy(menuItems = actions)
    def withKeymap(keymap: Map[KeyStroke, TextAction]) =
      copy(additionalActions = keymap)

    def configureEditorArea(editor: EditorArea) = {
      import InputEvent.{ SHIFT_MASK => ShiftKey }
      def keystroke(key: Int, mask: Int = 0): KeyStroke =
        KeyStroke.getKeyStroke(key, mask)

      if (highlightCurrentLine) {
        new LinePainter(editor)
      }
      editor.setFont(font)

      additionalActions.foreach {
        case (k, v) => editor.getInputMap.put(k, v)
      }

      editor.setFocusTraversalKeysEnabled(enableFocusTraversal)
      if (enableFocusTraversal) {
        editor.getInputMap.put(keystroke(KeyEvent.VK_TAB),           new TransferFocusAction())
        editor.getInputMap.put(keystroke(KeyEvent.VK_TAB, ShiftKey), new TransferFocusBackwardAction())
      } else {
        editor.getInputMap.put(keystroke(KeyEvent.VK_TAB),           Actions.tabKeyAction)
        editor.getInputMap.put(keystroke(KeyEvent.VK_TAB, ShiftKey), Actions.shiftTabKeyAction)
      }
      val focusTraversalListener = new FocusTraversalListener(editor)
      editor.addFocusListener(focusTraversalListener)
      editor.addMouseListener(focusTraversalListener)
    }
}
