package models

case class Blog(
  title: Option[String],
  created: Option[Long],
  lastModified: Option[Long],
  viewCount: Option[Int],
  commentCount: Option[Int],
  content: Option[String],
  tag: Option[List[String]],
  vote: Option[Int],
  id: Long,
  permission: Option[Int]
)

