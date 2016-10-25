// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

import java.awt.Component
import javax.swing.{ Action, JMenu, JMenuItem }
import UserAction.{ ActionRankKey, DefaultGroup, DefaultRank, RichUserAction }

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
  protected var subcategories: Map[String, (Menu, String)] = Map()

  def revokeAction(action: Action): Unit = {
    action.subcategory.flatMap(subcategories.get).foreach(_._1.revokeAction(action))
    getMenuComponents.zipWithIndex.collect {
      case (menuItem: JMenuItem, i) if menuItem.getAction == action => i
    }.foreach { removeIndex =>
      remove(removeIndex)
      val alteredGroups = groups.map {
        case (k, v) if v.start <= removeIndex && v.end >= removeIndex =>
          k -> (v.start to (v.end - 1))
        case (k, v) if v.start > removeIndex =>
          k -> ((v.start - 1) to (v.end - 1))
        case other => other
      }
      alteredGroups.filter {
        case (k, v) => v.end < v.start
      }.foreach {
        case (k, v) if v.end >= 0 => remove(v.end) // remove starting separator
        case _ =>
      }
      groups =
        alteredGroups.filterNot { case (k, v) => v.end < v.start }
    }
  }

  def offerAction(action: Action): Unit = {
    subcategoryItem(action) match {
      case Some((subcategoryItem, subcategoryGroup)) =>
        subcategoryItem.insertAction(action)
      case None                  => insertAction(action)
    }
  }

  def subcategoryItem(action: Action): Option[(Menu, String)] = {
    action.subcategory match {
      case Some(subcatName) if subcategories.isDefinedAt(subcatName) =>
        Some(subcategories(subcatName))
      case Some(subcatName) =>
        val (subcat, group) = createSubcategory(subcatName)
        insertItem(subcat, group, DefaultRank)
        subcategories = subcategories + (subcatName -> (subcat -> group))
        Some(subcat -> group)
      case None => None
    }
  }

  protected def createSubcategory(key: String): (Menu, String) = {
    (new Menu(key), DefaultGroup)
  }

  private def insertAction(action: Action): Unit = {
    insertItem(new JMenuItem(action), action.group, action.rank)
  }

  private def insertItem(item: Component, group: String, rank: Double): Unit = {
    val index = insertionIndex(group, rank)

    if (getClass.getName.contains("File")) { println("raw index: " + index) }
    val separatedIndex =
      if (! groups.isEmpty && ! groups.isDefinedAt(group)) {
        if (getClass.getName.contains("File")) { println("inserting separator") }
        insertSeparator(index)
        index + 1
      } else index

    if (getClass.getName.contains("File")) { println("inserting item: " + item + " @ " + separatedIndex) }
    add(item, separatedIndex)
    updateGroups(group, separatedIndex)
    if (getClass.getName.contains("File")) { println("after insertion in: " + getClass + " " + groups.mkString("\n")) }
  }

  private def updateGroups(groupName: String, insertedIndex: Int): Unit = {
    if (! groups.isDefinedAt(groupName))
      groups = groups + (groupName -> (insertedIndex to insertedIndex))
    else
      groups = groups.map {
        case (k, v) if k == groupName          => k -> (v.start       to (v.end + 1))
        case (k, v) if v.start > insertedIndex => k -> ((v.start + 1) to (v.end + 1))
        case kv                                => kv
      }
  }

  private def insertionIndex(groupName: String, rank: Double): Int = {
    if (groups.isDefinedAt(groupName)) rankedIndex(rank, groups(groupName))
    else getMenuComponentCount
  }

  private def rankedIndex(rank: Double, range: Range): Int = {
    val groupRanks: Seq[Double] = getMenuComponents
      .slice(range.start, range.end)
      .map {
        case menuItem: JMenuItem => menuItem.getAction.rank
        case _                   => DefaultRank
      }
    val index = groupRanks.indexWhere(d => d > rank)
    if (index == -1) range.end else range.start + index
  }
}
