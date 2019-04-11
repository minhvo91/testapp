package kixeye.testapp

import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.scalalogging.LazyLogging
import kixeye.testapp.routes.Leaderboard.GetLeaderboard
import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object RequestHandler {
  def props(redisClient: RedisClient): Props = Props(RequestHandler(redisClient))
}

case class RequestHandler(redis: RedisClient) extends Actor with LazyLogging {

  override def receive: Receive = {
    case GetLeaderboard => sendLeaderboard(sender())
  }

  override def unhandled(message: Any): Unit = {
    logger.info(s"Unhandled message $message")
    super.unhandled(message)
  }

  private def sendLeaderboard(replyTo: ActorRef): Unit = {
    logger.debug("getting leaderboard from redis")
    Redis.getLeaderboard(redis).onComplete {
      case Success(leaderboardEntries) =>
        logger.debug(s"got leaderboard: $leaderboardEntries")
        replyTo ! leaderboardEntries
      case Failure(error) =>
        logger.error("error getting leaderboard from Redis", error)
    }
  }
}