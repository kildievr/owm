package ru.rustam.owm

import akka.actor.ActorRefFactory
import spray.http.StatusCodes.OK
import spray.routing.{HttpService, Route}

trait MyService extends HttpService {
  override implicit def actorRefFactory: ActorRefFactory = Main.system

  val route: Route =
    path("coordinationsTransform") {
      post {
        formFields('x, 'y, 'proj4from, 'proj4to) { (x, y, proj4from, proj4to) =>
          println(s"$x, $y, $proj4from, $proj4to")
          ctx => ctx.complete(OK)
        }
      }
    } ~
      path("proj4ToEPSG") {
        get {
          parameters('data, 'signature) { (data, signature) =>
            ctx => ctx.complete(OK)
          }
        }
      }
}
