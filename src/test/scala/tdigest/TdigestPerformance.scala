package tdigest

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

import org.scalameter._
import com.tdunning.math.stats.MergingDigest
import org.scalameter

import scala.collection.parallel.ForkJoinTaskSupport

object TdigestPerformance extends App {

//  config(
//    Key.exec.minWarmupRuns -> 2,
//    Key.exec.maxWarmupRuns -> 10,
//    Key.verbose -> true
//  ) withWarmer new Warmer.Default

  import collection.JavaConverters._

  val time = config(
    Key.exec.minWarmupRuns -> 2,
    Key.exec.maxWarmupRuns -> 10,
    Key.verbose -> true
  ) withMeasurer new scalameter.Measurer.MemoryFootprint withWarmer new Warmer.Default measure {
    val digests = for (i <- 1 to 100000) yield {
      val rand = for (i <- 1 to 1000) yield util.Random.nextFloat()
      val digest = new MergingDigest(100)
      rand.foreach(digest.add(_))
      digest
    }

    val finalDigest = new MergingDigest(100)
    val smallDigests = digests.toList.grouped(100).toList.par
    smallDigests.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(4))
    val processed = smallDigests.map(dgsts =>{
      val d = new MergingDigest(100)
      dgsts.foreach(d.add(_))
      d
    }
    ).toList
    processed.foreach(finalDigest.add(_))

    println(s"Final digest size: ${finalDigest.size()}")
    println(s"Percentile: ${finalDigest.quantile(0.75)}")

    val serialized = digests.map(o => {
      val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
      val oos = new ObjectOutputStream(stream)
      oos.writeObject(o)
      oos.close()
      stream.toByteArray
    })

    serialized
  }

  println(time)

}
