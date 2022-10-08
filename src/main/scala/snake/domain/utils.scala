package snake.domain

import snake.models.{Board, Position}

object utils {
  def ensureCorrectPlacement(pos: Position, board: Board): Position = {
    def makeInBound(v: Int, dimension: Int): Int = {
      if (v < 0) dimension - 1
      else if (v >= dimension) 0
      else v
    }

    val adjustedX = makeInBound(pos.x, board.width)
    val adjustedY = makeInBound(pos.y, board.height)

    Position(adjustedX, adjustedY)
  }
}
