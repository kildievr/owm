package ru.rustam.owm.http

import akka.actor.Props
import akka.event.Logging.InfoLevel
import akka.routing.SmallestMailboxPool
import ru.rustam.owm.handlers.{Converter, Transformer}
import spray.http.HttpRequest
import spray.routing.directives.{LogEntry, LoggingMagnet}
import spray.routing.{HttpService, Route}

trait ApiService extends HttpService {

  private val converterRouter = actorRefFactory.actorOf(SmallestMailboxPool(10).props(Props[Converter]), "converter")
  private val transformerRouter = actorRefFactory.actorOf(SmallestMailboxPool(10).props(Props[Transformer]), "transformerActor")

  def requestMethodAndResponseStatusAsInfo(req: HttpRequest): Any => Option[LogEntry] = {
    res: Any => Some(LogEntry(req + "\n" + res, InfoLevel))
  }
  def printRequestMethodAndResponseStatus(req: HttpRequest)(res: Any): Unit =
    println(requestMethodAndResponseStatusAsInfo(req)(res).map(x => x.obj.toString).getOrElse(""))

  val route: Route = logRequestResponse(LoggingMagnet(printRequestMethodAndResponseStatus)) {
    path("coordinatesTransform") {
      post {
        formFields('x, 'y, 'proj4from, 'proj4to) { (x, y, proj4from, proj4to) =>
          ctx => transformerRouter ! (x, y, proj4from, proj4to, ctx)
        }
      }
    } ~
      path("proj4ToEPSG") {
        get {
          parameters('proj4string) { (proj4string) =>
            ctx => converterRouter ! (proj4string, ctx)
          }
        }
      }
  }
}
