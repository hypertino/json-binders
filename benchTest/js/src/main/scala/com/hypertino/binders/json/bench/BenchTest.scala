package com.hypertino.binders.json.bench

import scala.scalajs.js.JSApp
import scala.util.control.NonFatal


object BenchTest extends JSApp {
  val TEST_TIME = 10000 // ms

  def run(iteration: String): Unit = {
    val cls = new BindersJsonBenchmark
    import cls._
    println(s"Iteration: $iteration")
    measure("Binders_serializeCaseClassWithInnerArray", Binders_serializeCaseClassWithInnerArray)
    measure("Binders_serializeAndDeserializeCaseClassWithInnerArray", Binders_serializeAndDeserializeCaseClassWithInnerArray)
    measure("Binders_deserializeCaseClassWithInnerArray", Binders_deserializeCaseClassWithInnerArray)
    measure("Binders_serializeAndDeserializeCaseClass", Binders_serializeAndDeserializeCaseClass)
    measure("Binders_serializeCaseClass", Binders_serializeCaseClass)
    measure("Binders_deserializeCaseClass", Binders_deserializeCaseClass)
    //measure("Upickle_serializeCaseClassWithInnerArray", Upickle_serializeCaseClassWithInnerArray)
    //measure("Upickle_serializeAndDeserializeCaseClassWithInnerArray", Upickle_serializeAndDeserializeCaseClassWithInnerArray)
    //measure("Upickle_deserializeCaseClassWithInnerArray", Upickle_deserializeCaseClassWithInnerArray)
    measure("Upickle_serializeCaseClass", Upickle_serializeCaseClass)
    measure("Upickle_serializeAndDeserializeCaseClass", Upickle_serializeAndDeserializeCaseClass)
    measure("Upickle_deserializeCaseClass", Upickle_deserializeCaseClass)
  }

  def main(): Unit = {
    val iterations = 3
    1 to iterations foreach { iteration ⇒
      run(iteration.toString)
    }
  }

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
      println(s"Total $totalRuns runs of '$name': for ${after - before}ms. $opsPerSec ops/sec")
    }
    catch {
      case NonFatal(e) ⇒
        println(e)
    }
  }
}
