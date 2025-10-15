package psp.storage

import cats.effect.{IO, Ref}
import psp.core.TransactionRepo
import psp.domain._

final class InMemoryTransactionRepo private (
    state: Ref[IO, Map[TransactionId, Transaction]]
) extends TransactionRepo {
  override def create(tx: Transaction): IO[Unit] =
    state.update(_ + (tx.id -> tx))

  override def updateStatus(
      id: TransactionId,
      status: TransactionStatus
  ): IO[Unit] =
    state.update { m =>
      m.get(id).fold(m)(t => m.updated(id, t.copy(status = status)))
    }

  override def get(id: TransactionId): IO[Option[Transaction]] =
    state.get.map(_.get(id))
}

object InMemoryTransactionRepo {
  def create: IO[InMemoryTransactionRepo] = Ref
    .of[IO, Map[TransactionId, Transaction]](Map.empty)
    .map(new InMemoryTransactionRepo(_))
}
