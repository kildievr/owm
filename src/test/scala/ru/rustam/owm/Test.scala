package ru.rustam.owm

import java.net.URLEncoder

import akka.actor.ActorSystem
import org.specs2.mutable.Specification
import ru.rustam.owm.http.ApiService
import spray.http.HttpEntity
import spray.http.MediaTypes._
import spray.testkit.Specs2RouteTest

class Test extends Specification with Specs2RouteTest with ApiService {

  def actorRefFactory: ActorSystem = system

  private def encode(s: String) = URLEncoder.encode(s, "UTF-8")

  "Service" should {
    def transform(sX: String = "-142.0", expected: String = """{"result":{"x":500000.0,"y":916085.5081033339}}""") = {
      val x = encode(sX)
      val y = encode("56.50833333333333")
      val proj4from = encode("+proj=longlat +datum=NAD27 +to_meter=0.3048006096012192")
      val proj4to = encode("ESRI:26732")
      val post = Post("/coordinatesTransform", HttpEntity(`application/x-www-form-urlencoded`,
        s"x=$x&y=$y&proj4from=$proj4from&proj4to=$proj4to"))
      post ~> route ~> check {
        val result = responseAs[String]
        result mustEqual expected
        handled must beTrue
      }
    }

    "transform coordinates" in {
      transform("-123.sad0", """{"error":"For input string: \"-123.sad0\""}""")
    }

    def convert(proj4: String = "+proj=longlat +datum=WGS84 +no_defs", expected: String = """{"result":4326}""") = {
      val proj4string = encode(proj4)
      val get = Get(s"/proj4ToEPSG?proj4string=$proj4string")
      get ~> route ~> check {
        val result = responseAs[String]
        result mustEqual expected
        handled must beTrue
      }
    }

    "convert proj4 string to EPSG code" in {
      convert()
    }
    "convert invalid proj4 string to EPSG code" in {
      convert("+proj=tmerc +lat_0=40 +lon_0=-76.33333333333333 +k=0.999966667 +x_0=152400.3048006096 +y_0=0 +datum=NAD27 +units=us-ft +no_defs ",
      """{"error":"There is no EPSG code for query proj4 string"}""")
    }
  }
}
