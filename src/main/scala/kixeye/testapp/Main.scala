package kixeye.testapp

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import kixeye.testapp.routes.Leaderboard
import redis.RedisClient

import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

object Main extends HttpApp with LazyLogging {

  private implicit val system: ActorSystem = ActorSystem("leaderboard")
  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  private implicit val httpRequestTimeout: Timeout = Timeout(4 seconds)
  private implicit val httpRoutesMaterializer: ActorMaterializer = ActorMaterializer()

  private val config: Config = ConfigFactory.load()
  private val redisClient = RedisClient(config.getString("redis.host"), config.getInt("redis.port"))
  private val requestHandler: ActorRef = system.actorOf(RequestHandler.props(redisClient))

  def main(args: Array[String]): Unit = {
    Redis.init(redisClient).onComplete {
      case Success(recordsUpdated) =>
        logger.info("Loaded data into Redis")
        Main.startServer(config.getString("http.host"), config.getInt("http.port"))
      case Failure(error) =>
        logger.error("Error loading data into Redis, shutting down", error)
        System.exit(1)
    }
  }

  override protected def routes: Route = Leaderboard(requestHandler).routes

  override protected def waitForShutdownSignal(system: ActorSystem)(implicit ec: ExecutionContext): Future[Done] = {
    Promise[Done].future
  }
}