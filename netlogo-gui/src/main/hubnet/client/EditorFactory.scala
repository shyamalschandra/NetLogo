// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.hubnet.client

import javax.swing.{ JScrollPane, ScrollPaneConstants }

import org.nlogo.editor.AbstractEditorArea
import org.nlogo.api.CompilerServices

class EditorFactory(compiler: CompilerServices) extends org.nlogo.window.EditorFactory {
  override def newEditor(cols: Int, rows: Int, enableFocusTraversal: Boolean, enableHighlightCurrentLine: Boolean): org.nlogo.editor.AbstractEditorArea =
    newEditor(cols, rows, enableFocusTraversal, null, false, enableHighlightCurrentLine)
  def newEditor(cols: Int, rows: Int , enableFocusTraversal: Boolean, listener: java.awt.event.TextListener, isApp: Boolean,
                enableHighlightCurrentLine: Boolean = false) =
    new org.nlogo.window.CodeEditor(
      rows, cols,
      new java.awt.Font(org.nlogo.awt.Fonts.platformMonospacedFont, java.awt.Font.PLAIN, 12),
      enableFocusTraversal, listener,
      new org.nlogo.window.EditorColorizer(compiler))

  def scrollPane(editor: AbstractEditorArea): JScrollPane =
    new JScrollPane(
      editor,
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED)
}
