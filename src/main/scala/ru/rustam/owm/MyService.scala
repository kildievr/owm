package ru.rustam.owm

import akka.actor.Props
import akka.event.Logging
import akka.routing.SmallestMailboxPool
import spray.http.{HttpRequest, HttpResponse}
import spray.routing.directives.{LogEntry, LoggingMagnet}
import spray.routing.{HttpService, Route}

trait MyService extends HttpService {

  private val converterRouter = actorRefFactory.actorOf(SmallestMailboxPool(10).props(Props[Converter]), "converter")
  private val transformerRouter = actorRefFactory.actorOf(SmallestMailboxPool(10).props(Props[Transformer]), "TransformerActor")

  def requestMethodAndResponseStatusAsInfo(req: HttpRequest): Any => Option[LogEntry] = {
    case res: HttpResponse => Some(LogEntry(req + "\n" + res, Logging.InfoLevel))
    case _ => None
  }
  def printRequestMethodAndResponseStatus(req: HttpRequest)(res: Any): Unit =
    println(requestMethodAndResponseStatusAsInfo(req)(res).map(x => x.obj.toString).getOrElse(""))

  val route: Route = logRequestResponse(LoggingMagnet(printRequestMethodAndResponseStatus)) {
    path("coordinationsTransform") {
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
