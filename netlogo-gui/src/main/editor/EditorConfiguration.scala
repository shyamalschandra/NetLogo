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
}
