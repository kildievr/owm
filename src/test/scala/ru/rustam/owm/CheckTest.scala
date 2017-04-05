package ru.rustam.owm

import java.net.URLEncoder
import java.util.UUID._

import org.json4s.jackson.Serialization.{read, write}
import spray.http.HttpEntity
import spray.http.MediaTypes._
import akka.actor.ActorSystem
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest

import scala.concurrent.duration._

class CheckTest extends Specification with Specs2RouteTest with MyService {

  "Check test" should {
    "pass" in {
      val post1 = Post("/coordinationsTransform", HttpEntity(`application/x-www-form-urlencoded`, s"x=x&y=y&proj4from=proj4from&proj4to=proj4to"))
      post1 ~> route ~> check {
        val result = responseAs[String]
        println(s"response = $result")
        handled must beTrue
      }
    }
  }
}
