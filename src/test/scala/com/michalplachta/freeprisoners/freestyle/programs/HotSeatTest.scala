package com.michalplachta.freeprisoners.freestyle.programs

import com.michalplachta.freeprisoners.PrisonersDilemma._
import com.michalplachta.freeprisoners.freestyle.testhandlers.PlayerTestHandler
import com.michalplachta.freeprisoners.states.PlayerState.PlayerStateA
import com.michalplachta.freeprisoners.states.{FakePrisoner, PlayerState}
import org.scalatest.{Matchers, WordSpec}
import freestyle._
import freestyle.implicits._

class HotSeatTest extends WordSpec with Matchers with PlayerTestHandler {
  "Hot Seat (Freestyle) program" should {
    "question 2 prisoners and give verdicts" in {
      val blamingPrisoner = FakePrisoner(Prisoner("Blaming"), Guilty)
      val silentPrisoner = FakePrisoner(Prisoner("Silent"), Silence)
      val inputState =
        PlayerState(Set(blamingPrisoner, silentPrisoner), Map.empty, Map.empty)

      val result: PlayerState =
        HotSeat.program.interpret[PlayerStateA].runS(inputState).value

      result.verdicts should be(
        Map(blamingPrisoner.prisoner -> Verdict(0),
            silentPrisoner.prisoner -> Verdict(3)))
    }
  }
}
