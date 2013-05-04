package auth

import org.squeryl.KeyedEntity

case class SQUser(name: Option[String]) extends KeyedEntity[Long] {
  val id: Long = 0
}