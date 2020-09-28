package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 11
 * Type: Effect
 * Title: Squadron Assignments (V)
 */
public class Card211_052 extends AbstractNormalEffect {
    public Card211_052() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Squadron Assignments", Uniqueness.UNIQUE);
        setLore("Rebel starfighter squadrons take great pride in their proficiency and dedication. Alliance pilots train relentlessly until man and machine function as one.");
        setGameText("Deploy on table. During your deploy phase, may reveal one unpiloted Red or Rogue squadron starfighter (or vehicle) from hand to take its matching pilot character (or vice versa) from Reserve Deck and deploy both simultaneously; reshuffle. (Immune to Alter.)");
        addIcons(Icon.VIRTUAL_SET_11, Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        Filter redOrRogueSquadronThing = Filters.or(
                Filters.Red_Squadron_starfighter,
                Filters.Red_Squadron_vehicle,
                Filters.Rogue_Squadron_starfighter,
                Filters.Rogue_Squadron_vehicle);

        final Filter unpilotedRedOrRogueSquadronThing = Filters.and(Filters.unpiloted, redOrRogueSquadronThing);

        Filter filter = Filters.and(Filters.or(Filters.pilot, unpilotedRedOrRogueSquadronThing), Filters.isUniquenessOnTableNotReached);

        GameTextActionId gameTextActionId = GameTextActionId.SQUADRON_ASSIGNMENTS__DEPLOY_MATCHING_STARFIGHTER_OR_PILOT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal pilot, ship, or vehicle from hand");
            action.setActionMsg("Reveal a pilot or a red/rogue squadron vehicle or ship from hane");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));

            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (Filters.character.accepts(game, selectedCard)) {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching unpiloted red or rogue squadron starfighter or vehicle from Reserve Deck and deploy both simultaneously");

                                Filter matchingStarshipOrVehicle = Filters.or(Filters.matchingVehicle(selectedCard),Filters.matchingStarship(selectedCard));
                                searchFilter = Filters.and(unpilotedRedOrRogueSquadronThing, matchingStarshipOrVehicle);
                            }
                            else {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching pilot from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.matchingPilot(selectedCard);
                            }
                            // Perform result(s)
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}