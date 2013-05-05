package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.PrimitiveTypeMode.inTransaction
import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.AppDB

import org.squeryl.PrimitiveTypeMode._

import org.squeryl.dsl._

case class Party(var partyId: Long, var userId: Long) extends KeyedEntity[CompositeKey2[Long, Long]] {
  def id = compositeKey(partyId, userId)
}

object Party {
  
}