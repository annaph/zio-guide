package org.zio.guide.testing.utils

import org.zio.guide.testing.MyConsole
import org.zio.guide.testing.utils.TestMyConsole.State
import zio.{Chunk, IO, Ref, UIO, URIO, ZIO, ZLayer}

import java.io.{EOFException, IOException}

class TestMyConsole(ref: Ref[State]) extends MyConsole {

  override def printLine(line: String): IO[IOException, Unit] =
    ref.update { state =>
      state.copy(output = state.output :+ line)
    }

  override def readLine: IO[IOException, String] =
    ref
      .getAndUpdateSome { case State(input, output) if input.nonEmpty => State(input.tail, output) }
      .flatMap { state =>
        if (state.input.isEmpty) ZIO.fail(new EOFException("There is no more input left to read"))
        else ZIO.succeed(state.input.head)
      }

  def feedLine(line: String): UIO[Unit] =
    ref.update { state =>
      state.copy(input = state.input :+ line)
    }

  def output: UIO[Chunk[String]] =
    ref.get.map(_.output)

}

object TestMyConsole {

  def test: ZLayer[Any, Nothing, TestMyConsole] =
    ZLayer {
      for {
        ref <- Ref.make(State.empty)
      } yield new TestMyConsole(ref)
    }

  def feedLine(line: String): URIO[TestMyConsole, Unit] =
    ZIO.serviceWithZIO[TestMyConsole](_.feedLine(line))

  def output: URIO[TestMyConsole, Chunk[String]] =
    ZIO.serviceWithZIO[TestMyConsole](_.output)

  final case class State(input: Chunk[String], output: Chunk[String])

  object State {
    def empty: State =
      State(input = Chunk.empty[String], output = Chunk.empty[String])
  }

}
