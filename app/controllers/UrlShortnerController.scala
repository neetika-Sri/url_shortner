package controllers

import javax.inject._
import model.UrlMap
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class UrlShortnerController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `POST` request with
    * a path of `/create_new    `.
    */
  def saveUrls = Action { implicit request: Request[AnyContent] =>
     val body = request.body.asText // TODO: fetch the request as JSON
     val url = trimUrl(body)

    if(!validateURl(url)){
         BadRequest(" URL not found or Not in a proper format")
     }else {
       val key = UrlMap.save(body.get)
       Ok(key)
     }
  }

  def fetchUrl(key:String) = Action{ implicit request: Request[AnyContent] =>
    val urlOpt = UrlMap.fetch(key)
    urlOpt match {
      case Some(url) => Redirect(url, SEE_OTHER)
      case None => NotFound("page not found ")  // TODO : Add Error Handler to handle service errors
    }
  }

  def validateURl(trimUrl: String): Boolean = {
    !trimUrl.equals("") && (trimUrl.startsWith("http://") || trimUrl.startsWith("https://"))
  }

  def trimUrl(url: Option[String]): String = {
    url.getOrElse("").trim().takeWhile( x => x != '\n')
  }
}

// case class Error(message: String)

//case class urlBody(url: String)