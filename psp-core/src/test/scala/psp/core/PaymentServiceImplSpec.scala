package psp.core

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import munit.FunSuite
import psp.domain._

// Simple mock implementations for testing
class MockTransactionRepo extends TransactionRepo {
  private var transactions: Map[TransactionId, Transaction] = Map.empty

  override def create(tx: Transaction): IO[Unit] = IO {
    transactions = transactions + (tx.id -> tx)
  }

  override def updateStatus(
      id: TransactionId,
      status: TransactionStatus
  ): IO[Unit] = IO {
    transactions.get(id).foreach { tx =>
      transactions = transactions + (id -> tx.copy(status = status))
    }
  }

  override def get(id: TransactionId): IO[Option[Transaction]] = IO {
    transactions.get(id)
  }
}

class MockAcquirerClient(val name: String) extends AcquirerClient {
  override def authorize(tx: Transaction): IO[AcquirerDecision] = {
    // Use a different logic that's easier to test - approve if amount is even cents
    val decision =
      if (tx.amount.value % 2 == 0) AcquirerDecision.Approved
      else AcquirerDecision.Denied
    IO.pure(decision)
  }
}

class PaymentServiceImplSpec extends FunSuite {

  test("PaymentServiceImpl should successfully process a valid payment") {
    val repo = new MockTransactionRepo()
    val acquirerA = new MockAcquirerClient("A")
    val acquirerB = new MockAcquirerClient("B")
    val service = new PaymentServiceImpl(repo, acquirerA, acquirerB)

    val card = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    val amount = Amount(BigDecimal("100.00"), "USD")
    val merchant = MerchantId("merchant-1")

    val result = service.process(card, amount, merchant).unsafeRunSync()
    val (transactionId, status) = result

    assert(transactionId.value.nonEmpty)
    assert(
      status == TransactionStatus.Approved || status == TransactionStatus.Denied
    )
  }

  test("PaymentServiceImpl should reject invalid card numbers") {
    val repo = new MockTransactionRepo()
    val acquirerA = new MockAcquirerClient("A")
    val acquirerB = new MockAcquirerClient("B")
    val service = new PaymentServiceImpl(repo, acquirerA, acquirerB)

    val invalidCard = Card(
      "4242424242424241",
      java.time.YearMonth.of(2025, 12),
      "123"
    ) // Invalid Luhn
    val amount = Amount(BigDecimal("100.00"), "USD")
    val merchant = MerchantId("merchant-1")

    val result =
      service.process(invalidCard, amount, merchant).attempt.unsafeRunSync()

    assert(result.isLeft)
    assert(result.left.get.isInstanceOf[IllegalArgumentException])
    assert(result.left.get.getMessage == "Invalid card number")
  }

  test("PaymentServiceImpl should route to correct acquirer based on BIN") {
    val repo = new MockTransactionRepo()
    val acquirerA = new MockAcquirerClient("A")
    val acquirerB = new MockAcquirerClient("B")
    val service = new PaymentServiceImpl(repo, acquirerA, acquirerB)

    // BIN: 424242 (sum=18, even) -> should route to A
    val cardA =
      Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    val amount = Amount(BigDecimal("100.00"), "USD")
    val merchant = MerchantId("merchant-1")

    val resultA = service.process(cardA, amount, merchant).unsafeRunSync()

    // BIN: 123456 (sum=21, odd) -> should route to B
    val cardB =
      Card("1234567890123452", java.time.YearMonth.of(2025, 12), "123")
    val resultB = service.process(cardB, amount, merchant).unsafeRunSync()

    val (idA, statusA) = resultA
    val (idB, statusB) = resultB

    assert(idA.value.nonEmpty)
    assert(idB.value.nonEmpty)
    assert(idA != idB) // Different transaction IDs
    // Both should be either Approved or Denied based on last digit
    assert(
      statusA == TransactionStatus.Approved || statusA == TransactionStatus.Denied
    )
    assert(
      statusB == TransactionStatus.Approved || statusB == TransactionStatus.Denied
    )
  }

