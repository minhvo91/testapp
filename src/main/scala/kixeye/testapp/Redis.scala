package kixeye.testapp

import com.typesafe.scalalogging.LazyLogging
import kixeye.testapp.routes.Leaderboard.LeaderboardEntry
import redis.RedisClient

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object Redis extends LazyLogging {

  val random: Random = scala.util.Random

  def init(client: RedisClient): Future[Long] = {

    val scores: Seq[(Double, String)] = (1 to 4).foldLeft(Seq[(Double, String)]()) { (seq, userId) =>
      seq :+ random.nextInt().toDouble -> userId.toString
    }

    logger.info(s"generated scores: $scores")

    client.zadd("testapp:leaderboard", scores:_*)
  }

  def getLeaderboard(client: RedisClient)(implicit context: ExecutionContext): Future[Seq[LeaderboardEntry]] = {
    client.zrangeWithscores("testapp:leaderboard", 0, 1).map { entries =>
      entries.map {
        case (userId, score) => LeaderboardEntry(userId.utf8String.toInt, score.toInt)
      }
    }
  }

}