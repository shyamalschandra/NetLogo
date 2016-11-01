// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.window

import javax.swing.JScrollPane
import org.nlogo.editor.{ AbstractEditorArea, Colorizer, EditorConfiguration, EditorScrollPane }

trait EditorFactory {
  def colorizer: Colorizer

  def defaultConfiguration(cols: Int, rows: Int): EditorConfiguration =
    EditorConfiguration.default(cols, rows, colorizer)
      .withMenuActions(Seq(
        TextMenuActions.CutAction,
        TextMenuActions.CopyAction,
        TextMenuActions.PasteAction,
        TextMenuActions.DeleteAction,
        TextMenuActions.SelectAllAction))

  def newEditor(configuration: EditorConfiguration): AbstractEditorArea

  def scrollPane(editor: AbstractEditorArea): EditorScrollPane
}
