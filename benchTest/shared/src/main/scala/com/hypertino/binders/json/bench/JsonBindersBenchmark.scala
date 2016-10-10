package com.hypertino.binders.json.bench

import com.hypertino.binders.json.JsonBinders
import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scala.language.postfixOps
import scala.util.Random

case class Bench1Class(a: String, b: Int, d: Double, an: Option[String], bn: Option[Int], dn: Option[Double])
case class Bench2Class(x: String, y: Double, i: Int, b1: Bench1Class, b2: List[Bench1Class])

case class Bench1ClassNoOption(a: String, b: Int, d: Double, an: String, bn: Int, dn: Double)
case class Bench2ClassNoOption(x: String, y: Double, i: Int, b1: Bench1ClassNoOption, b2: List[Bench1ClassNoOption])

object Bench1Class {
  implicit val pkl = upickle.default.macroRW[Bench1Class]
}

object Bench2Class {
  implicit val pkl = upickle.default.macroRW[Bench2Class]
}

object Bench1ClassNoOption {
  implicit val pkl = upickle.default.macroRW[Bench1ClassNoOption]
}

object Bench2ClassNoOption {
  implicit val pkl = upickle.default.macroRW[Bench2ClassNoOption]
}

@State(Scope.Benchmark)
class JsonBindersBenchmark{
  import JsonBinders._
  val rand = new Random(100500)

  val b1 = Bench1Class(rstr(32), rand.nextInt, rand.nextDouble, Some(rstr(32)), Some(rand.nextInt), None)
  val b2 = Bench2Class(rstr(16), rand.nextDouble, rand.nextInt, b1, 1 to 10 map { _ ⇒ b1} toList)
  val b1Str = b1.toJson
  val b2Str = b2.toJson

  val b3 = Bench1ClassNoOption(rstr(32), rand.nextInt, rand.nextDouble(), rstr(32), rand.nextInt, rand.nextDouble)
  val b4 = Bench2ClassNoOption(rstr(16), rand.nextDouble, rand.nextInt, b3, 1 to 10 map { _ ⇒ b3} toList)
  val b3Str = b3.toJson
  val b4Str = b4.toJson

  @Benchmark
  def Binders_serializeCaseClass(): Int = {
    val str = b1.toJson
    str.length
  }

  @Benchmark
  def Binders_serializeAndDeserializeCaseClass(): Int = {
    val str = b1.toJson
    str.parseJson[Bench1Class].b
  }

  @Benchmark
  def Binders_deserializeCaseClass(): Int = {
    b1Str.parseJson[Bench1Class].b
  }

  @Benchmark
  def Binders_serializeCaseClass2(): Int = {
    val str = b2.toJson
    str.length
  }

  @Benchmark
  def Binders_serializeAndDeserializeCaseClass2(): Int = {
    val str = b2.toJson
    str.parseJson[Bench2Class].i
  }

  @Benchmark
  def Binders_deserializeCaseClass2(): Int = {
    b2Str.parseJson[Bench2Class].i
  }

  @Benchmark
  def Binders_serializeAndDeserializeCaseClass2NoOption(): Int = {
    val str = b4.toJson
    str.parseJson[Bench2ClassNoOption].i
  }

  import upickle.default._
  val b1StrU = write(b1)
  val b2StrU = write(b2)
  val b3StrU = write(b3)
  val b4StrU = write(b4)

  @Benchmark
  def Upickle_serializeCaseClass(): Int = {
    val str = write(b1)
    str.length
  }

  @Benchmark
  def Upickle_serializeAndDeserializeCaseClass(): Int = {
    val str = write(b1)
    read[Bench1Class](str).b
  }

  @Benchmark
  def Upickle_deserializeCaseClass(): Int = {
    read[Bench1Class](b1StrU).b
  }

  @Benchmark
  def Upickle_serializeCaseClass2(): Int = {
    val str = write(b2)
    str.length
  }

  @Benchmark
  def Upickle_serializeAndDeserializeCaseClass2(): Int = {
    val str = write(b2)
    read[Bench2Class](str).i
  }

  @Benchmark
  def Upickle_deserializeCaseClass2(): Int = {
    read[Bench2Class](b2StrU).i
  }

  @Benchmark
  def Upickle_serializeAndDeserializeCaseClass2NoOption(): Int = {
    val str = write(b4)
    read[Bench2ClassNoOption](str).i
  }

  def rstr(len: Int = 16) = rand.alphanumeric.take(len).mkString
}
