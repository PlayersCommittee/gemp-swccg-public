package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleInitiatedResult;

import java.util.*;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Shawn Valdez
 */
public class Card3_019 extends AbstractRebel {
    public Card3_019() {
        super(Side.LIGHT, 3, 3, 1, 2, 4, "Shawn Valdez", Uniqueness.UNIQUE);
        setLore("Experienced evacuation officer. Charismatic leader of Echo Base troopers. Trained to expedite the evacuation of Rebel installations with maximum efficiency. Poetic musician.");
        setGameText("Whenever you just initiated a battle at same site as Shawn, your troopers at adjacent sites who have not already battled this turn may immediately move to same site (as a regular move).");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.MUSICIAN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();


        // Whenever you just initiated a battle at same site as Shawn, your troopers at adjacent sites who have
        // not already battled this turn may immediately move to same site (as a regular move).

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter adjacentSiteFilter = Filters.adjacentSite(self);
        Filter troopersAtAdjacentSites = Filters.and(Filters.at(adjacentSiteFilter), Filters.trooper, Filters.your(playerId), Filters.hasNotPerformedRegularMove);
        Filter troopersAtSitesNotAboardSomething = Filters.and(troopersAtAdjacentSites, Filters.not(Filters.aboardAnyStarship), Filters.not(Filters.aboardAnyVehicle));


        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self)) &&
                TriggerConditions.battleInitiated(game, effectResult, self.getOwner()) &&
                GameConditions.canSpot(game, self, troopersAtSitesNotAboardSomething)) {

            BattleInitiatedResult battleInitiatedResult = (BattleInitiatedResult) effectResult;
            final PhysicalCard currentLocation = battleInitiatedResult.getLocation();
            Filter currentLocationFilter = Filters.sameCardId(currentLocation);

            // Figure out which of the troopers can actually battle and move (if any)
            Collection<PhysicalCard> troopers = Filters.filterActive(game, self, Filters.and(Filters.your(self), troopersAtSitesNotAboardSomething));
            if (!troopers.isEmpty()) {
                List<PhysicalCard> validTroopers = new ArrayList<PhysicalCard>();
                for (PhysicalCard trooper : troopers) {

                    boolean hasParticipatedInBattle = GameConditions.hasParticipatedInBattleThisTurn(game, trooper);
                    boolean movableAsRegularMove = Filters.movableAsRegularMove(playerId, false, 0, false, currentLocationFilter).accepts(game, trooper);
                    if (!hasParticipatedInBattle && movableAsRegularMove) {
                        validTroopers.add(trooper);
                    }
                }

                // We have the list of valid troopers. If we still have them, all the user to click them (one at a time)
                if (!validTroopers.isEmpty()) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Move adjacent trooper here");
                    action.setActionMsg("Move adjacent trooper here");
                    action.setRepeatableTrigger(true);


                    // Choose target(s) - Troopers which can move
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose trooper to move", Filters.in(validTroopers)) {
                                @Override
                                protected void cardSelected(PhysicalCard rebel) {

                                    action.addAnimationGroup(rebel);
                                    action.setActionMsg("Have " + GameUtils.getCardLink(rebel) + " make a regular move");

                                    // Perform result(s)
                                    action.appendEffect(
                                            new MoveCardAsRegularMoveEffect(action, playerId, rebel, false, false, currentLocation));
                                }
                            }
                    );

                    actions.add(action);
                }

            }

        }


        return actions;

    }

}
