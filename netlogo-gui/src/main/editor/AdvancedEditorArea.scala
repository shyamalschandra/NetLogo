// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.editor

import java.awt.{ Color, Font }
import java.awt.event.KeyEvent

import javax.swing.{ Action, JPopupMenu }
import javax.swing.text.{ Document, EditorKit }

import org.fife.ui.rtextarea.{ RTextArea, RTextScrollPane }
import org.fife.ui.rsyntaxtextarea.{ folding, AbstractTokenMakerFactory, RSyntaxTextArea, SyntaxConstants, Theme, TokenMakerFactory },
  folding.FoldParserManager

import org.nlogo.ide.NetLogoFoldParser
import KeyBinding._

class AdvancedEditorArea(val configuration: EditorConfiguration)
  extends RSyntaxTextArea(configuration.rows, configuration.columns) with AbstractEditorArea {

  var indenter = Option.empty[Indenter]

  val tmf = TokenMakerFactory.getDefaultInstance.asInstanceOf[AbstractTokenMakerFactory]
  tmf.putMapping("netlogo",   "org.nlogo.ide.NetLogoTwoDTokenMaker")
  tmf.putMapping("netlogo3d", "org.nlogo.ide.NetLogoThreeDTokenMaker")

  FoldParserManager.get.addFoldParserMapping("netlogo", new NetLogoFoldParser())
  FoldParserManager.get.addFoldParserMapping("netlogo3d", new NetLogoFoldParser())

  setSyntaxEditingStyle(if (configuration.is3Dlanguage) "netlogo3d" else "netlogo")
  setCodeFoldingEnabled(true)

  val theme =
    Theme.load(getClass.getResourceAsStream("/system/netlogo-editor-style.xml"))
  theme.apply(this)

  setCaretColor(Color.darkGray)

  configuration.configureAdvancedEditorArea(this)

  def enableBracketMatcher(enable: Boolean): Unit = {
    setBracketMatchingEnabled(enable)
  }

  override def getActions(): Array[Action] = {
    super.getActions.filter(_.getValue(Action.NAME) != "RSTA.GoToMatchingBracketAction").toArray[Action]
  }

  def resetUndoHistory(): Unit = {
    discardAllEdits()
  }

  override def createPopupMenu(): JPopupMenu = {
    val popupMenu = super.createPopupMenu
    configuration.contextActions.foreach(popupMenu.add)
    popupMenu.add(new ToggleFoldsAction(this))
    popupMenu.addPopupMenuListener(new SuspendCaretPopupListener(this))
    popupMenu
  }

  def setIndenter(indenter: Indenter): Unit = {
    indenter.addActions(configuration, getInputMap)
    this.indenter = Some(indenter)
  }

  override def replaceSelection(s: String): Unit = {
    var selection =
      s.dropWhile(c => Character.getType(c) == Character.FORMAT)
        .replaceAllLiterally("\t", "  ")
    super.replaceSelection(s)
    indenter.foreach(_.handleInsertion(selection))
  }

  // this needs to be implemented if we ever allow tab-based focus traversal
  // with this editor area
  def setSelection(s: Boolean): Unit = { }

  import org.nlogo.swing.UserAction
  def undoAction = {
    val action = RTextArea.getAction(RTextArea.UNDO_ACTION)
    action.putValue(UserAction.ActionCategoryKey, UserAction.EditCategory)
    action.putValue(UserAction.ActionGroupKey, UserAction.EditUndoGroup)
    action.putValue(Action.ACCELERATOR_KEY, UserAction.KeyBindings.keystroke('Z', withMenu = true))
    action
  }
  def redoAction = {
    val action = RTextArea.getAction(RTextArea.REDO_ACTION)
    action.putValue(UserAction.ActionCategoryKey, UserAction.EditCategory)
    action.putValue(UserAction.ActionGroupKey, UserAction.EditUndoGroup)
    action.putValue(Action.ACCELERATOR_KEY, UserAction.KeyBindings.keystroke('Y', withMenu = true))
    action
  }

  // These methods are used only by the input widget, which uses editor.EditorArea
  // exclusively at present. - RG 10/28/16
  def getEditorKitForContentType(contentType: String): EditorKit = null
  def getEditorKit(): EditorKit =
    getUI.getEditorKit(this)
  def setEditorKit(kit: EditorKit): Unit = { }
}
