// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

import javax.swing.{ Action, JMenu, JMenuItem }
import UserAction.{ ActionRankKey, DefaultRank, RichUserAction }

class Menu(text: String) extends JMenu(text) with UserAction.Menu {
  def addMenuItem(name: String, fn: () => Unit): javax.swing.JMenuItem =
    addMenuItem(RichAction(name) { _ => fn() })
  def addMenuItem(name: String, c: Char, shifted: Boolean, fn: () => Unit): javax.swing.JMenuItem =
    addMenuItem(c, shifted, RichAction(name) { _ => fn() })
  def addMenuItem(text: String): javax.swing.JMenuItem =
    addMenuItem(text, 0.toChar, false, null: javax.swing.Action, true)
  def addMenuItem(text: String, shortcut: Char): javax.swing.JMenuItem =
    addMenuItem(text, shortcut, false, null: javax.swing.Action, true)
  def addMenuItem(action: javax.swing.Action): javax.swing.JMenuItem =
    addMenuItem(action.getValue(javax.swing.Action.NAME).asInstanceOf[String], action)
  def addMenuItem(text: String, action: javax.swing.Action): javax.swing.JMenuItem =
    addMenuItem(text, 0.toChar, false, action, true)
  def addMenuItem(text: String, shortcut: Char, action: javax.swing.Action): javax.swing.JMenuItem =
    addMenuItem(text, shortcut, false, action, true)
  def addMenuItem(text: String, shortcut: Char, action: javax.swing.Action, addMenuMask: Boolean): javax.swing.JMenuItem =
    addMenuItem(text, shortcut, false, action, addMenuMask)
  def addMenuItem(shortcut: Char, action: javax.swing.Action): javax.swing.JMenuItem =
    addMenuItem(action.getValue(javax.swing.Action.NAME).asInstanceOf[String],
                shortcut, action, true)
  def addMenuItem(shortcut: Char, shift: Boolean, action: javax.swing.Action): javax.swing.JMenuItem =
    addMenuItem(action.getValue(javax.swing.Action.NAME).asInstanceOf[String],
                shortcut, shift, action, true)
  def addMenuItem(text: String, shortcut: Char, shift: Boolean): javax.swing.JMenuItem =
    addMenuItem(text, shortcut, shift, null: javax.swing.Action, true)
  def addMenuItem(text: String, shortcut: Char, shift: Boolean, action: javax.swing.Action): javax.swing.JMenuItem =
    addMenuItem(text, shortcut, shift, action, true)
  def addMenuItem(text: String, shortcut: Char, shift: Boolean, action: javax.swing.Action, addMenuMask: Boolean): javax.swing.JMenuItem = {
    val item =
      if(action == null)
        new javax.swing.JMenuItem(text)
      else {
        val item = new javax.swing.JMenuItem(action)
        item.setText(text)
        item
      }
    val mask = if(shift) java.awt.event.InputEvent.SHIFT_MASK else 0
    if(shortcut != 0) {
      val menuMask = if (addMenuMask) java.awt.Toolkit.getDefaultToolkit.getMenuShortcutKeyMask else 0
      item.setAccelerator(
        javax.swing.KeyStroke.getKeyStroke(
          shortcut, mask | menuMask))
    }
    item.setIcon(null) // unwanted visual clutter - ST 7/31/03
    add(item)
    item
  }
  def addCheckBoxMenuItem(text: String, initialValue: Boolean, action: javax.swing.Action) = {
    val item =
      if(action == null)
        new javax.swing.JCheckBoxMenuItem(text, initialValue)
      else {
        val item = new javax.swing.JCheckBoxMenuItem(action)
        item.setText(text)
        item.setState(initialValue)
        item
      }
    item.setIcon(null) // unwanted visual clutter - ST 7/31/03
    add(item)
    item
  }

  protected var groups: Map[String, Range] = Map()

  def revokeAction(action: Action): Unit = {
    getMenuComponents.zipWithIndex.collect {
      case (menuItem: JMenuItem, i) if menuItem.getAction == action => i
    }.foreach { removeIndex =>
      remove(removeIndex)
      groups = groups.map {
        case (k, v) if v.start <= removeIndex && v.end >= removeIndex =>
          k -> (v.start to (v.end - 1))
        case (k, v) if v.start > removeIndex =>
          k -> ((v.start - 1) to (v.end - 1))
        case other => other
        }.filterNot {
          case (k, v) => v.end < v.start
        }
    }
  }

  def offerAction(action: Action): Unit = {
    if (groups.isEmpty) {
      add(new JMenuItem(action), 0)
      groups = groups + (action.group -> (0 to 0))
    } else if (! groups.isDefinedAt(action.group)) {
      addSeparator()
      val index = getMenuComponentCount
      add(new JMenuItem(action), index)
      groups = groups + (action.group -> (index to index))
    } else {
      val range = groups(action.group)
      add(new JMenuItem(action), insertionIndex(action, range))
      groups = groups.map {
        case (k, v) if k == action.group     => k -> (range.start to range.end + 1)
        case (k, v) if v.start > range.start => k -> ((v.start + 1) to (v.end + 1))
        case kv                              => kv
      }
    }
  }

  private def insertionIndex(action: Action, range: Range): Int = {
    if (action.getValue(ActionRankKey) != null) {
      val groupRanks: Seq[Double] = getMenuComponents
        .slice(range.start, range.end)
        .map {
          case menuItem: JMenuItem => menuItem.getAction.rank
          case _                   => DefaultRank
        }
      val index = groupRanks.indexWhere(d => d > action.rank)
      range.start + index
    } else
      range.end
  }
}
