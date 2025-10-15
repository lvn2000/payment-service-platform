package psp.core

import munit.FunSuite

class RoutingSpec extends FunSuite {

  test("Routing.routeByBin should route to Acquirer A for even BIN sum") {
    // BIN: 424242 (4+2+4+2+4+2 = 18, even)
    assertEquals(Routing.routeByBin("4242424242424242"), AcquirerType.A)

    // BIN: 111111 (1+1+1+1+1+1 = 6, even)
    assertEquals(Routing.routeByBin("1111111111111111"), AcquirerType.A)

    // BIN: 222222 (2+2+2+2+2+2 = 12, even)
    assertEquals(Routing.routeByBin("2222222222222222"), AcquirerType.A)

    // BIN: 000000 (0+0+0+0+0+0 = 0, even)
    assertEquals(Routing.routeByBin("0000000000000000"), AcquirerType.A)
  }

  test("Routing.routeByBin should route to Acquirer B for odd BIN sum") {
    // BIN: 123456 (1+2+3+4+5+6 = 21, odd)
    assertEquals(Routing.routeByBin("1234567890123456"), AcquirerType.B)

    // BIN: 111110 (1+1+1+1+1+0 = 5, odd)
    assertEquals(Routing.routeByBin("1111100000000000"), AcquirerType.B)

    // BIN: 555555 (5+5+5+5+5+5 = 30, even) - should be A
    assertEquals(Routing.routeByBin("5555555555555555"), AcquirerType.A)

    // BIN: 123450 (1+2+3+4+5+0 = 15, odd)
    assertEquals(Routing.routeByBin("1234500000000000"), AcquirerType.B)
  }

  test("Routing.routeByBin should handle numbers with non-digit characters") {
    // BIN: 424242 (even sum)
    assertEquals(Routing.routeByBin("4242-4242-4242-4242"), AcquirerType.A)
    assertEquals(Routing.routeByBin("4242 4242 4242 4242"), AcquirerType.A)
    assertEquals(Routing.routeByBin("4242.4242.4242.4242"), AcquirerType.A)

    // BIN: 123456 (odd sum)
    assertEquals(Routing.routeByBin("1234-5678-9012-3456"), AcquirerType.B)
    assertEquals(Routing.routeByBin("1234 5678 9012 3456"), AcquirerType.B)
  }

  test("Routing.routeByBin should handle short numbers (less than 6 digits)") {
    // Only 3 digits: 123 (1+2+3 = 6, even)
    assertEquals(Routing.routeByBin("123"), AcquirerType.A)

    // Only 2 digits: 12 (1+2 = 3, odd)
    assertEquals(Routing.routeByBin("12"), AcquirerType.B)

    // Only 1 digit: 1 (1, odd)
    assertEquals(Routing.routeByBin("1"), AcquirerType.B)

    // Only 1 digit: 2 (2, even)
    assertEquals(Routing.routeByBin("2"), AcquirerType.A)
  }

  test("Routing.routeByBin should handle empty string") {
    // Empty string should default to sum = 0 (even)
    assertEquals(Routing.routeByBin(""), AcquirerType.A)
  }

  test("Routing.routeByBin should handle numbers with no digits") {
    // No digits should default to sum = 0 (even)
    assertEquals(Routing.routeByBin("abc"), AcquirerType.A)
    assertEquals(Routing.routeByBin("!@#$%"), AcquirerType.A)
  }

  test("Routing.routeByBin should handle mixed alphanumeric strings") {
    // BIN: 123456 (odd sum)
    assertEquals(Routing.routeByBin("abc123456def789"), AcquirerType.B)

    // BIN: 424242 (even sum)
    assertEquals(Routing.routeByBin("xyz424242abc"), AcquirerType.A)
  }

  test(
    "Routing.routeByBin should take only first 6 digits for BIN calculation"
  ) {
    // First 6 digits: 111111 (sum = 6, even), rest doesn't matter
    assertEquals(Routing.routeByBin("1111119999999999"), AcquirerType.A)

    // First 6 digits: 123456 (sum = 21, odd), rest doesn't matter
    assertEquals(Routing.routeByBin("1234560000000000"), AcquirerType.B)
  }
}
