package ru.rustam.owm.handlers

import akka.actor.Actor
import geotrellis.proj4.CRS
import org.json4s.{DefaultFormats, Formats}
import ru.rustam.owm.data.ConversionResponse
import ru.rustam.owm.handlers.Converter.convert
import spray.http.StatusCodes.OK
import spray.httpx.Json4sJacksonSupport
import spray.routing.RequestContext

object Converter {
  def convert(proj4string: String): Option[Int] = {
    val crs = CRS.fromString(proj4string)
    crs.epsgCode
  }
}

class Converter extends Actor with Json4sJacksonSupport {
  override implicit def json4sJacksonFormats: Formats = DefaultFormats

  override def receive: Receive = {
    case (proj4string: String, ctx: RequestContext) =>
      try {
        val res = convert(proj4string).getOrElse(throw new RuntimeException("There is no EPSG code for query proj4 string"))
        ctx.complete(OK, ConversionResponse(res))
      } catch {
        case th: Throwable =>
          ctx.complete(OK, ConversionResponse(th.getMessage))
      }
  }
}
