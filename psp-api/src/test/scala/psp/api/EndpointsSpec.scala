package psp.api

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import munit.FunSuite
import org.http4s.{Method, Request, Status}
import org.http4s.implicits._
import org.http4s.circe.CirceEntityEncoder._
import psp.storage.InMemoryTransactionRepo
import psp.acquirer.MockAcquirer

class EndpointsSpec extends FunSuite {
  import JsonCodecs._

  test("Endpoints should process valid payment request successfully") {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      acquirerA = new MockAcquirer("A")
      acquirerB = new MockAcquirer("B")
      paymentService = new psp.core.PaymentServiceImpl(
        repo,
        acquirerA,
        acquirerB
      )
      routes = Endpoints.routes(paymentService)

      paymentRequest = PaymentRequest(
        cardNumber = "4242424242424242",
        expiry = "2025-12",
        cvv = "123",
        amount = BigDecimal("100.00"),
        currency = "USD",
        merchantId = "merchant-1"
      )

      request = Request[IO](
        method = Method.POST,
        uri = uri"/payments"
      )
        .withEntity(paymentRequest)

      response <- routes.run(request).value
    } yield response

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    assertEquals(result.get.status, Status.Ok)
  }

  test("Endpoints should return error for invalid expiry format") {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      acquirerA = new MockAcquirer("A")
      acquirerB = new MockAcquirer("B")
      paymentService = new psp.core.PaymentServiceImpl(
        repo,
        acquirerA,
        acquirerB
      )
      routes = Endpoints.routes(paymentService)

      paymentRequest = PaymentRequest(
        cardNumber = "4242424242424242",
        expiry = "invalid-date",
        cvv = "123",
        amount = BigDecimal("100.00"),
        currency = "USD",
        merchantId = "merchant-1"
      )

      request = Request[IO](
        method = Method.POST,
        uri = uri"/payments"
      )
        .withEntity(paymentRequest)

      response <- routes.run(request).value
    } yield response

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    assertEquals(result.get.status, Status.BadRequest)
  }

  test("Endpoints should handle different expiry formats correctly") {
    val validExpiryFormats = List("2025-12", "2026-01", "2030-06")

    validExpiryFormats.foreach { expiry =>
      val testIO = for {
        repo <- InMemoryTransactionRepo.create
        acquirerA = new MockAcquirer("A")
        acquirerB = new MockAcquirer("B")
        paymentService = new psp.core.PaymentServiceImpl(
          repo,
          acquirerA,
          acquirerB
        )
        routes = Endpoints.routes(paymentService)

        paymentRequest = PaymentRequest(
          cardNumber = "4242424242424242",
          expiry = expiry,
          cvv = "123",
          amount = BigDecimal("100.00"),
          currency = "USD",
          merchantId = "merchant-1"
        )

        request = Request[IO](
          method = Method.POST,
          uri = uri"/payments"
        )
          .withEntity(paymentRequest)

        response <- routes.run(request).value
      } yield response

      val result = testIO.unsafeRunSync()
      assert(result.isDefined)
      assertEquals(result.get.status, Status.Ok)
    }
  }

  test("Endpoints should handle payment service errors") {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      acquirerA = new MockAcquirer("A")
      acquirerB = new MockAcquirer("B")
      paymentService = new psp.core.PaymentServiceImpl(
        repo,
        acquirerA,
        acquirerB
      )
      routes = Endpoints.routes(paymentService)

      paymentRequest = PaymentRequest(
        cardNumber = "4242424242424241", // Invalid card
        expiry = "2025-12",
        cvv = "123",
        amount = BigDecimal("100.00"),
        currency = "USD",
        merchantId = "merchant-1"
      )

      request = Request[IO](
        method = Method.POST,
        uri = uri"/payments"
      )
        .withEntity(paymentRequest)

      response <- routes.run(request).value
    } yield response

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    assertEquals(result.get.status, Status.BadRequest)
  }

  test("Endpoints should handle different currencies and amounts") {
    val testCases = List(
      (BigDecimal("50.00"), "EUR"),
      (BigDecimal("1000.00"), "JPY"),
      (BigDecimal("0.01"), "USD"),
      (BigDecimal("999999.99"), "GBP")
    )

    testCases.foreach { case (amount, currency) =>
      val testIO = for {
        repo <- InMemoryTransactionRepo.create
        acquirerA = new MockAcquirer("A")
        acquirerB = new MockAcquirer("B")
        paymentService = new psp.core.PaymentServiceImpl(
          repo,
          acquirerA,
          acquirerB
        )
        routes = Endpoints.routes(paymentService)

        paymentRequest = PaymentRequest(
          cardNumber = "4242424242424242",
          expiry = "2025-12",
          cvv = "123",
          amount = amount,
          currency = currency,
          merchantId = "merchant-1"
        )

        request = Request[IO](
          method = Method.POST,
          uri = uri"/payments"
        )
          .withEntity(paymentRequest)

        response <- routes.run(request).value
      } yield response

      val result = testIO.unsafeRunSync()
      assert(result.isDefined)
      assertEquals(result.get.status, Status.Ok)
    }
  }

  test("Endpoints should handle different merchant IDs") {
    val merchantIds = List("merchant-1", "merchant-2", "merchant-abc", "m-123")

    merchantIds.foreach { merchantId =>
      val testIO = for {
        repo <- InMemoryTransactionRepo.create
        acquirerA = new MockAcquirer("A")
        acquirerB = new MockAcquirer("B")
        paymentService = new psp.core.PaymentServiceImpl(
          repo,
          acquirerA,
          acquirerB
        )
        routes = Endpoints.routes(paymentService)

        paymentRequest = PaymentRequest(
          cardNumber = "4242424242424242",
          expiry = "2025-12",
          cvv = "123",
          amount = BigDecimal("100.00"),
          currency = "USD",
          merchantId = merchantId
        )

        request = Request[IO](
          method = Method.POST,
          uri = uri"/payments"
        )
          .withEntity(paymentRequest)

        response <- routes.run(request).value
      } yield response

      val result = testIO.unsafeRunSync()
      assert(result.isDefined)
      assertEquals(result.get.status, Status.Ok)
    }
  }

  test("Endpoints should handle malformed JSON requests") {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      acquirerA = new MockAcquirer("A")
      acquirerB = new MockAcquirer("B")
      paymentService = new psp.core.PaymentServiceImpl(
        repo,
        acquirerA,
        acquirerB
      )
      routes = Endpoints.routes(paymentService)

      malformedJson = """{"invalid": "json" structure}"""

      request = Request[IO](
        method = Method.POST,
        uri = uri"/payments"
      )
        .withEntity(malformedJson)

      response <- routes.run(request).value
    } yield response

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    assertEquals(result.get.status, Status.BadRequest)
  }

  test("Endpoints should handle empty request body") {
    val testIO = for {
      repo <- InMemoryTransactionRepo.create
      acquirerA = new MockAcquirer("A")
      acquirerB = new MockAcquirer("B")
      paymentService = new psp.core.PaymentServiceImpl(
        repo,
        acquirerA,
        acquirerB
      )
      routes = Endpoints.routes(paymentService)

      request = Request[IO](
        method = Method.POST,
        uri = uri"/payments"
      )

      response <- routes.run(request).value
    } yield response

    val result = testIO.unsafeRunSync()
    assert(result.isDefined)
    assertEquals(result.get.status, Status.BadRequest)
  }
}
