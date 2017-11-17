package com.michalplachta.freeprisoners.apps

import akka.actor.{ActorSystem, Props}
import com.michalplachta.freeprisoners.actors.{GameServer, MatchmakingServer}

object MultiplayerServer extends App {
  private val system = ActorSystem("prisonersDilemma")
  system.actorOf(Props[MatchmakingServer], "matchmakingServer")
  system.actorOf(Props[GameServer], "gameServer")
  println("Server is running...")
}