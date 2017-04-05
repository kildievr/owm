package ru.rustam.owm

import akka.actor.Actor
import org.json4s.{DefaultFormats, Formats}
import org.osgeo.proj4j.{CRSFactory, CoordinateReferenceSystem, CoordinateTransformFactory, ProjCoordinate}
import spray.http.StatusCodes.OK
import spray.httpx.Json4sJacksonSupport
import spray.routing.RequestContext

object Transformer {
  private val crsFactory = new CRSFactory()
  private val ctFactory  = new CoordinateTransformFactory()

  def createCRS(crsSpec: String): CoordinateReferenceSystem =
    if (crsSpec.indexOf("+") >= 0 || crsSpec.indexOf("=") >= 0)
      crsFactory.createFromParameters("Anon", crsSpec)
    else
      crsFactory.createFromName(crsSpec)

  def transform(x: String, y: String, proj4from: String, proj4to: String): Coordinates = {
    val crsFrom = createCRS(proj4from)
    val crsTo = createCRS(proj4to)
    val fromCoordinates = new ProjCoordinate(x.toDouble, y.toDouble)
    val trans = ctFactory.createTransform(crsFrom, crsTo)
    val toCoordinates = new ProjCoordinate()
    trans.transform(fromCoordinates, toCoordinates)
    Coordinates(toCoordinates.x, toCoordinates.y)
  }
}

class Transformer extends Actor with Json4sJacksonSupport {
  override def receive: Receive = {
    case (x: String, y: String, proj4from: String, proj4to: String, ctx: RequestContext) =>
      try {
        val res = Transformer.transform(x, y, proj4from, proj4to)
        ctx.complete(OK, TransformResponse(res))
      } catch {
        case th: Throwable =>
          ctx.complete(OK, TransformResponse(th.getMessage))
      }
  }

  override implicit def json4sJacksonFormats: Formats = DefaultFormats
}
