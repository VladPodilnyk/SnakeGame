package snake

import org.scalatest.flatspec.AnyFlatSpec
import snake.SnakeGame.GameConfig
import snake.domain.ObjectPlacer.TestPlacer
import snake.models.UserInput.{DoNothing, TurnLeft, TurnRight}
import snake.models.{GameState, Position, UserInput}

import scala.concurrent.duration._

/**
  * Few simple tests.
  */
class SnakeMovementTest extends AnyFlatSpec {
  "Snake" should "grow as it eats food" in {
    setup(boardSize = 10, minBoardDimension = 4) match {
      case Left(err) => fail(err.message)
      case Right((game, initState)) =>
        val inputs = Seq(TurnRight, TurnLeft, DoNothing)
        val lastState = inputs.foldLeft(initState) {
          case (state, input) =>
            game.nextFrame(state, input)
        }

        assert(initState.snake.body.size == 2)
        assert(lastState.snake.body.size == 3)
    }
  }

  "Snake" should "wrap around if it reaches a grid's board" in {
    setup(boardSize = 4, minBoardDimension = 4) match {
      case Left(err) => fail(err.message)
      case Right((game, initState)) =>
        var inputs: Seq[UserInput] = Seq(DoNothing, DoNothing, DoNothing)
        var lastState              = move(game, initState, inputs)
        assert(lastState.snake.body.head == Position(2, 3))

        inputs    = Seq(TurnLeft, DoNothing, DoNothing)
        lastState = move(game, lastState, inputs)
        assert(lastState.snake.body.head == Position(3, 3))

        inputs    = Seq(TurnLeft)
        lastState = move(game, lastState, inputs)
        assert(lastState.snake.body.head == Position(3, 0))

        inputs    = Seq(TurnRight)
        lastState = move(game, lastState, inputs)
        assert(lastState.snake.body.head == Position(0, 0))
    }
  }

  def move(game: SnakeGame, state: GameState, inputs: Seq[UserInput]): GameState = {
    inputs.foldLeft(state) {
      case (currentState, input) => game.nextFrame(currentState, input)
    }
  }

  def setup(boardSize: Int, minBoardDimension: Int) = {
    val gameConfig = GameConfig(minBoardDimension, refreshPeriod = 1.second)
    val game       = new SnakeGame.ClassicSnakeGame(TestPlacer, gameConfig)
    game.create(boardSize, boardSize).map(initState => game -> initState)
  }
}
