package ru.rustam.owm.http

import akka.actor.{Actor, ActorContext}

class BaseApiActor extends Actor with ApiService {

  override def actorRefFactory: ActorContext = context

  def receive: Receive = runRoute(route)

}