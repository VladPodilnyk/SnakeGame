package snake.models

import snake.models.Snake.MoveResult.{Moved, Overlap}
import snake.models.Snake.{Direction, MoveResult}

/**
  * A data class that describes a snake.
  * Mutable data structures could be used instead of persistent ones for sake of performance.
  * For simplicity (and a bit from functional/elegant considerations) I've chosen to use immutable data structures.
  *
  * In case of mutable data structure, I think it would be better to make them as private fields and create
  *  a few methods to update Snake class.
  *
  * Optimisations: I thought that it would be nice to be able to check quickly whether
  *                a position is occupied by snake. Instead of checking the whole vector (which is O(n))
  *                we can have a lookup table for that. This will cost us more memory, but we'll have
  *                quick lookups instead.
  *
  * @param body - board cells that occupied by snake
  * @param direction - snake movement direction
  * @param lookup - set of cells that occupied by snake (for a quick lookup)
  */
final case class Snake(body: Vector[Position], direction: Direction, lookup: Set[Position]) {
  def eat(food: Food, newDirection: Direction): Snake = {
    val newBody        = body.prepended(food.position)
    val newLookupTable = lookup.incl(food.position)
    Snake(newBody, newDirection, newLookupTable)
  }

  def move(newHeadPosition: Position, newDirection: Direction): MoveResult = {
    val last    = body.last
    val newBody = body.dropRight(1).prepended(newHeadPosition)
    if (lookup.contains(newHeadPosition)) {
      Overlap(Snake(newBody, newDirection, lookup))
    } else {
      Moved(Snake(newBody, newDirection, lookup.excl(last).incl(newHeadPosition)))
    }
  }
}

object Snake {
  sealed trait MoveResult
  object MoveResult {
    final case class Overlap(lastSnakePosition: Snake) extends MoveResult
    final case class Moved(snake: Snake) extends MoveResult
  }

  sealed trait Direction extends Product with Serializable
  object Direction {
    case object UP extends Direction
    case object DOWN extends Direction
    case object LEFT extends Direction
    case object RIGHT extends Direction
  }
}
