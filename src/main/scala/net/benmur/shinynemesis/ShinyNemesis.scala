/**
  * Copyright (c) 2012 Rached Ben Mustapha
  *
  * See the file LICENSE for copying permission.
  */

package net.benmur.shinynemesis

import java.io.File

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration.DurationInt
import scala.util.{ Failure, Success }

import akka.actor.{ Actor, ActorSystem, Props, actorRef2Scala }
import akka.pattern.ask
import akka.util.Timeout
import net.benmur.shinynemesis.analyzer.ColorAnalyzer
import net.benmur.shinynemesis.input.{ Eof, Error }
import net.benmur.shinynemesis.input.xuggle.XuggleReader
import net.benmur.shinynemesis.output.ImageColorOutput

class FileHandler extends Actor {
  def receive = {
    case filename: String =>
      val outputFile = new File(filename).getName().replaceAll("\\.[\\d\\w]+$", ".png")
      val out = new ImageColorOutput(outputFile)
      if (!out.alreadyExists) {
        val colorAnalyzer = new ColorAnalyzer(Config.COLOR_DIFF_THRESHOLD, 1)
        println("Reading " + filename)
        new XuggleReader(filename, colorAnalyzer).readAll match {
          case Error =>
            println("Finishing unexpectedly, not writing output")
            sender ! false
          case Eof =>
            println("End of file reached, writing to " + outputFile)
            out.writeStatsFrom(colorAnalyzer)
            sender ! true
        }
      }
  }
}

object ShinyNemesis extends App {
  implicit val system = ActorSystem()
  implicit val timeout = Timeout(1.hour)
  implicit val ec = ExecutionContext.global
  val handler = system.actorOf(Props[FileHandler])

  val futures = args.toList map (handler ? _)
  Future.sequence(futures) onComplete {
    case res =>
      res match {
        case Success(result) => println("ok")
        case Failure(ex)     => println("fail")
      }
      println("Took %ds".format((System.currentTimeMillis() - executionStart) / 1000))
      system.shutdown
  }
}