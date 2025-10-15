package psp.core

import cats.effect.IO
import psp.domain._
import psp.core.AcquirerDecision._
import psp.core.AcquirerType._

import java.util.UUID

final class PaymentServiceImpl(
    repo: TransactionRepo,
    acquirerA: AcquirerClient,
    acquirerB: AcquirerClient
) extends PaymentService {
  override def process(
      card: Card,
      amount: Amount,
      merchantId: MerchantId
  ): IO[(TransactionId, TransactionStatus)] = {
    if (!Validation.luhnValid(card.number))
      IO.raiseError(new IllegalArgumentException("Invalid card number"))
    else {
      val id = TransactionId(UUID.randomUUID().toString)
      val pending =
        Transaction(id, card, amount, merchantId, TransactionStatus.Pending)
      for {
        _ <- repo.create(pending)
        acquirer = Routing.routeByBin(card.number) match {
          case A => acquirerA; case B => acquirerB
        }
        decision <- acquirer.authorize(pending)
        finalStatus = decision match {
          case Approved => TransactionStatus.Approved;
          case Denied   => TransactionStatus.Denied
        }
        _ <- repo.updateStatus(id, finalStatus)
      } yield (id, finalStatus)
    }
  }
}
