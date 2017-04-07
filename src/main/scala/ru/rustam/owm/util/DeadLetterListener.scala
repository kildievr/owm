package ru.rustam.owm.util

import akka.actor.{Actor, DeadLetter}

class DeadLetterListener extends Actor {
  override def receive: Receive = {
    case DeadLetter(msg, from, to) =>
      println(s"msg = $msg, from = $from, to = $to")
  }
}