package kixeye.testapp.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives.{complete, get, _}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import kixeye.testapp.routes.Leaderboard.{GetLeaderboard, LeaderboardEntry}

import scala.concurrent.ExecutionContext
import scala.language.postfixOps


object Leaderboard {

  object GetLeaderboard

  case class LeaderboardEntry(userId: Int, score: Int)

}

case class Leaderboard(requestHandlerActor: ActorRef)
                      (implicit timeout: Timeout, ec: ExecutionContext)
  extends LazyLogging with FailFastCirceSupport {

  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: Exception =>
        extractUri { uri =>
          logger.error(s"Request to $uri could not be handled normally", e)
          complete(HttpResponse(InternalServerError, entity = e.getLocalizedMessage))
        }
    }

  val routes: Route = path("leaderboard") {
      get {
        logger.trace(s"HTTP request for leaderboard")
        onSuccess((requestHandlerActor ? GetLeaderboard).mapTo[Seq[LeaderboardEntry]]) { status =>
          logger.debug(s"Got status response $status for HTTP leaderboard request")
          complete(status.asJson.noSpaces)
        }

      }
    } ~ path("amiup") {
      get {
        complete("OK")
      }
    }
}