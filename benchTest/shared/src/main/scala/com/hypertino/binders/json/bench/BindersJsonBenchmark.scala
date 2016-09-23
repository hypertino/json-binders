package com.hypertino.binders.json.bench

import org.openjdk.jmh.annotations.{Benchmark, Scope, State}

import scala.util.Random

case class Bench1CaseClass(a: String, b: Int, d: Double, an: Option[String], bn: Option[Int], dn: Option[Double])
case class Bench2CaseClass(x: String, y: Double, i: Int, b1: Bench1CaseClass, b2: List[Bench1CaseClass])

@State(Scope.Benchmark)
class BindersJsonBenchmark{
  import com.hypertino.binders.json._
  val rand = new Random(100500)

  val b1 = Bench1CaseClass(rstr(32), rand.nextInt, rand.nextDouble(), Some(rstr(32)), Some(rand.nextInt), None)
  val b2 = Bench2CaseClass(rstr(16), rand.nextDouble, rand.nextInt, b1, 1 to 10 map { _ ⇒ b1} toList)
  val b1Str = b1.toJson
  val b2Str = b2.toJson

  //println(b2Str)

  @Benchmark
  def Binders_serializeCaseClass(): Int = {
    val str = b1.toJson
    str.length
  }

  @Benchmark
  def Binders_serializeAndDeserializeCaseClass(): Int = {
    val str = b1.toJson
    str.parseJson[Bench1CaseClass].b
  }

  @Benchmark
  def Binders_deserializeCaseClass(): Int = {
    b1Str.parseJson[Bench1CaseClass].b
  }

  @Benchmark
  def Binders_serializeCaseClass2(): Int = {
    val str = b2.toJson
    str.length
  }

  @Benchmark
  def Binders_serializeAndDeserializeCaseClass2(): Int = {
    val str = b2.toJson
    str.parseJson[Bench2CaseClass].i
  }

  @Benchmark
  def Binders_deserializeCaseClass2(): Int = {
    b2Str.parseJson[Bench2CaseClass].i
  }

  import upickle.default._

  @Benchmark
  def Upickle_serializeCaseClass(): Int = {
    val str = write(b1)
    str.length
  }

  @Benchmark
  def Upickle_serializeAndDeserializeCaseClass(): Int = {
    val str = write(b1)
    read[Bench1CaseClass](str).b
  }

  @Benchmark
  def Upickle_deserializeCaseClass(): Int = {
    read[Bench1CaseClass](b1Str).b
  }

  @Benchmark
  def Upickle_serializeCaseClass2(): Int = {
    val str = write(b2)
    str.length
  }

  @Benchmark
  def Upickle_serializeAndDeserializeCaseClass2(): Int = {
    val str = write(b2)
    read[Bench2CaseClass](str).i
  }

  @Benchmark
  def Upickle_deserializeCaseClass2(): Int = {
    read[Bench2CaseClass](b2Str).i
  }

  def rstr(len: Int = 16) = rand.alphanumeric.take(len).mkString
}
