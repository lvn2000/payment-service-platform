package psp.api

import cats.effect.IO
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import psp.core.PaymentService
import psp.domain._
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import org.http4s.HttpRoutes
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.generic.auto._

final case class PaymentRequest(
    cardNumber: String,
    expiry: String,
    cvv: String,
    amount: BigDecimal,
    currency: String,
    merchantId: String
)
final case class PaymentResponse(
    transactionId: String,
    status: String,
    message: String
)
final case class ErrorResponse(message: String)

object JsonCodecs {
  implicit val prDec: Decoder[PaymentRequest] = deriveDecoder
  implicit val prEnc: Encoder[PaymentRequest] = deriveEncoder
  implicit val respEnc: Encoder[PaymentResponse] = deriveEncoder
  implicit val respDec: Decoder[PaymentResponse] = deriveDecoder
  implicit val errEnc: Encoder[ErrorResponse] = deriveEncoder
  implicit val errDec: Decoder[ErrorResponse] = deriveDecoder
}

object Endpoints {
  import JsonCodecs._

  val processPayment
      : PublicEndpoint[PaymentRequest, ErrorResponse, PaymentResponse, Any] =
    endpoint.post
      .in("payments")
      .in(jsonBody[PaymentRequest])
      .out(jsonBody[PaymentResponse])
      .errorOut(jsonBody[ErrorResponse])

  def routes(paymentService: PaymentService): HttpRoutes[IO] = {
    import java.time.YearMonth

    val logic: PaymentRequest => IO[Either[ErrorResponse, PaymentResponse]] =
      req => {
        def parseYearMonth(s: String): Either[ErrorResponse, YearMonth] = {
          import scala.util.Try
          Try(YearMonth.parse(s)).toEither.left.map(e =>
            ErrorResponse(s"Invalid expiry format: ${e.getMessage}")
          )
        }

        val prepared = for {
          ym <- parseYearMonth(req.expiry)
          card = Card(req.cardNumber, ym, req.cvv)
          amount = Amount(req.amount, req.currency)
          merchant = MerchantId(req.merchantId)
        } yield (card, amount, merchant)

        prepared match {
          case Left(err) => IO.pure(Left(err))
          case Right((card, amount, merchant)) =>
            paymentService.process(card, amount, merchant).attempt.map {
              case Right((id, status)) =>
                Right(PaymentResponse(id.value, status.toString, "Processed"))
              case Left(e) => Left(ErrorResponse(e.getMessage))
            }
        }
      }

    Http4sServerInterpreter[IO]().toRoutes(processPayment.serverLogic(logic))
  }

  def docsRoutes(): HttpRoutes[IO] = {
    val endpoints = List(processPayment)
    val docs =
      SwaggerInterpreter().fromEndpoints[IO](endpoints, "PSP API", "0.1.0")
    Http4sServerInterpreter[IO]().toRoutes(docs)
  }
}
