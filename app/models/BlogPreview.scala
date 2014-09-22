package models

case class BlogPreview(
  title: String,
  created: Long,
  lastModified: Long,
  viewCount: Int,
  commentCount: Int,
  preview: String,
  tag: List[String],
  vote: Int,
  id: Long,
  permission: Int
)

