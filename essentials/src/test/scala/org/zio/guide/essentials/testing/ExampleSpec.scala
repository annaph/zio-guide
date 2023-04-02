package org.zio.guide.essentials.testing

import zio.test.Assertion._
import zio.test.TestAspect.nonFlaky
import zio.test.{Assertion, Gen, Spec, TestEnvironment, ZIOSpecDefault, assert, assertZIO, check, checkN}
import zio.{Scope, ZIO}

object ExampleSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite(label = "ExampleSpec")(
    test(label = "addition works") {
      val actual = 1 + 1
      assert(actual)(equalTo(expected = 2))
    },

    test(label = "ZIO.succeed succeeds with specified value") {
      assertZIO(effect = ZIO.succeed(1 + 1))(equalTo(expected = 2))
    },

    test(label = "testing an effect using map operator") {
      ZIO.succeed(1 + 1).map(n => assert(n)(equalTo(expected = 2)))
    },

    test(label = "testing an effect using a for comprehension") {
      for {
        n <- ZIO.succeed(1 + 1)
      } yield assert(n)(equalTo(expected = 2))
    },

    test(label = "and") {
      for {
        x <- ZIO.succeed(1)
        y <- ZIO.succeed(2)
      } yield assert(x)(equalTo(expected = 1)) &&
        assert(y)(equalTo(expected = 2))
    },

    test(label = "hasSameElements") {
      assert(List(1, 1, 2, 3))(hasSameElements(List(3, 2, 1, 1)))
    },

    test(label = "fails") {
      for {
        exit <- ZIO.attempt(code = 1 / 0).catchAll(_ => ZIO.fail(error = ())).exit
      } yield assert(exit)(fails(isUnit))
    },

    test(label = "non empty & all non negative") {
      assert(List(1, 1, 2, 3))(assertNonEmptyAndAllNonNegative)
    },

    test(label = "is empty or contains three elements") {
      assert(List(1, 2, 3))(assertIsEmptyOrContainsThreeElements) &&
        assert(List.empty[Any])(assertIsEmptyOrContainsThreeElements)
    },

    test(label = "contains at least one duplicate") {
      assert(List(1, 2, 1))(assertContainsDuplicate)
    },

    test(label = "this test will be repeated to ensure it is stable") {
      assertZIO(ZIO.succeed(1 + 1))(equalTo(expected = 2))
    } @@ nonFlaky,

    test(label = "integer addition is associative") {
      check(Gen.int, Gen.int, Gen.int) { (x, y, z) =>
        val left = (x + y) + z
        val right = x + (y + z)
        assert(left)(equalTo(right))
      }
    },

    test(label = "integer addition is associative - check 1,000 times") {
      checkN(n = 1000)(Gen.int, Gen.int, Gen.int) { (x, y, z) =>
        val left = (x + y) + z
        val right = x + (y + z)
        assert(left)(equalTo(right))
      }
    },

    test(label = "generate user json string") {
      def toUser(json: String): User = {
        val name = json
          .substring(10)
          .takeWhile(_ != '\"')

        val age = json
          .substring(10 + name.length + 10)
          .takeWhile(_ != '}')
          .toInt

        User(name, age)
      }

      check(userGen) { user =>
        val jsonString = user.jsonString
        assert(toUser(jsonString))(equalTo(user))
      }
    }

  )

  private def assertNonEmptyAndAllNonNegative: Assertion[Iterable[Int]] =
    isNonEmpty && forall(nonNegative)

  private def assertIsEmptyOrContainsThreeElements[T]: Assertion[Iterable[T]] =
    isEmpty || hasSize(equalTo(expected = 3))

  private def assertContainsDuplicate[T]: Assertion[Iterable[T]] =
    not(isDistinct)

  private def userGen: Gen[Any, User] =
    for {
      name <- Gen.asciiString
      age <- Gen.int(min = 18, max = 120)
    } yield User(name.replace("\"", ""), age)

  final case class User(name: String, age: Int) {
    def jsonString: String = s"""{"name": "$name", "age": $age}"""
  }

}
