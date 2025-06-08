package error

import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router
import play.twirl.api.Html

import javax.inject._
import scala.concurrent._

@Singleton
class CustomErrorHandler @Inject() (
                                       env: Environment,
                                       config: Configuration,
                                       sourceMapper: OptionalSourceMapper,
                                       router: Provider[Router]
                                   ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

    override def onClientError(
                                  request: RequestHeader,
                                  statusCode: Int,
                                  message: String
                              ): Future[Result] = {

        val isAjax = request.headers.get("X-Requested-With").contains("XMLHttpRequest")

        statusCode match {
            case 404 =>
                val html: Html =
                    if (isAjax) Html("<p>Halaman tidak ditemukan.</p>")
                    else views.html.main("404 Not Found")(Html("<h1>404 - Halaman tidak ditemukan</h1>"))

                Future.successful(NotFound(html))
            case _ =>
                Future.successful(Status(statusCode)("Client error: " + message))
        }
    }
}