// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.editor

import java.awt.Font
import java.awt.event.KeyEvent

import javax.swing.{ Action, JPopupMenu }
import javax.swing.text.Document

import org.fife.ui.rtextarea.RTextScrollPane
import org.fife.ui.rsyntaxtextarea.{ folding, AbstractTokenMakerFactory, RSyntaxTextArea, SyntaxConstants, Theme, TokenMakerFactory },
  folding.FoldParserManager

import org.nlogo.ide.NetLogoFoldParser
import KeyBinding._

class AdvancedEditorArea(val configuration: EditorConfiguration, rows: Int, columns: Int)
  extends RSyntaxTextArea(rows, columns) with AbstractEditorArea {

  TokenMakerFactory.getDefaultInstance
    .asInstanceOf[AbstractTokenMakerFactory]
    .putMapping("netlogo", "org.nlogo.ide.NetLogoTokenMaker")

  FoldParserManager.get.addFoldParserMapping("netlogo", new NetLogoFoldParser())

  setSyntaxEditingStyle("netlogo")
  setCodeFoldingEnabled(true)

  val theme =
    Theme.load(getClass.getResourceAsStream("/system/netlogo-editor-style.xml"))
  theme.apply(this)

  def enableBracketMatcher(enable: Boolean): Unit = {
    setBracketMatchingEnabled(enable)
  }

  override def getActions(): Array[Action] =
    super.getActions.filter(_.getValue(Action.NAME) != "RSTA.GoToMatchingBracketAction").toArray[Action]

  override def createPopupMenu(): JPopupMenu = {
    val popupMenu = super.createPopupMenu
    configuration.contextActions.foreach(popupMenu.add)
    popupMenu
  }

  def getEditorKit(): javax.swing.text.EditorKit = ???
  def getEditorKitForContentType(contentType: String): javax.swing.text.EditorKit = ???
  def setEditorKit(kit: javax.swing.text.EditorKit): Unit = ???
  def lineToEndOffset(doc: Document,line: Int): Int = ???
  def lineToStartOffset(doc: Document,line: Int): Int = ???
  def offsetToLine(doc: Document,line: Int): Int = ???
  def setIndenter(indenter: Indenter): Unit = {
    indenter.addActions(configuration, getInputMap)
    // TODO: EditorArea also uses indenter in replaceSelection, although I don't know
    // whether we need to do that or not
  }
  // this needs to be implemented if we ever allow tab-based focus traversal
  // with this editor area
  def setSelection(s: Boolean): Unit = {
  }
}
