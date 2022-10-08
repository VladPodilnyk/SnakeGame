package snake.domain

import snake.models.GameState.GameError
import snake.models.GameState.GameStatus.Finished
import snake.models.{GameState, Position}

import scala.collection.mutable.ArrayBuffer

/**
  * Simple renderer (is not meant to be performant).
  * For a real console game, I would go with ANSI X3.64 control sequences
  * or in case I need a nice UI, I would go with frame buffers. The later approach
  * should be pretty fast and eventually is possible to come up with an algorithm
  * to update only a portion of a screen (don't know if someone has ever do this for a snake game tho :) )
  */
object Renderer {
  def draw(gameState: GameState): Unit = {
    var res = ""
    var y   = 0

    while (y < gameState.board.height) {
      val row = new ArrayBuffer[Char]()
      var x   = 0

      while (x < gameState.board.width) {
        val pos    = Position(x, y)
        val symbol = positionToSymbol(pos, gameState)
        row.addOne(symbol)
        x += 1
      }

      res = res + row.mkString(" ") + "\n"
      y += 1
    }

    println(res)
    if (gameState.status == Finished) {
      finishMessage(gameState.snake.body.size)
    }
  }

  def showFailure(error: GameError): Unit = {
    println(s"Couldn't create a game :(\nReason: ${error.message}")
  }

  private def finishMessage(score: Int): Unit = {
    println(s"Game finished with score: $score")
  }

  private def positionToSymbol(position: Position, gameState: GameState): Char = {
    if (position == gameState.food.position) '@'
    else if (gameState.snake.lookup.contains(position) && gameState.snake.body.head == position) 'o'
    else if (gameState.snake.lookup.contains(position)) 'x'
    else '.'
  }
}
