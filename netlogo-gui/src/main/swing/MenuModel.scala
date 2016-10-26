// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

import scala.math.Ordering

class MenuModel[A](implicit ordering: Ordering[A]) {
  sealed trait Node {
    def key: A
    def groupName: String
  }

  case class Branch(model: MenuModel[A], key: A, groupName: String) extends Node
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

  def createBranch(key: A, groupName: String = ""): MenuModel[A] = {
    val mm = new MenuModel[A]()
    children = (children :+ Branch(mm, key, groupName)).sorted
    mm
  }

  def removeElement(leafValue: A): Unit = {
    children = children.filterNot(_.key == leafValue)
  }

  object NodeOrdering extends Ordering[Node] {
    def compare(x: Node, y: Node): Int = {
      val xGroup = groups.indexOf(x.groupName)
      val yGroup = groups.indexOf(y.groupName)
      if (xGroup == yGroup)
        ordering.compare(x.key, y.key)
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
