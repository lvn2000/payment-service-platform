package psp.core

object Validation {
  def luhnValid(number: String): Boolean = {
    val digits = number.filter(_.isDigit).map(_.asDigit).reverse
    val sum = digits.zipWithIndex.map { case (d, idx) =>
      if (idx % 2 == 1) { val x = d * 2; if (x > 9) x - 9 else x }
      else d
    }.sum
    sum % 10 == 0
  }
}
