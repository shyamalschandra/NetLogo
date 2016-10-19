// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.editor

import java.awt.Font
import java.awt.event.{ TextEvent, TextListener }

import javax.swing.{ Action, KeyStroke }
import javax.swing.text.TextAction

import org.nlogo.awt.Fonts.platformMonospacedFont

object EditorConfiguration {
  val defaultFont = new Font(platformMonospacedFont, Font.PLAIN, 12)

  protected val emptyListener =
    new TextListener() { override def textValueChanged(e: TextEvent) { } }

  def default(rows: Int, columns: Int, colorizer: Colorizer) =
    EditorConfiguration(rows, columns, defaultFont, emptyListener, colorizer, Map(), Seq(), false, true)
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
}
  //TOOD: Can this class configure the editor without the editor needing to know about all of its values?
