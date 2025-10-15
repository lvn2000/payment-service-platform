package psp.app

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.implicits._
import com.comcast.ip4s._

import psp.storage.InMemoryTransactionRepo
import psp.core._
import psp.acquirer.MockAcquirer
import psp.api.Endpoints

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      repo <- InMemoryTransactionRepo.create
      acqA = new MockAcquirer("A")
      acqB = new MockAcquirer("B")
      service = new PaymentServiceImpl(repo, acqA, acqB)
      httpApp = Router(
        "/" -> Endpoints.routes(service),
        "/" -> Endpoints.docsRoutes()
      ).orNotFound
      exit <- EmberServerBuilder
        .default[IO]
        .withHost(ip"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
    } yield exit
  }
}
