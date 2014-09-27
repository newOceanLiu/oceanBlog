package utils

object Content {
  def trimBlog (blog: Option[String]) : Option[String] = {
    blog match {
      case Some(text) => return Option(text.drop(100))
      case None => return blog
    }
  }
}
