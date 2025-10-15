error id: file://<WORKSPACE>/psp-app/src/main/scala/psp/app/Main.scala:
file://<WORKSPACE>/psp-app/src/main/scala/psp/app/Main.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -org/http4s/server/Router.
	 -org/http4s/server/Router#
	 -org/http4s/server/Router().
	 -org/http4s/implicits/Router.
	 -org/http4s/implicits/Router#
	 -org/http4s/implicits/Router().
	 -com/comcast/ip4s/Router.
	 -com/comcast/ip4s/Router#
	 -com/comcast/ip4s/Router().
	 -psp/core/Router.
	 -psp/core/Router#
	 -psp/core/Router().
	 -Router.
	 -Router#
	 -Router().
	 -scala/Predef.Router.
	 -scala/Predef.Router#
	 -scala/Predef.Router().
offset: 603
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
      httpApp = Rou@@ter(
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

```


#### Short summary: 

empty definition using pc, found symbol in pc: 