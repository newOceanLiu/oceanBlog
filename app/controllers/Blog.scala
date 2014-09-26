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
  def preview = Action.async {
    def collection: JSONCollection = db.collection[JSONCollection]("preview")
    val cursor: Cursor[BlogPreview] = collection.find(Json.obj("permission" -> 0)).cursor[BlogPreview]
    val previewList: Future[List[BlogPreview]] = cursor.collect[List]()
    previewList.map {preview =>
     Ok(Json.toJson(preview))
    }
  }

  // todo: auto generated ID
  // todo: add option so not every field is necessary
  // todo: hookup with preview to reflect change, same for delete and update
  def create = Action.async(parse.json) { request =>
    def collection: JSONCollection = db.collection[JSONCollection]("blog")
    request.body.validate[Blog].map { blog =>
      // `user` is an instance of the case class `models.User
      collection.insert(blog).map { lastError =>
        print(s"Successfully inserted with LastError: $lastError")
        Ok("This blog is created!")
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def get(blogId: Long) = Action.async {
    def collection: JSONCollection = db.collection[JSONCollection]("blog")
    val cursor: Cursor[Blog] = collection.find(Json.obj("id" -> blogId)).cursor[Blog]
    val blogs: Future[List[Blog]] = cursor.collect[List]()
    blogs.map { blog =>
      Ok(Json.toJson(blog))
    }
  }

  def remove(blogId: Long) = Action.async{
    def collection: JSONCollection = db.collection[JSONCollection]("blog")
    val selector = Json.obj("id" -> blogId)
    collection.remove(selector) map{_ =>
      Ok("This blog is deleted!")
    } recover {
      case e =>
        InternalServerError("Failed to delete this blog!")
    }
  }

  // todo: need to handle the case where blog is not found: should not return success
  def update(blogId: Long) = Action.async(parse.json) { request =>
    def collection: JSONCollection = db.collection[JSONCollection]("blog")
    request.body.validate[Blog].map { blog =>
      val selector = Json.obj("id" -> blogId)
      val modifier = Json.obj("$set" -> blog)
      collection.update(selector, modifier).map { _=>
        Ok("This blog is updated!")
      } recover {
        case e =>
          InternalServerError("Failed to update this blog!")
      }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }
}
