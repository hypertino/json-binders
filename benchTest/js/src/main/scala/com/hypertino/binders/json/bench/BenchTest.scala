package com.hypertino.binders.json.bench

import scala.collection.mutable
import scala.scalajs.js.JSApp
import scala.util.control.NonFatal

case class MeasureResult(name: String, totalRuns: Long, time: Long, opsPerSec: Double)

object BenchTest extends JSApp {
  val TEST_TIME = 7000 // ms

  def main(): Unit = {
    val iterations = 3
    1 to iterations foreach { iteration ⇒
      run(iteration.toString)
    }
    println("------------------------------------------------------")
    printMeasures(measures)
  }

  def run(iteration: String): Unit = {
    val cls = new JsonBindersBenchmark
    import cls._
    println(s"Iteration: $iteration")
    measure("Binders_serializeCaseClass", Binders_serializeCaseClass)
    measure("Binders_deserializeCaseClass", Binders_deserializeCaseClass)
    measure("Binders_serializeAndDeserializeCaseClass", Binders_serializeAndDeserializeCaseClass)
    measure("Binders_serializeCaseClass2", Binders_serializeCaseClass2)
    measure("Binders_deserializeCaseClass2", Binders_deserializeCaseClass2)
    measure("Binders_serializeAndDeserializeCaseClass2", Binders_serializeAndDeserializeCaseClass2)
    measure("Binders_serializeAndDeserializeCaseClass2NoOption", Binders_serializeAndDeserializeCaseClass2NoOption)

    measure("Upickle_serializeCaseClass", Upickle_serializeCaseClass)
    measure("Upickle_deserializeCaseClass", Upickle_deserializeCaseClass)
    measure("Upickle_serializeAndDeserializeCaseClass", Upickle_serializeAndDeserializeCaseClass)
    measure("Upickle_serializeCaseClass2", Upickle_serializeCaseClass2)
    measure("Upickle_deserializeCaseClass2", Upickle_deserializeCaseClass2)
    measure("Upickle_serializeAndDeserializeCaseClass2", Upickle_serializeAndDeserializeCaseClass2)
    measure("Upickle_serializeAndDeserializeCaseClass2NoOption", Upickle_serializeAndDeserializeCaseClass2NoOption)
  }

  def alignRight(d: Double) = {
    val s = f"$d%8.4f"
    val spaces = Math.max(0, 16 - s.length)
    Seq.fill(spaces)(" ").mkString + s
  }

  val measures = mutable.ArrayBuffer[MeasureResult]()
  def measure(name: String, code: () ⇒ Unit) = {
    println(s"---------------------------------")
    println(s"Measuring: $name...")
    try {
      val before = System.currentTimeMillis()
      code()
      code()
      code()
      code()
      code()
      code()
      code()
      code()
      code()
      code()
      var after = System.currentTimeMillis()
      var totalRuns: Long = 10

      if (after - before < TEST_TIME) {
        val maxBatchSize = 1000
        val batch = if (after == before)
          maxBatchSize
        else
          Math.max(Math.min(maxBatchSize, TEST_TIME / (after - before + 1) ), 1)

        val endTime = System.currentTimeMillis() + TEST_TIME
        println(s"batch: $batch, endtime: $endTime")
        while (endTime > System.currentTimeMillis()) {
          var cnt = batch
          while (cnt > 0) {
            code()
            cnt -= 1
          }
          totalRuns += batch
        }
        after = System.currentTimeMillis()
      }
      val opsPerSec = 1.0 * totalRuns / ((after - before) / 1000.0)
      val measureResult = MeasureResult(name, totalRuns, after-before, opsPerSec)
      measures += measureResult
      println(s"Total ${measureResult.totalRuns} runs of '${measureResult.name}': for ${measureResult.time} ms. ${measureResult.opsPerSec} ops/sec")
    }
    catch {
      case NonFatal(e) ⇒
        println(e)
    }
  }

  private def printMeasures(measures: Iterable[MeasureResult]): Unit = {
    val alignLen = measures.map(_.name.length).max
    measures.foreach { m ⇒
      println(s"${m.name}${Seq.fill(alignLen - m.name.length)(" ").mkString} ${alignRight(m.opsPerSec)} ops/sec. ${m.totalRuns} cnt for ${m.time} ms")
    }
  }
}
