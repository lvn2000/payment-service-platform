package psp.storage

import cats.effect.unsafe.implicits.global
import munit.FunSuite
import psp.domain._

class InMemoryTransactionRepoSpec extends FunSuite {

  test("InMemoryTransactionRepo should create and retrieve transactions") {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      card = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
      amount = Amount(BigDecimal("100.00"), "USD")
      merchant = MerchantId("merchant-1")
      transaction = Transaction(
        TransactionId("tx-123"),
        card,
        amount,
        merchant,
        TransactionStatus.Pending
      )
      _ <- repo.create(transaction)
      retrieved <- repo.get(TransactionId("tx-123"))
    } yield retrieved

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    assertEquals(result.get.id, TransactionId("tx-123"))
    assertEquals(result.get.status, TransactionStatus.Pending)
    assertEquals(result.get.card.number, "4242424242424242")
    assertEquals(result.get.amount.value, BigDecimal("100.00"))
    assertEquals(result.get.merchantId, MerchantId("merchant-1"))
  }

  test(
    "InMemoryTransactionRepo should return None for non-existent transactions"
  ) {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      result <- repo.get(TransactionId("non-existent"))
    } yield result

    val result = testIO.unsafeRunSync()
    assert(result.isEmpty)
  }

  test("InMemoryTransactionRepo should update transaction status") {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      card = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
      amount = Amount(BigDecimal("100.00"), "USD")
      merchant = MerchantId("merchant-1")
      transaction = Transaction(
        TransactionId("tx-124"),
        card,
        amount,
        merchant,
        TransactionStatus.Pending
      )
      _ <- repo.create(transaction)
      _ <- repo.updateStatus(
        TransactionId("tx-124"),
        TransactionStatus.Approved
      )
      updated <- repo.get(TransactionId("tx-124"))
    } yield updated

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    assertEquals(result.get.status, TransactionStatus.Approved)
    assertEquals(result.get.id, TransactionId("tx-124"))
  }

  test("InMemoryTransactionRepo should update status from Pending to Denied") {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      card = Card("4242424242424241", java.time.YearMonth.of(2025, 12), "123")
      amount = Amount(BigDecimal("50.00"), "EUR")
      merchant = MerchantId("merchant-2")
      transaction = Transaction(
        TransactionId("tx-125"),
        card,
        amount,
        merchant,
        TransactionStatus.Pending
      )
      _ <- repo.create(transaction)
      _ <- repo.updateStatus(TransactionId("tx-125"), TransactionStatus.Denied)
      updated <- repo.get(TransactionId("tx-125"))
    } yield updated

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    assertEquals(result.get.status, TransactionStatus.Denied)
    assertEquals(result.get.amount.currency, "EUR")
  }

  test(
    "InMemoryTransactionRepo should handle multiple transactions independently"
  ) {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      card1 = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
      card2 = Card("5555555555554444", java.time.YearMonth.of(2026, 6), "456")

      tx1 = Transaction(
        TransactionId("tx-126"),
        card1,
        Amount(BigDecimal("100.00"), "USD"),
        MerchantId("merchant-1"),
        TransactionStatus.Pending
      )

      tx2 = Transaction(
        TransactionId("tx-127"),
        card2,
        Amount(BigDecimal("200.00"), "EUR"),
        MerchantId("merchant-2"),
        TransactionStatus.Pending
      )

      _ <- repo.create(tx1)
      _ <- repo.create(tx2)
      _ <- repo.updateStatus(
        TransactionId("tx-126"),
        TransactionStatus.Approved
      )
      _ <- repo.updateStatus(TransactionId("tx-127"), TransactionStatus.Denied)

      result1 <- repo.get(TransactionId("tx-126"))
      result2 <- repo.get(TransactionId("tx-127"))
    } yield (result1, result2)

    val (result1, result2) = testIO.unsafeRunSync()
    assert(result1.isDefined)
    assertEquals(result1.get.status, TransactionStatus.Approved)
    assertEquals(result1.get.card.number, "4242424242424242")

    assert(result2.isDefined)
    assertEquals(result2.get.status, TransactionStatus.Denied)
    assertEquals(result2.get.card.number, "5555555555554444")
  }

  test(
    "InMemoryTransactionRepo should not affect other transactions when updating status"
  ) {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      card1 = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
      card2 = Card("5555555555554444", java.time.YearMonth.of(2026, 6), "456")

      tx1 = Transaction(
        TransactionId("tx-128"),
        card1,
        Amount(BigDecimal("100.00"), "USD"),
        MerchantId("merchant-1"),
        TransactionStatus.Pending
      )

      tx2 = Transaction(
        TransactionId("tx-129"),
        card2,
        Amount(BigDecimal("200.00"), "EUR"),
        MerchantId("merchant-2"),
        TransactionStatus.Pending
      )

      _ <- repo.create(tx1)
      _ <- repo.create(tx2)
      _ <- repo.updateStatus(
        TransactionId("tx-128"),
        TransactionStatus.Approved
      )

      unchanged <- repo.get(TransactionId("tx-129"))
    } yield unchanged

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    assertEquals(
      result.get.status,
      TransactionStatus.Pending
    ) // Should remain unchanged
  }

  test(
    "InMemoryTransactionRepo should handle updateStatus for non-existent transaction gracefully"
  ) {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      _ <- repo.updateStatus(
        TransactionId("non-existent"),
        TransactionStatus.Approved
      )
      result <- repo.get(TransactionId("non-existent"))
    } yield result

    val result = testIO.unsafeRunSync()
    assert(result.isEmpty)
  }

  test(
    "InMemoryTransactionRepo should preserve all transaction fields when updating status"
  ) {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      card = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
      amount = Amount(BigDecimal("150.50"), "GBP")
      merchant = MerchantId("merchant-3")
      originalTx = Transaction(
        TransactionId("tx-130"),
        card,
        amount,
        merchant,
        TransactionStatus.Pending
      )

      _ <- repo.create(originalTx)
      _ <- repo.updateStatus(
        TransactionId("tx-130"),
        TransactionStatus.Approved
      )
      updated <- repo.get(TransactionId("tx-130"))
    } yield updated

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    val tx = result.get
    assertEquals(tx.id, TransactionId("tx-130"))
    assertEquals(
      tx.card,
      Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    )
    assertEquals(tx.amount, Amount(BigDecimal("150.50"), "GBP"))
    assertEquals(tx.merchantId, MerchantId("merchant-3"))
    assertEquals(tx.status, TransactionStatus.Approved)
  }
}
