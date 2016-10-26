// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.swing

import org.scalatest.FunSuite

class MenuModelTests extends FunSuite {
  trait Helper {
    val model = new MenuModel[String]()
    def assertLeafValueAt(value: String, i: Int): Unit = {
      model.children(i) match {
        case model.Leaf(v, _) => assert(v == value)
        case _ => fail(s"expected leaf at $i, but found branch")
      }
    }
    def assertBranchValueAt(b: MenuModel[String], i: Int): Unit = {
      model.children(i) match {
        case model.Branch(m, _, _) => assert(m == b)
        case _ => fail(s"expected branch at $i, but found leaf")
      }
    }
  }
  test("an empty menu model has no items") { new Helper {
    assert(model.leaves.isEmpty)
  } }

  test("when a leaf is inserted into the model, it has a single item") { new Helper {
    model.insertLeaf("abc")
    assert(model.leaves.nonEmpty)
    assertLeafValueAt("abc", 0)
  } }

  test("orders leaves with the provided ordering") { new Helper {
    model.insertLeaf("def")
    model.insertLeaf("abc")
    assertLeafValueAt("abc", 0)
    assertLeafValueAt("def", 1)
  } }

  test("can have branches inserted") { new Helper {
    val b = model.createBranch("")
    b.insertLeaf("abc")
    assertBranchValueAt(b, 0)
  } }

  test("lists both branches and leaves as children") { new Helper {
    val b = model.createBranch("")
    b.insertLeaf("abc")
    model.insertLeaf("def")
    assertBranchValueAt(b, 0)
    assertLeafValueAt("def", 1)
  } }

  test("groups like items") { new Helper {
    model.insertLeaf("ghi", "group2")
    model.insertLeaf("def", "group1")
    model.insertLeaf("abc", "group1")
    assertLeafValueAt("ghi", 0)
    assertLeafValueAt("abc", 1)
    assertLeafValueAt("def", 2)
  } }

  test("allows branches to be placed in groups") { new Helper {
    model.insertLeaf("abc", "group1")
    model.insertLeaf("ghi", "group2")
    val b = model.createBranch("def", "group1")
    assertBranchValueAt(b, 1)
  } }

  test("permits removal of elements") { new Helper {
    model.insertLeaf("abc", "group1")
    model.insertLeaf("ghi", "group2")
    model.insertLeaf("def", "group2")
    model.removeElement("def")
    assert(model.children.length == 2)
    assertLeafValueAt("ghi", 1)
  } }
}
