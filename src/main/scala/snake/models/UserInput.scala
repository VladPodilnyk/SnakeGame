package snake.models

sealed trait UserInput extends Product with Serializable
object UserInput {
  case object TurnLeft extends UserInput
  case object TurnRight extends UserInput
  case object DoNothing extends UserInput
}
