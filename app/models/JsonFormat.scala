package models

object JsonFormats {
  import play.api.libs.json.Json

  implicit val previewFormat = Json.format[BlogPreview]
  implicit val blogFormat = Json.format[Blog]
}
