package org.scalasteward.core.scalafmt

import org.scalasteward.core.data.Version
import org.scalasteward.core.mock.MockContext._
import org.scalasteward.core.mock.MockState
import org.scalasteward.core.vcs.data.Repo
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ScalafmtAlgTest extends AnyFunSuite with Matchers {
  test("getScalafmtVersion on unquoted version") {
    val repo = Repo("scalafmt-alg", "test-1")
    val repoDir = config.workspace / repo.owner / repo.repo
    val scalafmtConf = repoDir / ".scalafmt.conf"
    val initialState = MockState.empty.add(
      scalafmtConf,
      """maxColumn = 100
        |version=2.0.0-RC8
        |align.openParenCallSite = false
        |""".stripMargin
    )
    val (state, maybeUpdate) =
      initialState.init.flatMap(scalafmtAlg.getScalafmtVersion(repo).run).unsafeRunSync()

    maybeUpdate shouldBe Some(Version("2.0.0-RC8"))
    state shouldBe MockState.empty.copy(
      commands = Vector(List("read", s"$repoDir/.scalafmt.conf")),
      files = Map(
        scalafmtConf ->
          """maxColumn = 100
            |version=2.0.0-RC8
            |align.openParenCallSite = false
            |""".stripMargin
      )
    )
  }

  test("getScalafmtVersion on quoted version") {
    val repo = Repo("scalafmt-alg", "test-2")
    val repoDir = config.workspace / repo.owner / repo.repo
    val scalafmtConf = repoDir / ".scalafmt.conf"
    val initialState = MockState.empty.add(
      scalafmtConf,
      """maxColumn = 100
        |version="2.0.0-RC8"
        |align.openParenCallSite = false
        |""".stripMargin
    )
    val (_, maybeUpdate) =
      initialState.init.flatMap(scalafmtAlg.getScalafmtVersion(repo).run).unsafeRunSync()
    maybeUpdate shouldBe Some(Version("2.0.0-RC8"))
  }
}
