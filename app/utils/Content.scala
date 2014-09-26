package utils

object Content {
  def trimBlog(blog: Option[String]) : Option[String] = {
    blog match {
      case Some(blog) => return Option(blog.drop(100))
      case None => return blog
    }
  }
}
