package snake.models

import snake.models.Position.Step

final case class Position(x: Int, y: Int) {
  def update(step: Step): Position = Position(x + step.x, y + step.y)
}

object Position {
  final case class Step(x: Int, y: Int)
}
