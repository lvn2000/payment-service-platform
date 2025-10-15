package psp.domain

import java.time.YearMonth

final case class Card(number: String, expiry: YearMonth, cvv: String)
final case class Amount(value: BigDecimal, currency: String)
final case class MerchantId(value: String) extends AnyVal
final case class TransactionId(value: String) extends AnyVal

sealed trait TransactionStatus
object TransactionStatus {
  case object Pending extends TransactionStatus
  case object Approved extends TransactionStatus
  case object Denied extends TransactionStatus
}

final case class Transaction(
    id: TransactionId,
    card: Card,
    amount: Amount,
    merchantId: MerchantId,
    status: TransactionStatus
)
