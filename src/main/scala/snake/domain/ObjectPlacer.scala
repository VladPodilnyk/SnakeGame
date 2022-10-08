package snake.domain

import snake.models.GameState.GameError
import snake.models.Snake.Direction.UP
import snake.models.{Board, Food, Position, Snake}

import scala.util.Random

/**
  * A common interface for an object placement algorithm.
  * This should give a flexibility and more control during testing.
  */
trait ObjectPlacer {
  def placeFood(board: Board, snake: Snake): Food
  def placeSnake(board: Board): Either[GameError, Snake]
}

object ObjectPlacer {

  /**
    * Handy implementation solely for testing purposes.
    * Allow us to have a control over food/snake placement in a determinate way.
    */
  object TestPlacer extends ObjectPlacer {
    private[this] val rnd = new Random()

    override def placeFood(board: Board, snake: Snake): Food = {
      // try to generate a food pellets predefined position
      val position        = snake.body.head.copy(x = snake.body.head.x + 1, y = snake.body.head.y - 2)
      var correctPosition = utils.ensureCorrectPlacement(position, board)

      // fallback, in case we place food on snake, randomly generate a new position
      while (snake.lookup.contains(correctPosition)) {
        val rndX = rnd.nextInt(board.width)
        val rndY = rnd.nextInt(board.height)
        correctPosition = Position(rndX, rndY)
      }

      Food(correctPosition)
    }

    // super-simple(silly) way to place a snake on board
    override def placeSnake(board: Board): Either[GameError, Snake] = {
      val headX = board.width / 2
      val headY = board.height / 2
      val head  = Position(headX, headY)
      val tail  = Position(headX, headY + 1)

      if (tail.y > board.height) {
        Left(GameError(s"Couldn't place snake on the board. Game is broken. Please contact developers and get a refund!"))
      } else {
        val body = Vector(head, tail)
        Right(Snake(body, UP, body.toSet))
      }
    }
  }

  /**
    * This implementation is intended to use in real game.
    */
  object RandomPlacer extends ObjectPlacer {
    private[this] val rnd = new Random()

    /**
     * For prod usage, I guess it would be better to generate snake position
     * in a random manner.
     */
    override def placeSnake(board: Board): Either[GameError, Snake] = ???

    override def placeFood(board: Board, snake: Snake): Food = {
      var rndX = rnd.nextInt(board.width)
      var rndY = rnd.nextInt(board.height)

      while (snake.lookup.contains(Position(rndX, rndY))) {
        rndX = rnd.nextInt(board.width)
        rndY = rnd.nextInt(board.height)
      }

      Food(Position(rndX, rndY))
    }
  }
}
