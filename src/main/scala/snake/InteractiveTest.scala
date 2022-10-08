package snake

import snake.SnakeGame.GameConfig
import snake.domain.ObjectPlacer.TestPlacer
import snake.domain.Renderer
import snake.models.UserInput.{DoNothing, TurnLeft, TurnRight}
import scala.concurrent.duration._

/**
  * Your task is to model a Snake game, logic wise only.
  * There's a summary of what Snake is below.
  *
  * No need for a fancy UI or threading, if you need to display something do it in Std.out!
  *
  * Steps:
  *   1) Model the board (that wraps around), the snake and food pellets.
  *   2) Place a size 2 snake in the middle of the board and a food somewhere else than the snake.
  *   3) `Display` board state in the console (ASCII "art")
  *   4) Define a method that performs a 'step' of the game
  *     - takes a possible user input as parameter
  *     - is called from outside (Launcher?) at a regular time interval
  *     - update the board's state
  *   5) Code an input sequence that eats the first food pellet.
  *   6) Randomise food pellet placement
  */
object InteractiveTest extends App {
  // this will be done in a game launcher.
  val gameConfig  = GameConfig(minBoardSideLength = 4, refreshPeriod = 1.second)
  val game        = new SnakeGame.ClassicSnakeGame(TestPlacer, gameConfig)
  val gameSession = game.create(10, 10)

  gameSession match {
    case Left(error) =>
      Renderer.showFailure(error)
    case Right(initState) =>
      val inputs = Seq(TurnRight, TurnLeft, DoNothing)
      Renderer.draw(initState)
      inputs.foldLeft(initState) {
        case (state, input) =>
          val newState = game.nextFrame(state, input)
          Renderer.draw(newState)
          newState
      }
  }
}

/**
  * Summary: Snake game
  *
  * You control a Snake that moves around in a grid, the snake spans over a 2 cells at the beginning.
  * You can only turn the snake left or right and it moves one cell every "game turn".
  * There is always one food pellet on the grid.
  * Goal of the game is to eat said pellets by moving the snake to its position.
  * When the snake eats a pellet it grows one cell longer.
  * If the snake collides with itself, the game is lost.
  * If the snake reaches one of the grid's borders, it wraps around (i.e. going out on the left side means you come back in on the right side).
  */

/* Example ASCII output:
 . . . . . .
 . @ . . . .
 . . . o . .
 . . . x . .
 . . . x . .
 . . . . . .

 @ = food
 o = snake head
 x = snake body
 */
