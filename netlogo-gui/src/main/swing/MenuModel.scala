// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

import scala.math.Ordering

class MenuModel[A, B](implicit leafOrdering: Ordering[A], branchOrdering: Ordering[B]) {
  sealed trait Node {
    def groupName: String
  }

  case class Branch(model: MenuModel[A, B], item: B, groupName: String) extends Node
  case class Leaf(item: A, groupName: String) extends Node {
    def key = item
  }

  var children: Seq[Node] = Seq.empty[Node]
  var groups: Seq[String] = Seq()

  def leaves: Seq[A] = children collect {
    case Leaf(item, _) => item
  }

  implicit val nodeOrdering = NodeOrdering

  def insertLeaf(leafValue: A, groupName: String = ""): Unit = {
    groups :+= groupName
    children = (children :+ Leaf(leafValue, groupName)).sorted
  }

  def createBranch(branchValue: B, groupName: String = ""): MenuModel[A, B] = {
    children.collect {
      case Branch(model, item, name) if branchValue == item && groupName == name => model
    }.headOption.getOrElse {
      val mm = new MenuModel[A, B]
      children = (children :+ Branch(mm, branchValue, groupName)).sorted
      mm
    }
  }

  def removeElement(leafValue: A): Unit = {
    children.foreach {
      case Branch(model, _, group) => model.removeElement(leafValue)
      case _ =>
    }
    children = children.filter {
      case Leaf(key, _) => key != leafValue
      case b: Branch => b.model.children.isEmpty
    }
  }

  object NodeOrdering extends Ordering[Node] {
    def compare(x: Node, y: Node): Int = {
      val xGroup = groups.indexOf(x.groupName)
      val yGroup = groups.indexOf(y.groupName)
      if (xGroup == yGroup)
        (x, y) match {
          case (xl: Leaf,  yl: Leaf)    => leafOrdering.compare(xl.item, yl.item)
          case (_: Leaf,   _: Branch)   => -1 // leaves come before branches
          case (_: Branch, _: Leaf)     => 1 // leaves come before branches
          case (xb: Branch, yb: Branch) => branchOrdering.compare(xb.item, yb.item)
        }
      else
        (xGroup, yGroup) match {
          case (-1, _) => 1
          case (_, -1) => -1
          case (xg, yg) if xg < yg => -1
          case _ => 1
        }
    }
  }
}
