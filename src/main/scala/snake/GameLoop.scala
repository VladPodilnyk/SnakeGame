package snake

import snake.GameLoop.InputsBuffer
import snake.SnakeGame.GameConfig
import snake.domain.Renderer
import snake.models.GameState.GameError
import snake.models.GameState.GameStatus.Finished
import snake.models.{GameState, UserInput}

import scala.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import scala.annotation.unused
import scala.util.Try

/**
  * Possible implementation of a game loop. Just a sketch
  *
  * gameConfig and objectPlacer are configured with `resource.conf`, that
  * itself must be shipped with game bundle.
  */
final class GameLoop(game: SnakeGame, config: GameConfig, inBuffer: InputsBuffer) {
  private[this] val isExit = new AtomicBoolean(false)

  def start: Either[GameError, Unit] = {
    for {
      boardSize      <- readUsersInput()
      (width, height) = boardSize
      initState      <- game.create(width, height)
      _               = listenUserInput(inBuffer)
      _               = loop(initState)
    } yield ()
  }

  private[this] def loop(state: GameState): Unit = {
    var s = state
    // print initial state
    Renderer.draw(s)

    while (!isExit.get()) {
      val in = inBuffer.dequeue()
      s = game.nextFrame(s, in)
      if (s.status == Finished) {
        isExit.set(true)
      }
      Renderer.draw(s)
      Thread.sleep(config.refreshPeriod.toMillis)
    }
  }

  private[this] def listenUserInput(@unused buffer: InputsBuffer): Future[Unit] = ???

  private[this] def readUsersInput(): Either[GameError, (Int, Int)] = {
    Try {
      val width  = scala.io.StdIn.readLine().toInt
      val height = scala.io.StdIn.readLine().toInt
      width -> height
    }.toEither.left.map(err => GameError(s"Failed to read user's input, due to ${err.getMessage}"))
  }
}

object GameLoop {
  /**
    * Should be a thread-safe queue.
    * We will push values here from a separate thread that is listening for a user's input
    */
  type InputsBuffer = scala.collection.mutable.Queue[UserInput]
}
