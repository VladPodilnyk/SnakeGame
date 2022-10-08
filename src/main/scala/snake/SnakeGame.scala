package snake

import snake.domain.{ObjectPlacer, utils}
import snake.models.GameState.GameError
import snake.models.GameState.GameStatus._
import snake.models.Position.Step
import snake.models.Snake._
import snake.models.Snake.Direction._
import snake.models.Snake.MoveResult._
import snake.models.UserInput.{DoNothing, TurnLeft, TurnRight}
import snake.models._

import scala.concurrent.duration.FiniteDuration

/**
  * So, my idea is to have a SnakeGame interface that allows users to create a game
  * and update game state based on user's inputs. GameState could be based on mutable
  * data structures for sake of performance gains. Every time we call `nextFrame`
  * we need to pass an old state and a user's input. As a result we will receive
  * an updated state. Receiving updated state in result allows us to test game loop
  * with ease.
  *
  * Could be defined as trait, so latter we will easily create different game modes.
  */
trait SnakeGame {
  /* Creates new game on a board with given `width` and `height` */
  def create(width: Int, height: Int): Either[GameError, GameState]

  /* Updates game state based on user's input */
  def nextFrame(gameState: GameState, userInput: UserInput): GameState
}

object SnakeGame {
  final case class GameConfig(minBoardSideLength: Int, refreshPeriod: FiniteDuration)

  final class ClassicSnakeGame(objectPlacer: ObjectPlacer, gameConfig: GameConfig) extends SnakeGame {

    override def create(width: Int, height: Int): Either[GameError, GameState] = {
      if (width < gameConfig.minBoardSideLength || height < gameConfig.minBoardSideLength) {
        Left(GameError(s"Board dimension MUST be greater than ${gameConfig.minBoardSideLength}"))
      } else {
        val board = Board(width, height)
        objectPlacer.placeSnake(board).map {
          snake =>
            val food = objectPlacer.placeFood(board, snake)
            GameState(board, food, snake, Active)
        }
      }
    }

    override def nextFrame(gameState: GameState, userInput: UserInput): GameState = {
      val snakeHead               = gameState.snake.body.head
      val (step, newDirection)    = advancePosition(userInput, gameState.snake.direction)
      val adjustedPossibleHeadPos = utils.ensureCorrectPlacement(snakeHead.update(step), gameState.board)

      if (adjustedPossibleHeadPos == gameState.food.position) snakeFoodCollisionHandler(gameState, newDirection)
      else snakeMoveHandler(gameState, adjustedPossibleHeadPos, newDirection)
    }

    private[this] def snakeFoodCollisionHandler(gameState: GameState, newDirection: Direction): GameState = {
      val newSnakePosition = gameState.snake.eat(gameState.food, newDirection)
      val newFood          = objectPlacer.placeFood(gameState.board, gameState.snake)
      gameState.copy(food = newFood, snake = newSnakePosition)
    }

    private[this] def snakeMoveHandler(gameState: GameState, newHead: Position, newDirection: Direction): GameState = {
      gameState.snake.move(newHead, newDirection) match {
        case Overlap(lastSnakePosition) => gameState.copy(snake = lastSnakePosition, status = Finished)
        case Moved(newSnake)            => gameState.copy(snake = newSnake)
      }
    }

    private[this] def advancePosition(userInput: UserInput, direction: Direction): (Step, Direction) = {
      (userInput, direction) match {
        case (TurnLeft, UP)  => Step(-1, 0) -> LEFT
        case (TurnRight, UP) => Step(1, 0)  -> RIGHT
        case (DoNothing, UP) => Step(0, -1) -> UP

        case (TurnLeft, DOWN)  => Step(-1, 0) -> LEFT
        case (TurnRight, DOWN) => Step(1, 0)  -> RIGHT
        case (DoNothing, DOWN) => Step(0, 1)  -> DOWN

        case (TurnLeft, LEFT)  => Step(0, 1)  -> DOWN
        case (TurnRight, LEFT) => Step(0, -1) -> UP
        case (DoNothing, LEFT) => Step(-1, 0) -> LEFT

        case (TurnLeft, RIGHT)  => Step(0, -1) -> UP
        case (TurnRight, RIGHT) => Step(0, 1)  -> DOWN
        case (DoNothing, RIGHT) => Step(1, 0)  -> RIGHT
      }
    }

  }
}
