package controllers

import controllers.Assets.Asset
import javax.inject._
import play.api.{Environment, Mode}
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(ws: WSClient,
                               assets: Assets,
                               environment: Environment,
                               cc: ControllerComponents
                              )(implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def bundle(file: String): Action[AnyContent] = if (environment.mode == Mode.Dev) Action.async {
    ws.url(s"http://localhost:8080/bundles/$file").get().map { response =>
      val contentType = response.headers.get("Content-Type").flatMap(_.headOption).getOrElse("application/octet-stream")
      val headers = response.headers
        .toSeq.filter(p => List("Content-Type", "Content-Length").indexOf(p._1) < 0).map(p => (p._1, p._2.mkString))
      Ok(response.body).withHeaders(headers: _*).as(contentType)
    }
  } else {
    assets.at("/public/bundles", file)
  }

}
