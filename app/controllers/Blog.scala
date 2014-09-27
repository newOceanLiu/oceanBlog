package controllers

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import scala.concurrent.Future
import reactivemongo.api._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import models._
import models.JsonFormats._

object Blog extends Controller with MongoController{
  // todo: change permission level as a parameter instead of hardcoded
  // todo: add pagination info to get preview of different page
  // todo: setup log system
  private def previews: JSONCollection = db.collection[JSONCollection]("preview")
  private def blogs: JSONCollection = db.collection[JSONCollection]("blog")

  def preview = Action.async {
    val cursor: Cursor[BlogPreview] = previews.find(Json.obj("permission" -> 0)).cursor[BlogPreview]
    val previewList: Future[List[BlogPreview]] = cursor.collect[List]()
    previewList.map {preview =>
     Ok(Json.toJson(preview))
    }
  }

  def create = Action.async(parse.json) { request =>
    request.body.validate[Blog].map { blog =>
      val blogInsert = blogs.insert(blog)
      val previewInsert = previews.insert(blog)
      for {
        blogError <- blogInsert
        previewError <- previewInsert
      } yield {
        if(blogError.ok && previewError.ok) Ok("This blog is created!")
        else InternalServerError("Failed to create this blog!")
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def get(blogId: Long) = Action.async {
    val cursor: Cursor[Blog] = blogs.find(Json.obj("id" -> blogId)).cursor[Blog]
    val blogList: Future[List[Blog]] = cursor.collect[List]()
    blogList.map { blog =>
      Ok(Json.toJson(blog))
    }
  }

  // todo: should return blog not found if blog of the id does not found
  def remove(blogId: Long) = Action.async{
    val selector = Json.obj("id" -> blogId)
    val blogRemove = blogs.remove(selector)
    val previewRemove = previews.remove(selector)
    for {
      blogError <- blogRemove
      previewError <- previewRemove
    } yield {
      if(blogError.ok && previewError.ok) Ok("This blog is deleted!")
      else InternalServerError("Failed to remove this blog!")
    }
  }

  def update(blogId: Long) = Action.async(parse.json) { request =>
    request.body.validate[Blog].map { blog =>
      val selector = Json.obj("id" -> blogId)
      val modifier = Json.obj("$set" -> blog)
      val blogUpdate = blogs.update(selector, modifier)
      val previewUpdate = previews.update(selector, modifier)
      for {
        blogError <- blogUpdate
        previewError <- previewUpdate
      } yield {
        if(blogError.ok && previewError.ok) Ok("This blog is updated!")
        else InternalServerError("Failed to update this blog!")
      }

    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }
}
