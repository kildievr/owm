package ru.rustam.owm.data

trait Response[T] {
  val result: Option[T]
  val error: Option[String]
}
case class Coordinates(x: Double, y: Double)
object TransformResponse {
  def apply(coordinates: Coordinates): TransformResponse = TransformResponse(Some(coordinates), None)
  def apply(errorMsg: String): TransformResponse = TransformResponse(None, Some(errorMsg))
}
case class TransformResponse(result: Option[Coordinates], error: Option[String]) extends Response[Coordinates]
object ConversionResponse{
  def apply(result: Int): ConversionResponse = new ConversionResponse(Some(result), None)
  def apply(error: String): ConversionResponse = new ConversionResponse(None, Some(error))
}
case class ConversionResponse(result: Option[Int], error: Option[String]) extends Response[Int]
