package ru.rustam.owm

import akka.actor.{Actor, ActorContext}

class BaseApiActor extends Actor with MyService {

  override def actorRefFactory: ActorContext = context

  def receive: Receive = runRoute(route)

}