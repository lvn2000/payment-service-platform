package psp.acquirer

import cats.effect.IO
import psp.core.{AcquirerClient, AcquirerDecision}
import psp.core.AcquirerDecision._
import psp.domain._

final class MockAcquirer(val name: String) extends AcquirerClient {
  override def authorize(tx: Transaction): IO[AcquirerDecision] = {
    val lastDigit =
      tx.card.number.filter(_.isDigit).lastOption.map(_.asDigit).getOrElse(1)
    val decision = if (lastDigit % 2 == 0) Approved else Denied
    IO.println(s"Acquirer[$name]: authorizing tx ${tx.id.value} -> $decision")
      .as(decision)
  }
}
