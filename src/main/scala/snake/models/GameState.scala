package snake.models

import snake.models.GameState.GameStatus

/**
 * A data class that holds all information about current game state.
 * @param board - defines size of the board
 * @param food - holds food pellets position
 * @param snake - describes a snake on the board
 * @param status - game status.
 */
final case class GameState(board: Board, food: Food, snake: Snake, status: GameStatus)

object GameState {
  final case class GameError(message: String) extends RuntimeException(message)

  sealed trait GameStatus extends Product with Serializable
  object GameStatus {
    case object Active extends GameStatus
    case object Finished extends GameStatus
  }
}