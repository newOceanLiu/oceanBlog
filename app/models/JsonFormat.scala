package models

object JsonFormats {
  import play.api.libs.json.Json
  import utils.Content._

  implicit val previewFormat = Json.format[BlogPreview]
  implicit val blogFormat = Json.format[Blog]

  implicit def blog2preview(blog: Blog) : BlogPreview=
    new BlogPreview(
      blog.title,
      blog.created,
      blog.lastModified,
      blog.viewCount,
      blog.commentCount,
      trimBlog(blog.content),
      blog.tag,
      blog.vote,
      blog.id,
      blog.permission
    )
}
