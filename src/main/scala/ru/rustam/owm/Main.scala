package ru.rustam.owm

import akka.actor.{ActorSystem, DeadLetter, Props}
import akka.io.IO
import akka.util.Timeout
import ru.rustam.owm.http.BaseApiActor
import ru.rustam.owm.util.DeadLetterListener
import spray.can.Http

import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App {
  implicit val system = ActorSystem("owm-actor-system")
  system.eventStream.subscribe(system.actorOf(Props[DeadLetterListener]), classOf[DeadLetter])

  val baseApiActor = system.actorOf(Props[BaseApiActor], "base-api-actor")
//  implicit val timeout = Timeout(10 seconds)
  implicit val execContext = system.dispatcher
  IO(Http) ! Http.Bind(baseApiActor, interface = "0.0.0.0", port = 9000)
}
