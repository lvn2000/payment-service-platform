error id: file://<WORKSPACE>/psp-acquirer-mock/src/main/scala/psp/acquirer/MockAcquirer.scala:psp/acquirer/MockAcquirer#`<error: <none>>`#
file://<WORKSPACE>/psp-acquirer-mock/src/main/scala/psp/acquirer/MockAcquirer.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -psp/core/AcquirerClient#
	 -psp/core/AcquirerDecision.AcquirerClient#
	 -psp/domain/AcquirerClient#
	 -AcquirerClient#
	 -scala/Predef.AcquirerClient#
offset: 204
uri: file://<WORKSPACE>/psp-acquirer-mock/src/main/scala/psp/acquirer/MockAcquirer.scala
text:
```scala
package psp.acquirer

import cats.effect.IO
import psp.core.{AcquirerClient, AcquirerDecision}
import psp.core.AcquirerDecision._
import psp.domain._

final class MockAcquirer(name: String) extends Acquir@@erClient {
  override def authorize(tx: Transaction): IO[AcquirerDecision] = {
    val lastDigit =
      tx.card.number.filter(_.isDigit).lastOption.map(_.asDigit).getOrElse(1)
    if (lastDigit % 2 == 0) IO.pure(Approved) else IO.pure(Denied)
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 