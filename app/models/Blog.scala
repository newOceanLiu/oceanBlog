package models

case class Blog(
  title: String,
  created: Long,
  lastModified: Long,
  viewCount: Int,
  commentCount: Int,
  content: String,
  tag: List[String],
  vote: Int,
  id: Long,
  permission: Int
)

