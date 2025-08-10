package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_7_296_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
				}},
				10,
				10,
				StartingSetup.DefaultLSGroundLocation,
				StartingSetup.CarbonChamberObjective,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void CarbonChamberTestingStatsAndKeywordsAreCorrect() {
		/**
		 * Front Title: Carbon Chamber Testing
		 * Back Title : My Favorite Decoration
		 * Side: Dark
		 * Type: Objective
		 * Destiny: 0/7
		 * Front Game Text: Deploy Carbonite Chamber, Carbonite Chamber Console and Security Tower with a Rebel
		 * 			(opponent's choice) from opponent's Reserve Deck (if possible) imprisoned there. While this side up,
		 * 			once during each of your deploy phases, you may deploy from Reserve Deck one Audience Chamber,
		 * 			Docking Bay 94 or East Platform; reshuffle. You may not play Dark Deal. Flip this card if you move a
		 * 			frozen captive to Audience Chamber (or if no Rebel was in opponent's Reserve Deck at start of game).
		 * Back Game Text : While this side up, your aliens and starships are immune to attrition < 4 and, once during
		 * 			each of your control phases, you may retrieve 1 Force. While you have a frozen captive at Audience
		 * 			Chamber, Scum And Villainy is immune to Alter and during your deploy phase, you may deploy Scum And
		 * 			Villainy from Reserve Deck; reshuffle. Place out of play if there are no frozen captives on table
		 * 			(unless no Rebel was in opponent's Reserve Deck at start of game).
		 * Set: Special Edition
		 * Rarity: R
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("testing").getBlueprint();

		assertEquals(Title.Carbon_Chamber_Testing, card.getTitle());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(0, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.SPECIAL_EDITION));

		var back = scn.GetDSCard("testing").getOtherSideBlueprint();

		assertEquals(Title.My_Favorite_Decoration, back.getTitle());
		assertEquals(Side.DARK, back.getSide());
		assertEquals(7, back.getDestiny(), scn.epsilon);
		assertEquals(1, back.getIconCount(Icon.SPECIAL_EDITION));
	}


}
