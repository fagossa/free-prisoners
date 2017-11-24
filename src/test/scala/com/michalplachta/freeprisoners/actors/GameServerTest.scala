package com.michalplachta.freeprisoners.actors

import akka.actor.{ActorSelection, ActorSystem, Props}
import akka.testkit.TestKit
import com.michalplachta.freeprisoners.PrisonersDilemma.{Guilty, Prisoner}
import com.michalplachta.freeprisoners.actors.GameServer.{
  GetGameId,
  GetSavedDecision,
  SaveDecision
}
import com.michalplachta.freeprisoners.actors.ServerCommunication._
import org.scalatest.{AsyncWordSpecLike, BeforeAndAfterAll, Matchers}

class GameServerTest
    extends TestKit(ActorSystem("gameServerTest"))
    with AsyncWordSpecLike
    with Matchers
    with BeforeAndAfterAll {
  "GameServer actor" should {
    "return the same gameId for player and opponent" in {
      val player = Prisoner("Player")
      val opponent = Prisoner("Opponent")
      val server = createServer()
      for {
        playerGameId <- askServer(server, GetGameId(player, opponent))
        opponentGameId <- askServer(server, GetGameId(opponent, player))
      } yield playerGameId should equal(opponentGameId)
    }

    "return different gameIds for the same match is created twice" in {
      val player = Prisoner("Player")
      val opponent = Prisoner("Opponent")
      val server = createServer()
      for {
        firstGameId <- askServer(server, GetGameId(player, opponent))
        secondGameId <- askServer(server, GetGameId(player, opponent))
      } yield firstGameId shouldNot equal(secondGameId)
    }

    "save the decision of the player" in {
      val player = Prisoner("Player")
      val opponent = Prisoner("Opponent")
      val server = createServer()
      for {
        gameId <- askServer(server, GetGameId(player, opponent))
        _ <- tellServer(server, SaveDecision(gameId, player, Guilty))
        decision <- askServer(server, GetSavedDecision(gameId, player))
      } yield decision should contain(Guilty)
    }

    "save the decision of the player just for one game" in {
      val player = Prisoner("Player")
      val opponent = Prisoner("Opponent")
      val server = createServer()

      for {
        gameId <- askServer(server, GetGameId(player, opponent))
        _ <- tellServer(server, SaveDecision(gameId, player, Guilty))
        differentGameId <- askServer(server, GetGameId(player, opponent))
        decision <- askServer(server, GetSavedDecision(differentGameId, player))
      } yield decision should be(None)
    }
  }

  override def afterAll(): Unit = system.terminate()

  private def createServer() =
    ActorSelection(system.actorOf(Props[GameServer]), "/")
}
