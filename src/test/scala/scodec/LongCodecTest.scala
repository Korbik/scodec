package scodec

import scalaz.syntax.id._
import org.scalacheck.Gen

import Codecs._


class LongCodecTest extends CodecSuite {

  test("int64") { forAll { (n: Long) => roundtrip(int64, n) } }
  test("int64L") { forAll { (n: Long) => roundtrip(int64L, n) } }
  test("long(48)") { forAll { (n: Long) => whenever (n >= -(1L << 48) && n < (1L << 48)) { roundtrip(long(48), n) } } }
  test("uint32") { forAll(Gen.choose(0L, (1L << 32) - 1)) { (n: Long) => roundtrip(uint32, n) } }

  test("endianess") {
    forAll { (n: Long) =>
      val bigEndian = int64.encode(n).toOption.get.toByteVector
      val littleEndian = int64L.encode(n).toOption.get.toByteVector
      littleEndian shouldBe bigEndian.reverse
    }
  }

  test("range checking") {
    uint32.encode(-1) shouldBe "-1 is less than minimum value 0 for 32-bit unsigned integer".left
  }

  test("decoding with too few bits") {
    uint32.decode(BitVector.low(8)) shouldBe ("cannot acquire 32 bits from a vector that contains 8 bits".left)
  }
}
