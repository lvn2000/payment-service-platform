error id: file://<WORKSPACE>/psp-app/src/main/scala/psp/app/Main.scala:psp/api/Endpoints.docsRoutes().
file://<WORKSPACE>/psp-app/src/main/scala/psp/app/Main.scala
empty definition using pc, found symbol in pc: psp/api/Endpoints.docsRoutes().
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -org/http4s/implicits/Endpoints.docsRoutes.
	 -org/http4s/implicits/Endpoints.docsRoutes#
	 -org/http4s/implicits/Endpoints.docsRoutes().
	 -com/comcast/ip4s/Endpoints.docsRoutes.
	 -com/comcast/ip4s/Endpoints.docsRoutes#
	 -com/comcast/ip4s/Endpoints.docsRoutes().
	 -psp/core/Endpoints.docsRoutes.
	 -psp/core/Endpoints.docsRoutes#
	 -psp/core/Endpoints.docsRoutes().
	 -psp/api/Endpoints.docsRoutes.
	 -psp/api/Endpoints.docsRoutes#
	 -psp/api/Endpoints.docsRoutes().
	 -Endpoints.docsRoutes.
	 -Endpoints.docsRoutes#
	 -Endpoints.docsRoutes().
	 -scala/Predef.Endpoints.docsRoutes.
	 -scala/Predef.Endpoints.docsRoutes#
	 -scala/Predef.Endpoints.docsRoutes().
offset: 676
uri: file://<WORKSPACE>/psp-app/src/main/scala/psp/app/Main.scala
text:
```scala
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
        "/" -> Endpoints.d@@ocsRoutes()
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

```


#### Short summary: 

empty definition using pc, found symbol in pc: psp/api/Endpoints.docsRoutes().