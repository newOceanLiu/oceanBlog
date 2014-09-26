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
  // todo: sort by created get the top ten
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

  // todo: auto generated ID
  // todo: hookup with preview to reflect change, same for delete and update in an ASYNC fashion
  // todo: need to handle the error from preview
  def create = Action.async(parse.json) { request =>
    request.body.validate[Blog].map { blog =>
      // `user` is an instance of the case class `models.User
      blogs.insert(blog).map { lastError =>
        print(s"Successfully inserted with LastError: $lastError")
        createPreview(blog)
        Ok("This blog is created!")
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  private def createPreview(blog: Blog) = {
    previews.insert(blog).map{ lastError =>
      print(s"Successfully inserted preview with LastError :$lastError")
    }
  }

  def get(blogId: Long) = Action.async {
    val cursor: Cursor[Blog] = blogs.find(Json.obj("id" -> blogId)).cursor[Blog]
    val blogList: Future[List[Blog]] = cursor.collect[List]()
    blogList.map { blog =>
      Ok(Json.toJson(blog))
    }
  }

  def remove(blogId: Long) = Action.async{
    val selector = Json.obj("id" -> blogId)
    blogs.remove(selector) map{_ =>
      removePreview(selector)
      Ok("This blog is deleted!")
    } recover {
      case e =>
        InternalServerError("Failed to delete this blog!")
    }
  }

  // todo: fix the generate preview from content
  private def removePreview(selector: JsObject) = {
    previews.remove(selector) map { _ =>
      print(s"Remove the Preview along with the blog!")
    }
  }

  // todo: need to handle the case where blog is not found: should not return success
  def update(blogId: Long) = Action.async(parse.json) { request =>
    request.body.validate[Blog].map { blog =>
      val selector = Json.obj("id" -> blogId)
      val modifier = Json.obj("$set" -> blog)
      blogs.update(selector, modifier).map { _=>
        updatePreview(selector, modifier)
        Ok("This blog is updated!")
      } recover {
        case e =>
          InternalServerError("Failed to update this blog!")
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  private def updatePreview(selector: JsObject, modifier: JsObject) = {
    previews.update(selector, modifier) map { _=>
      print(s"The preview for this blog is updated!")
    }
  }
}
