error id: FCB8254F561DE1FEE8D042853524D99F
file://<WORKSPACE>/psp-api/src/test/scala/psp/api/EndpointsSpec.scala
### java.lang.AssertionError: assertion failed: (List(),0)

occurred in the presentation compiler.



action parameters:
offset: 265
uri: file://<WORKSPACE>/psp-api/src/test/scala/psp/api/EndpointsSpec.scala
text:
```scala
package psp.api

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import munit.FunSuite
import org.http4s.{Method, Request, Status}
import org.http4s.implicits._
import org.http4s.circe.CirceEntityEncoder._
import psp.storage.InMemoryTransactionRepo@@
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

```


presentation compiler configuration:
Scala version: 2.13.16
Classpath:
<WORKSPACE>/.bloop/api/bloop-bsp-clients-classes/test-classes-Metals-_EqYtCGWTwSoUrL5CHmWNg== [exists ], <HOME>/.cache/bloop/semanticdb/com.sourcegraph.semanticdb-javac.0.11.0/semanticdb-javac-0.11.0.jar [exists ], <WORKSPACE>/.bloop/api/bloop-bsp-clients-classes/classes-Metals-_EqYtCGWTwSoUrL5CHmWNg== [exists ], <WORKSPACE>/.bloop/domain/bloop-bsp-clients-classes/classes-Metals-_EqYtCGWTwSoUrL5CHmWNg== [exists ], <WORKSPACE>/.bloop/core/bloop-bsp-clients-classes/classes-Metals-_EqYtCGWTwSoUrL5CHmWNg== [exists ], <WORKSPACE>/.bloop/storage/bloop-bsp-clients-classes/classes-Metals-_EqYtCGWTwSoUrL5CHmWNg== [exists ], <WORKSPACE>/.bloop/acquirerMock/bloop-bsp-clients-classes/classes-Metals-_EqYtCGWTwSoUrL5CHmWNg== [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.16/scala-library-2.13.16.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/http4s/http4s-dsl_2.13/0.23.27/http4s-dsl_2.13-0.23.27.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/http4s/http4s-ember-server_2.13/0.23.27/http4s-ember-server_2.13-0.23.27.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/http4s/http4s-circe_2.13/0.23.27/http4s-circe_2.13-0.23.27.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-core_2.13/1.10.8/tapir-core_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-json-circe_2.13/1.10.8/tapir-json-circe_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-http4s-server_2.13/1.10.8/tapir-http4s-server_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-swagger-ui-bundle_2.13/1.10.8/tapir-swagger-ui-bundle_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-core_2.13/0.14.10/circe-core_2.13-0.14.10.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-generic_2.13/0.14.10/circe-generic_2.13-0.14.10.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-parser_2.13/0.14.10/circe-parser_2.13-0.14.10.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scalameta/munit_2.13/1.0.0/munit_2.13-1.0.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-core_2.13/2.12.0/cats-core_2.13-2.12.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-effect_2.13/3.5.4/cats-effect_2.13-3.5.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/http4s/http4s-core_2.13/0.23.27/http4s-core_2.13-0.23.27.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/http4s/http4s-ember-core_2.13/0.23.27/http4s-ember-core_2.13-0.23.27.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/http4s/http4s-server_2.13/0.23.27/http4s-server_2.13-0.23.27.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/log4cats-slf4j_2.13/2.6.0/log4cats-slf4j_2.13-2.6.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/http4s/http4s-jawn_2.13/0.23.27/http4s-jawn_2.13-0.23.27.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-jawn_2.13/0.14.10/circe-jawn_2.13-0.14.10.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/model/core_2.13/1.7.11/core_2.13-1.7.11.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/shared/core_2.13/1.3.19/core_2.13-1.3.19.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/shared/ws_2.13/1.3.19/ws_2.13-1.3.19.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/magnolia1_2/magnolia_2.13/1.1.10/magnolia_2.13-1.1.10.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-server_2.13/1.10.8/tapir-server_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-cats_2.13/1.10.8/tapir-cats_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-cats-effect_2.13/1.10.8/tapir-cats-effect_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/shared/fs2_2.13/1.3.19/fs2_2.13-1.3.19.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-swagger-ui_2.13/1.10.8/tapir-swagger-ui_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-openapi-docs_2.13/1.10.8/tapir-openapi-docs_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/apispec/openapi-circe-yaml_2.13/0.10.0/openapi-circe-yaml_2.13-0.10.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-numbers_2.13/0.14.10/circe-numbers_2.13-0.14.10.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/chuusai/shapeless_2.13/2.3.12/shapeless_2.13-2.3.12.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scalameta/junit-interface/1.0.0/junit-interface-1.0.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scalameta/munit-diff_2.13/1.0.0/munit-diff_2.13-1.0.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-kernel_2.13/2.12.0/cats-kernel_2.13-2.12.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-effect-kernel_2.13/3.5.4/cats-effect-kernel_2.13-3.5.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-effect-std_2.13/3.5.4/cats-effect-std_2.13-3.5.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/case-insensitive_2.13/1.4.0/case-insensitive_2.13-1.4.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-parse_2.13/1.0.0/cats-parse_2.13-1.0.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/http4s/http4s-crypto_2.13/0.2.4/http4s-crypto_2.13-0.2.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/co/fs2/fs2-core_2.13/3.10.2/fs2-core_2.13-3.10.2.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/co/fs2/fs2-io_2.13/3.10.2/fs2-io_2.13-3.10.2.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/comcast/ip4s-core_2.13/3.5.0/ip4s-core_2.13-3.5.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/literally_2.13/1.1.0/literally_2.13-1.1.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scodec/scodec-bits_2.13/1.1.38/scodec-bits_2.13-1.1.38.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/vault_2.13/3.5.0/vault_2.13-3.5.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/log4s/log4s_2.13/1.10.0/log4s_2.13-1.10.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/log4cats-core_2.13/2.6.0/log4cats-core_2.13-2.6.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/twitter/hpack/1.0.2/hpack-1.0.2.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/jawn-fs2_2.13/2.4.0/jawn-fs2_2.13-2.4.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/jawn-parser_2.13/1.6.0/jawn-parser_2.13-1.6.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-files_2.13/1.10.8/tapir-files_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/webjars/swagger-ui/5.17.2/swagger-ui-5.17.2.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-apispec-docs_2.13/1.10.8/tapir-apispec-docs_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/tapir/tapir-enumeratum_2.13/1.10.8/tapir-enumeratum_2.13-1.10.8.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/quicklens/quicklens_2.13/1.9.7/quicklens_2.13-1.9.7.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/apispec/openapi-model_2.13/0.10.0/openapi-model_2.13-0.10.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/apispec/openapi-circe_2.13/0.10.0/openapi-circe_2.13-0.10.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-yaml_2.13/0.15.1/circe-yaml_2.13-0.15.1.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-sbt/test-interface/1.0/test-interface-1.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-reflect/2.13.16/scala-reflect-2.13.16.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/apispec/asyncapi-model_2.13/0.10.0/asyncapi-model_2.13-0.10.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/beachape/enumeratum_2.13/1.7.3/enumeratum_2.13-1.7.3.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/apispec/apispec-model_2.13/0.10.0/apispec-model_2.13-0.10.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/softwaremill/sttp/apispec/jsonschema-circe_2.13/0.10.0/jsonschema-circe_2.13-0.10.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/io/circe/circe-yaml-common_2.13/0.15.1/circe-yaml-common_2.13-0.15.1.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/yaml/snakeyaml/2.2/snakeyaml-2.2.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/com/beachape/enumeratum-macros_2.13/1.6.4/enumeratum-macros_2.13-1.6.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/modules/scala-collection-compat_2.13/2.11.0/scala-collection-compat_2.13-2.11.0.jar [exists ]
Options:
-deprecation -feature -unchecked -Xlint:unused -Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
scala.tools.nsc.classpath.FileBasedCache.getOrCreate(ZipAndJarFileLookupFactory.scala:282)
	scala.tools.nsc.classpath.JrtClassPath$.createJrt(DirectoryClassPath.scala:188)
	scala.tools.nsc.classpath.JrtClassPath$.apply(DirectoryClassPath.scala:166)
	scala.tools.util.PathResolver$Calculated$.jrt(PathResolver.scala:273)
	scala.tools.util.PathResolver$Calculated$.basis(PathResolver.scala:262)
	scala.tools.util.PathResolver$Calculated$.containers$lzycompute(PathResolver.scala:275)
	scala.tools.util.PathResolver$Calculated$.containers(PathResolver.scala:275)
	scala.tools.util.PathResolver.containers(PathResolver.scala:291)
	scala.tools.util.PathResolver.computeResult(PathResolver.scala:313)
	scala.tools.util.PathResolver.result(PathResolver.scala:296)
	scala.tools.nsc.backend.JavaPlatform.classPath(JavaPlatform.scala:30)
	scala.tools.nsc.backend.JavaPlatform.classPath$(JavaPlatform.scala:29)
	scala.tools.nsc.Global$GlobalPlatform.classPath(Global.scala:133)
	scala.tools.nsc.Global.classPath(Global.scala:158)
	scala.tools.nsc.Global$GlobalMirror.rootLoader(Global.scala:68)
	scala.reflect.internal.Mirrors$Roots$RootClass.<init>(Mirrors.scala:309)
	scala.reflect.internal.Mirrors$Roots.RootClass$lzycompute(Mirrors.scala:323)
	scala.reflect.internal.Mirrors$Roots.RootClass(Mirrors.scala:323)
	scala.reflect.internal.Mirrors$Roots$EmptyPackageClass.<init>(Mirrors.scala:332)
	scala.reflect.internal.Mirrors$Roots.EmptyPackageClass$lzycompute(Mirrors.scala:338)
	scala.reflect.internal.Mirrors$Roots.EmptyPackageClass(Mirrors.scala:338)
	scala.reflect.internal.Mirrors$Roots.EmptyPackageClass(Mirrors.scala:278)
	scala.reflect.internal.Mirrors$RootsBase.init(Mirrors.scala:252)
	scala.tools.nsc.Global.rootMirror$lzycompute(Global.scala:75)
	scala.tools.nsc.Global.rootMirror(Global.scala:73)
	scala.tools.nsc.Global.rootMirror(Global.scala:45)
	scala.reflect.internal.Definitions$DefinitionsClass.ObjectClass$lzycompute(Definitions.scala:295)
	scala.reflect.internal.Definitions$DefinitionsClass.ObjectClass(Definitions.scala:295)
	scala.reflect.internal.Definitions$DefinitionsClass.init(Definitions.scala:1667)
	scala.tools.nsc.Global$Run.<init>(Global.scala:1263)
	scala.tools.nsc.interactive.Global$TyperRun.<init>(Global.scala:1352)
	scala.tools.nsc.interactive.Global.newTyperRun(Global.scala:1375)
	scala.tools.nsc.interactive.Global.<init>(Global.scala:295)
	scala.meta.internal.pc.MetalsGlobal.<init>(MetalsGlobal.scala:49)
	scala.meta.internal.pc.ScalaPresentationCompiler.newCompiler(ScalaPresentationCompiler.scala:627)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$compilerAccess$1(ScalaPresentationCompiler.scala:147)
	scala.meta.internal.pc.CompilerAccess.loadCompiler(CompilerAccess.scala:40)
	scala.meta.internal.pc.CompilerAccess.retryWithCleanCompiler(CompilerAccess.scala:182)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withSharedCompiler$1(CompilerAccess.scala:155)
	scala.Option.map(Option.scala:242)
	scala.meta.internal.pc.CompilerAccess.withSharedCompiler(CompilerAccess.scala:154)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withInterruptableCompiler$1(CompilerAccess.scala:92)
	scala.meta.internal.pc.CompilerAccess.$anonfun$onCompilerJobQueue$1(CompilerAccess.scala:209)
	scala.meta.internal.pc.CompilerJobQueue$Job.run(CompilerJobQueue.scala:152)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	java.base/java.lang.Thread.run(Thread.java:1583)
```
#### Short summary: 

java.lang.AssertionError: assertion failed: (List(),0)