package psp.acquirer

import cats.effect.unsafe.implicits.global
import munit.FunSuite
import psp.core.AcquirerDecision
import psp.domain._

class MockAcquirerSpec extends FunSuite {

  test("MockAcquirer should approve transactions with even last digit") {
    val acquirer = new MockAcquirer("TestAcquirer")
    val card = Card("4242424242424242", java.time.YearMonth.of(2025, 12), "123")
    val amount = Amount(BigDecimal("100.00"), "USD")
    val merchant = MerchantId("merchant-1")
    val transaction = Transaction(
      TransactionId("tx-123"),
      card,
      amount,
      merchant,
      TransactionStatus.Pending
    )

    val result = acquirer.authorize(transaction).unsafeRunSync()
    assertEquals(result, AcquirerDecision.Approved)
  }

  test("MockAcquirer should deny transactions with odd last digit") {
    val acquirer = new MockAcquirer("TestAcquirer")
    val card = Card("4242424242424241", java.time.YearMonth.of(2025, 12), "123")
    val amount = Amount(BigDecimal("100.00"), "USD")
    val merchant = MerchantId("merchant-1")
    val transaction = Transaction(
      TransactionId("tx-124"),
      card,
      amount,
      merchant,
      TransactionStatus.Pending
    )

    val result = acquirer.authorize(transaction).unsafeRunSync()
    assertEquals(result, AcquirerDecision.Denied)
  }

  test("MockAcquirer should handle cards with non-digit characters") {
    val acquirer = new MockAcquirer("TestAcquirer")

    // Card with dashes, last digit is 2 (even)
    val cardWithDashes =
      Card("4242-4242-4242-4242", java.time.YearMonth.of(2025, 12), "123")
    val transaction1 = Transaction(
      TransactionId("tx-125"),
      cardWithDashes,
      Amount(BigDecimal("100.00"), "USD"),
      MerchantId("merchant-1"),
      TransactionStatus.Pending
    )

    val result1 = acquirer.authorize(transaction1).unsafeRunSync()
    assertEquals(result1, AcquirerDecision.Approved)

    // Card with spaces, last digit is 1 (odd)
    val cardWithSpaces =
      Card("4242 4242 4242 4241", java.time.YearMonth.of(2025, 12), "123")
    val transaction2 = Transaction(
      TransactionId("tx-126"),
      cardWithSpaces,
      Amount(BigDecimal("100.00"), "USD"),
      MerchantId("merchant-1"),
      TransactionStatus.Pending
    )

    val result2 = acquirer.authorize(transaction2).unsafeRunSync()
    assertEquals(result2, AcquirerDecision.Denied)
  }

  test("MockAcquirer should handle cards with no digits (default to deny)") {
    val acquirer = new MockAcquirer("TestAcquirer")
    val card = Card("abc", java.time.YearMonth.of(2025, 12), "123")
    val transaction = Transaction(
      TransactionId("tx-127"),
      card,
      Amount(BigDecimal("100.00"), "USD"),
      MerchantId("merchant-1"),
      TransactionStatus.Pending
    )

    val result = acquirer.authorize(transaction).unsafeRunSync()
    assertEquals(result, AcquirerDecision.Denied)
  }

  test("MockAcquirer should handle cards with mixed alphanumeric content") {
    val acquirer = new MockAcquirer("TestAcquirer")

    // Last digit is 0 (even)
    val card1 = Card("abc123def0", java.time.YearMonth.of(2025, 12), "123")
    val transaction1 = Transaction(
      TransactionId("tx-128"),
      card1,
      Amount(BigDecimal("100.00"), "USD"),
      MerchantId("merchant-1"),
      TransactionStatus.Pending
    )

    val result1 = acquirer.authorize(transaction1).unsafeRunSync()
    assertEquals(result1, AcquirerDecision.Approved)

    // Last digit is 7 (odd)
    val card2 = Card("xyz789abc7", java.time.YearMonth.of(2025, 12), "123")
    val transaction2 = Transaction(
      TransactionId("tx-129"),
      card2,
      Amount(BigDecimal("100.00"), "USD"),
      MerchantId("merchant-1"),
      TransactionStatus.Pending
    )

    val result2 = acquirer.authorize(transaction2).unsafeRunSync()
    assertEquals(result2, AcquirerDecision.Denied)
  }

  test("MockAcquirer should store and expose the acquirer name") {
    val acquirerA = new MockAcquirer("AcquirerA")
    val acquirerB = new MockAcquirer("AcquirerB")

    assertEquals(acquirerA.name, "AcquirerA")
    assertEquals(acquirerB.name, "AcquirerB")
  }

  test("MockAcquirer should handle single digit cards") {
    val acquirer = new MockAcquirer("TestAcquirer")

    // Single digit 2 (even)
    val card1 = Card("2", java.time.YearMonth.of(2025, 12), "123")
    val transaction1 = Transaction(
      TransactionId("tx-130"),
      card1,
      Amount(BigDecimal("100.00"), "USD"),
      MerchantId("merchant-1"),
      TransactionStatus.Pending
    )

    val result1 = acquirer.authorize(transaction1).unsafeRunSync()
    assertEquals(result1, AcquirerDecision.Approved)

    // Single digit 3 (odd)
    val card2 = Card("3", java.time.YearMonth.of(2025, 12), "123")
    val transaction2 = Transaction(
      TransactionId("tx-131"),
      card2,
      Amount(BigDecimal("100.00"), "USD"),
      MerchantId("merchant-1"),
      TransactionStatus.Pending
    )

    val result2 = acquirer.authorize(transaction2).unsafeRunSync()
    assertEquals(result2, AcquirerDecision.Denied)
  }

  test("MockAcquirer should handle empty card numbers (default to deny)") {
    val acquirer = new MockAcquirer("TestAcquirer")
    val card = Card("", java.time.YearMonth.of(2025, 12), "123")
    val transaction = Transaction(
      TransactionId("tx-132"),
      card,
      Amount(BigDecimal("100.00"), "USD"),
      MerchantId("merchant-1"),
      TransactionStatus.Pending
    )

    val result = acquirer.authorize(transaction).unsafeRunSync()
    assertEquals(result, AcquirerDecision.Denied)
  }
}
