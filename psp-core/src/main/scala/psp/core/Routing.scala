package psp.core

sealed trait AcquirerType
object AcquirerType {
  case object A extends AcquirerType; case object B extends AcquirerType
}

object Routing {
  def routeByBin(pan: String): AcquirerType = {
    val bin = pan.filter(_.isDigit).take(6)
    val sum = bin.map(_.asDigit).sum
    if (sum % 2 == 0) AcquirerType.A else AcquirerType.B
  }
}
