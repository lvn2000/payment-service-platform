package psp.core

import munit.FunSuite

class ValidationSpec extends FunSuite {

  test("Validation.luhnValid should return true for valid card numbers") {
    // Test cases from various card types
    assert(Validation.luhnValid("4242424242424242")) // Visa test card
    assert(Validation.luhnValid("5555555555554444")) // Mastercard test card
    assert(Validation.luhnValid("378282246310005")) // Amex test card
    assert(Validation.luhnValid("6011111111111117")) // Discover test card
    assert(Validation.luhnValid("4000000000000002")) // Visa test card
  }

  test("Validation.luhnValid should return false for invalid card numbers") {
    assert(!Validation.luhnValid("4242424242424241")) // Invalid Visa
    assert(!Validation.luhnValid("5555555555554443")) // Invalid Mastercard
    assert(!Validation.luhnValid("378282246310004")) // Invalid Amex
    assert(!Validation.luhnValid("1234567890123457")) // Random invalid
    assert(
      !Validation.luhnValid("0000000000000001")
    ) // Invalid all zeros with 1
  }

  test("Validation.luhnValid should handle numbers with non-digit characters") {
    assert(Validation.luhnValid("4242-4242-4242-4242")) // With dashes
    assert(Validation.luhnValid("4242 4242 4242 4242")) // With spaces
    assert(Validation.luhnValid("4242.4242.4242.4242")) // With dots
    assert(!Validation.luhnValid("4242-4242-4242-4241")) // Invalid with dashes
  }

  test("Validation.luhnValid should handle edge cases") {
    assert(Validation.luhnValid("")) // Empty string (sum=0, 0%10=0, so valid)
    assert(!Validation.luhnValid("123")) // Too short
    assert(Validation.luhnValid("abc")) // No digits (sum=0, 0%10=0, so valid)
    assert(Validation.luhnValid("4242424242424242a")) // Valid with letter
    assert(Validation.luhnValid("a4242424242424242")) // Letter at start
  }

  test("Validation.luhnValid should handle single digit numbers") {
    assert(Validation.luhnValid("0")) // Valid single digit
    assert(!Validation.luhnValid("1")) // Invalid single digit
    assert(!Validation.luhnValid("2")) // Invalid single digit
    assert(!Validation.luhnValid("3")) // Invalid single digit
    assert(!Validation.luhnValid("4")) // Invalid single digit
    assert(!Validation.luhnValid("5")) // Invalid single digit
    assert(!Validation.luhnValid("6")) // Invalid single digit
    assert(!Validation.luhnValid("7")) // Invalid single digit
    assert(!Validation.luhnValid("8")) // Invalid single digit
    assert(!Validation.luhnValid("9")) // Invalid single digit
  }

  test("Validation.luhnValid should handle very long numbers") {
    val longValid =
      "4242424242424242424242424242424242" // Extended valid number
    val longInvalid =
      "4242424242424242424242424242424241" // Extended invalid number

    assert(Validation.luhnValid(longValid))
    assert(!Validation.luhnValid(longInvalid))
  }
}
