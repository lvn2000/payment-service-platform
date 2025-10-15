package psp.core

import cats.effect.IO
import psp.domain._

trait TransactionRepo {
  def create(tx: Transaction): IO[Unit]
  def updateStatus(id: TransactionId, status: TransactionStatus): IO[Unit]
  def get(id: TransactionId): IO[Option[Transaction]]
}

sealed trait AcquirerDecision
object AcquirerDecision {
  case object Approved extends AcquirerDecision;
  case object Denied extends AcquirerDecision
}

trait AcquirerClient {
  def authorize(tx: Transaction): IO[AcquirerDecision]
}

trait PaymentService {
  def process(
      card: Card,
      amount: Amount,
      merchantId: MerchantId
  ): IO[(TransactionId, TransactionStatus)]
}
