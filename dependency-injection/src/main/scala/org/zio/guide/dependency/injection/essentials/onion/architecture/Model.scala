package org.zio.guide.dependency.injection.essentials.onion.architecture

import zio.Chunk

import java.nio.charset.StandardCharsets

object Model {

  case class Issue(id: String, title: String)

  case class Comment(text: String) {
    def toChunks: Chunk[Byte] = {
      val bytes = s"comment:$text".getBytes(StandardCharsets.UTF_8)
      Chunk(bytes.toIndexedSeq: _*)
    }
  }

  object Issue {
    def fromChunks(chunks: Chunk[Byte]): Chunk[Issue] = {
      val str = new String(chunks.toArray, StandardCharsets.UTF_8)

      val issues = str
        .split(";")
        .flatMap(fromString)

      Chunk(issues.toIndexedSeq: _*)
    }

    def fromString(str: String): Option[Issue] =
      str.split("\\|") match {
        case Array(id, title) => Some(Issue(id, title))
        case _ => None
      }
  }

  object Comment {
    def fromChunks(chunks: Chunk[Byte]): Option[Comment] = {
      val str = new String(chunks.toArray, StandardCharsets.UTF_8)

      str.split(":") match {
        case Array(_, text) => Some(Comment(text))
        case _ => None
      }
    }
  }

}
