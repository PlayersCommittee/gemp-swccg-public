package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Weather Vane (5_127 Dark) — bug #54: attached weapon must go Lost when character is bumped.
 */
public class Card_5_127_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
					put("vane", "5_127"); // Weather Vane (Dark)
					put("boba", "5_91"); // Boba Fett
					put("blaster", "5_179"); // Boba Fett's Blaster Rifle
					put("trooper", "1_194"); // Stormtrooper
				}},
				10,
				10,
				StartingSetup.DefaultLSGroundLocation, // Cloud City: Chasm Walkway
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void WeatherVaneStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Weather Vane
		 * Uniqueness: Unique
		 * Side: Dark
		 * Type: Effect
		 * Destiny: 4
		 * Game Text: Deploy on table. Any character here may be captured or rescued by a player's starship or vehicle
		 * 			controlling Cloud City during that player's control phase. Character here lost if new character arrives.
		 * 			Effect lost if Cloud City lost. (Immune to Alter.)
		 * Lore: Not a good place to hang around.
		 * Set: Cloud City
		 * Rarity: U
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("vane").getBlueprint();

		assertEquals("Weather Vane", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.EFFECT));
		assertEquals(4, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
		assertTrue(card.isImmuneToCardTitle(Title.Alter));
	}

	/**
	 * Bug #54: Character + attached weapon on Weather Vane bumped by a new character →
	 * prior character AND weapon go to Lost Pile (weapon does NOT vanish from game).
	 *
	 * Uses the same stacked-card selection and LoseCardsFromTableSimultaneouslyEffect that Card5_127
	 * now runs when RelocateToWeatherVaneResult fires ("Character here lost if new character arrives").
	 * Leaves Table: character + weapon leave simultaneously; owner may order Lost Pile placement.
	 */
	@Test
	public void WeatherVaneBumpedCharacterAttachedWeaponGoesLost() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var vane = scn.GetDSCard("vane");
		var boba = scn.GetDSCard("boba");
		var blaster = scn.GetDSCard("blaster");
		var trooper = scn.GetDSCard("trooper");
		var site = scn.GetLSStartingLocation(); // Chasm Walkway (Cloud City)

		scn.StartGame();

		scn.MoveCardsToDSSideOfTable(vane);
		scn.MoveCardsToLocation(site, boba, trooper);
		scn.AttachCardsTo(boba, blaster);

		assertEquals(Zone.ATTACHED, blaster.getZone());
		assertEquals(boba, blaster.getAttachedTo());
		assertTrue(boba.getCardsAttached().contains(blaster));

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());

		// Prior character already on Weather Vane with weapon (inactive stacked)
		scn.gameState().relocateCardAsStacked(boba, vane, false, true);
		assertEquals(Zone.STACKED, boba.getZone());
		assertEquals(vane, boba.getStackedOn());
		assertTrue(boba.getCardsAttached().contains(blaster));

		// New character arrives on Weather Vane
		scn.gameState().relocateCardAsStacked(trooper, vane, false, true);
		assertEquals(Zone.STACKED, trooper.getZone());

		// Same selection Card5_127 uses on RelocateToWeatherVaneResult
		Collection<PhysicalCard> charactersToLose = Filters.filter(
				scn.gameState().getStackedCards(vane), scn.game(), Filters.not(trooper));
		assertTrue(charactersToLose.contains(boba));
		assertFalse(charactersToLose.contains(trooper));

		int dsLostBefore = scn.GetDSLostPileCount();

		// Same effect Card5_127 now appends (LoseCardsFromTableSimultaneouslyEffect)
		var loseAction = new TopLevelGameTextAction(vane, scn.DS, vane.getCardId());
		loseAction.setText("Make character lost");
		loseAction.appendEffect(new LoseCardsFromTableSimultaneouslyEffect(loseAction, charactersToLose, false, true));
		var awaiting = (CardActionSelectionDecision) scn.userFeedback().getAwaitingDecision(scn.DS);
		String[] actionIdsBefore = scn.DSGetADParam("actionId");
		int newIndex = actionIdsBefore == null ? 0 : actionIdsBefore.length;
		awaiting.addAction(loseAction);
		scn.DSDecided(String.valueOf(newIndex));
		scn.PassAllResponses();

		for (int i = 0; i < 6; i++) {
			if (!(scn.DSDecisionAvailable("Lost Pile") || scn.DSDecisionAvailable("lost")
					|| scn.DSDecisionAvailable("Choose card") || scn.DSDecisionAvailable("place"))) {
				break;
			}
			if (scn.DSHasCardChoiceAvailable(boba)) {
				scn.DSChooseCard(boba);
			} else if (scn.DSHasCardChoiceAvailable(blaster)) {
				scn.DSChooseCard(blaster);
			} else {
				scn.DSPass();
			}
			scn.PassAllResponses();
		}
		scn.PassAllResponses();

		assertEquals(Zone.STACKED, trooper.getZone());
		assertEquals(vane, trooper.getStackedOn());

		assertInZone(Zone.LOST_PILE, boba);
		assertInZone(Zone.LOST_PILE, blaster);
		assertTrue(scn.GetDSLostPileCount() >= dsLostBefore + 2);
	}
}
