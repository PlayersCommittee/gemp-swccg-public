package com.gempukku.swccgo.cards.set5.light;

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
 * Weather Vane (5_30 Light) — bug #54: same bump/attached-weapon path as Dark 5_127.
 */
public class Card_5_030_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("vane", "5_30"); // Weather Vane (Light)
					put("chewie", "2_3"); // Chewbacca
					put("bowcaster", "8_86"); // Chewie's Bowcaster
					put("luke", "1_19"); // Luke Skywalker
				}},
				new HashMap<>()
				{{
				}},
				10,
				10,
				StartingSetup.DefaultLSGroundLocation,
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
		 * Side: Light
		 * Type: Effect
		 * Destiny: 4
		 * Game Text: Deploy on table. Any character here may be captured or rescued by a player's starship or vehicle
		 * 			controlling Cloud City during that player's control phase. Character here lost if new character arrives.
		 * 			Effect lost if Cloud City lost. (Immune to Alter.)
		 * Lore: The metal rods extending from the bottom of Cloud City are part of the city's flotation system...
		 * Set: Cloud City
		 * Rarity: U
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("vane").getBlueprint();

		assertEquals("Weather Vane", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.isCardType(CardType.EFFECT));
		assertEquals(4, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
		assertTrue(card.isImmuneToCardTitle(Title.Alter));
	}

	@Test
	public void WeatherVaneBumpedCharacterAttachedWeaponGoesLost() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var vane = scn.GetLSCard("vane");
		var chewie = scn.GetLSCard("chewie");
		var bowcaster = scn.GetLSCard("bowcaster");
		var luke = scn.GetLSCard("luke");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSSideOfTable(vane);
		scn.MoveCardsToLocation(site, chewie, luke);
		scn.AttachCardsTo(chewie, bowcaster);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingLSControlPhaseActions());

		scn.gameState().relocateCardAsStacked(chewie, vane, false, true);
		assertTrue(chewie.getCardsAttached().contains(bowcaster));

		scn.gameState().relocateCardAsStacked(luke, vane, false, true);

		Collection<PhysicalCard> charactersToLose = Filters.filter(
				scn.gameState().getStackedCards(vane), scn.game(), Filters.not(luke));
		assertTrue(charactersToLose.contains(chewie));
		assertFalse(charactersToLose.contains(luke));

		int lsLostBefore = scn.GetLSLostPileCount();

		var loseAction = new TopLevelGameTextAction(vane, scn.LS, vane.getCardId());
		loseAction.setText("Make character lost");
		loseAction.appendEffect(new LoseCardsFromTableSimultaneouslyEffect(loseAction, charactersToLose, false, true));
		var awaiting = (CardActionSelectionDecision) scn.userFeedback().getAwaitingDecision(scn.LS);
		String[] actionIdsBefore = scn.LSGetADParam("actionId");
		int newIndex = actionIdsBefore == null ? 0 : actionIdsBefore.length;
		awaiting.addAction(loseAction);
		scn.LSDecided(String.valueOf(newIndex));
		scn.PassAllResponses();

		for (int i = 0; i < 6; i++) {
			if (!(scn.LSDecisionAvailable("Lost Pile") || scn.LSDecisionAvailable("lost")
					|| scn.LSDecisionAvailable("Choose card") || scn.LSDecisionAvailable("place"))) {
				break;
			}
			if (scn.LSHasCardChoiceAvailable(chewie)) {
				scn.LSChooseCard(chewie);
			} else if (scn.LSHasCardChoiceAvailable(bowcaster)) {
				scn.LSChooseCard(bowcaster);
			} else {
				scn.LSPass();
			}
			scn.PassAllResponses();
		}
		scn.PassAllResponses();

		assertEquals(Zone.STACKED, luke.getZone());
		assertInZone(Zone.LOST_PILE, chewie);
		assertInZone(Zone.LOST_PILE, bowcaster);
		assertTrue(scn.GetLSLostPileCount() >= lsLostBefore + 2);
	}
}