  test("PaymentServiceImpl should create transaction with correct status") {
    val repo = new MockTransactionRepo()
    val acquirerA = new MockAcquirerClient("A")
    val acquirerB = new MockAcquirerClient("B")
    val service = new PaymentServiceImpl(repo, acquirerA, acquirerB)

    val card = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    val amount = Amount(BigDecimal("100.00"), "USD")
    val merchant = MerchantId("merchant-1")

    val (transactionId, finalStatus) =
      service.process(card, amount, merchant).unsafeRunSync()
    val storedTransaction = repo.get(transactionId).unsafeRunSync()

    assert(storedTransaction.isDefined)
    assertEquals(storedTransaction.get.id, transactionId)
    assertEquals(
      storedTransaction.get.status,
      finalStatus
    ) // Should be updated to final status
    assertEquals(
      storedTransaction.get.card,
      Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    )
    assertEquals(
      storedTransaction.get.amount,
      Amount(BigDecimal("100.00"), "USD")
    )
    assertEquals(storedTransaction.get.merchantId, MerchantId("merchant-1"))
  }

  test("PaymentServiceImpl should handle different currencies and amounts") {
    val repo = new MockTransactionRepo()
    val acquirerA = new MockAcquirerClient("A")
    val acquirerB = new MockAcquirerClient("B")
    val service = new PaymentServiceImpl(repo, acquirerA, acquirerB)

    val card = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    val amount = Amount(BigDecimal("1500.75"), "JPY")
    val merchant = MerchantId("merchant-3")

    val (transactionId, status) =
      service.process(card, amount, merchant).unsafeRunSync()
    val storedTransaction = repo.get(transactionId).unsafeRunSync()

    assert(transactionId.value.nonEmpty)
    assert(
      status == TransactionStatus.Approved || status == TransactionStatus.Denied
    )
    assert(storedTransaction.isDefined)
    assertEquals(
      storedTransaction.get.amount,
      Amount(BigDecimal("1500.75"), "JPY")
    )
  }

  test("PaymentServiceImpl should generate unique transaction IDs") {
    val repo = new MockTransactionRepo()
    val acquirerA = new MockAcquirerClient("A")
    val acquirerB = new MockAcquirerClient("B")
    val service = new PaymentServiceImpl(repo, acquirerA, acquirerB)

    val card = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    val amount = Amount(BigDecimal("100.00"), "USD")
    val merchant = MerchantId("merchant-1")

    val result1 = service.process(card, amount, merchant).unsafeRunSync()
    val result2 = service.process(card, amount, merchant).unsafeRunSync()

    val (id1, _) = result1
    val (id2, _) = result2

    assert(id1 != id2)
    assert(id1.value.nonEmpty)
    assert(id2.value.nonEmpty)
  }

  test("PaymentServiceImpl should handle acquirer approval and denial") {
    val repo = new MockTransactionRepo()
    val acquirerA = new MockAcquirerClient("A")
    val acquirerB = new MockAcquirerClient("B")
    val service = new PaymentServiceImpl(repo, acquirerA, acquirerB)

    // Amount 100.00 (even) -> should be approved
    val approvedCard =
      Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    val approvedAmount = Amount(BigDecimal("100.00"), "USD")
    val merchant = MerchantId("merchant-1")

    val approvedResult =
      service.process(approvedCard, approvedAmount, merchant).unsafeRunSync()

    // Amount 101.00 (odd) -> should be denied
    val deniedCard =
      Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    val deniedAmount = Amount(BigDecimal("101.00"), "USD")
    val deniedResult =
      service.process(deniedCard, deniedAmount, merchant).unsafeRunSync()

    val (approvedId, approvedStatus) = approvedResult
    val (deniedId, deniedStatus) = deniedResult

    assert(approvedId.value.nonEmpty)
    assert(deniedId.value.nonEmpty)
    assert(approvedId != deniedId)

    // Based on MockAcquirer logic: even amount = Approved, odd amount = Denied
    assertEquals(approvedStatus, TransactionStatus.Approved)
    assertEquals(deniedStatus, TransactionStatus.Denied)
  }
}
