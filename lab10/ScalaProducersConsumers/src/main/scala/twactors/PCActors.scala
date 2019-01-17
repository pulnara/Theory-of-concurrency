package twactors

import java.lang.Math
import java.util.Random

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Stash}
import akka.event.Logging
import akka.event.LoggingReceive
import scala.concurrent.duration._
import scala.concurrent.Await

// Assignment:
// - implement solution to the producers/consumers problem
//   using the actor model / Akka
// - test the correctness of the created solution for multiple
//   producers and consumers
// Hint: use akka.actor.Stash


// object PC contains messages for all actors -- add new if you need them
object PC {
  case class Init()
  case class Put(x: Long)
  case class Get()
  case class ProduceDone()
  case class ConsumeDone(x: Long)
  case class ImDying()
}

class Producer(name: String, buf: ActorRef) extends Actor {
  import PC._
  var repeat = 5
  var rand = new Random()

  private def iter(): Unit = {
    if (repeat > 0) {
      var product: Long = Math.abs(rand.nextLong() % 100)
      println("P" + name + "(" + repeat + ") producing " + product)
      buf ! Put(product)
      repeat -= 1
    } else {
      println("P" + name + " BYE!")
      buf ! ImDying
      context.stop(self)
    }
  }

  def receive = LoggingReceive {
    case Init => {
      iter()
    }
    case ProduceDone => {
      iter()
    }
  }
}

class Consumer(name: String, buf: ActorRef) extends Actor {
  import PC._
  var repeat = 5

  private def iter(): Unit = {
    if (repeat > 0) {
      buf ! Get
      repeat -= 1
    } else {
      println("C" + name + " BYE!")
      buf ! ImDying
      context.stop(self)
    }
  }

  def receive = LoggingReceive {
    case Init => {
      iter()
    }
    case ConsumeDone(x) => {
      println("C" + name + "(" + (repeat + 1) + "): received " + x)
      iter()
    }
  }
}


class Buffer(n: Int, actorsNum: Int) extends Actor with Stash {
  import PC._

  private val buf = new Array[Long](n)
  private var count = 0
  private var actorsNumber = actorsNum

  def receive = LoggingReceive {
    case Put(x) if count < n => {
      buf(count) = x
      println(buf.mkString(" "))
      print(count)
      count += 1
      sender ! ProduceDone
      unstashAll()
    }
    case Get if count > 0 => {
      count -= 1
      val dropped = buf(count)
      buf(count) = 0
      sender ! ConsumeDone(dropped)
      unstashAll()
    }
    case ImDying => {
      actorsNumber -= 1
      if (actorsNumber == 0) {
        println("FINISHING")
        context.system.terminate()
      }
    }
    case _ => {
      stash()
    }
  }
}


object ProdConsMain extends App {
  import PC._

  val system = ActorSystem("ProdKons")

  val prodNum = 5
  val consNum = 5
  val buf_size = 50

  val buffer = system.actorOf(Props(new Buffer(buf_size, prodNum + consNum)), "B")

  // create Consumer actors. Use "p ! Init" to kick them off
  for (i: Int <- 1 to consNum) {
    val c = system.actorOf(Props(new Consumer(i.toString : String, buffer)), "C" + i.toString)
    println("About to start consumer " + i)
    c ! Init
  }

  // create Producer actors. Use "p ! Init" to kick them off
  for (i: Int <- 1 to prodNum) {
    val p = system.actorOf(Props(new Producer(i.toString : String, buffer)), "P" + i.toString)
    println("About to start producer " + i)
    p ! Init
  }

  Await.result(system.whenTerminated, Duration.Inf)
}



