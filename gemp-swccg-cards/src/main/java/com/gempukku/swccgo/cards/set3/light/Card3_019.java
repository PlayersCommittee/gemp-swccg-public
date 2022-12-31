package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.BattleInitiatedResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Shawn Valdez
 */
public class Card3_019 extends AbstractRebel {
    public Card3_019() {
        super(Side.LIGHT, 3, 3, 1, 2, 4, "Shawn Valdez", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.U1);
        setLore("Experienced evacuation officer. Charismatic leader of Echo Base troopers. Trained to expedite the evacuation of Rebel installations with maximum efficiency. Poetic musician.");
        setGameText("Whenever you just initiated a battle at same site as Shawn, your troopers at adjacent sites who have not already battled this turn may immediately move to same site (as a regular move).");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.MUSICIAN, Keyword.ECHO_BASE_TROOPER);
        addPersona(Persona.SHAWN);
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

        if(GameConditions.canSpot(game, self, Filters.Shawn)) {
            final PhysicalCard shawn = Filters.findFirstActive(game, self, Filters.Shawn);

            // Check condition(s)
            if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameSite(shawn)) &&
                    TriggerConditions.battleInitiated(game, effectResult, self.getOwner()) &&
                    GameConditions.canSpot(game, self, troopersAtSitesNotAboardSomething)) {

                BattleInitiatedResult battleInitiatedResult = (BattleInitiatedResult) effectResult;
                final PhysicalCard currentLocation = battleInitiatedResult.getLocation();
                Filter currentLocationFilter = Filters.sameCardId(currentLocation);
                Filter yourTroopersAtSitesNotAboardAnything = Filters.and(Filters.your(self), troopersAtSitesNotAboardSomething);

                // We have the filter of valid troopers. However, we also need to check which of them is actually
                // capable of moving (based on game-state)

                // Of those which are still valid, allow the user to click them (one at a time)
                List<PhysicalCard> validTroopers = getValidTroopers(game, playerId, currentLocationFilter, self, yourTroopersAtSitesNotAboardAnything);
                if (!validTroopers.isEmpty()) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Move adjacent trooper here");
                    action.setActionMsg("Move adjacent trooper here");

                    // Allow user to choose a single trooper at a time. This special function allows picking the trooper
                    // and queues up additional 'pick' actions as add-on-effects
                    ChooseCardOnTableEffect chooseCardOnTableEffect = buildAnotherChooseCardEffect(game, self, yourTroopersAtSitesNotAboardAnything, action, playerId, currentLocation);

                    // Choose target(s) - Troopers which can move
                    action.appendTargeting(
                            chooseCardOnTableEffect
                    );

                    actions.add(action);

                }

            }
        }


        return actions;

    }


    /**
     * Build a list of valid troopers-to-move based on the current game state.
     */
    private List<PhysicalCard> getValidTroopers(SwccgGame game, String playerId, Filter currentLocationFilter, PhysicalCard self, Filter trooperFilter) {

        List<PhysicalCard> validTroopers = new ArrayList<>();

        // Figure out which of the troopers can actually battle and move (if any)
        Collection<PhysicalCard> troopers = Filters.filterActive(game, self, Filters.and(Filters.your(self), trooperFilter));
        if (!troopers.isEmpty()) {
            for (PhysicalCard trooper : troopers) {

                boolean hasParticipatedInBattle = GameConditions.hasParticipatedInBattleThisTurn(game, trooper);
                boolean movableAsRegularMove = Filters.movableAsRegularMove(playerId, false, 0, false, currentLocationFilter).accepts(game, trooper);
                if (!hasParticipatedInBattle && movableAsRegularMove) {
                    validTroopers.add(trooper);
                }
            }
        }


        return validTroopers;
    }


    /**
     * Build a new action which allows us to move troopers. If no additional troopers are available
     * then just return null.
     */
    private static ChooseCardOnTableEffect buildAnotherChooseCardEffect(final SwccgGame game, final PhysicalCard self, final Filter troopersAtSitesNotAboardSomething, final OptionalGameTextTriggerAction action, final String playerId, final PhysicalCard currentLocation) {

        // Figure out which of the troopers can actually battle and move (if any)
        Collection<PhysicalCard> troopers = Filters.filterActive(game, self, Filters.and(Filters.your(self), troopersAtSitesNotAboardSomething));
        if (!troopers.isEmpty()) {
            List<PhysicalCard> validTroopers = new ArrayList<PhysicalCard>();
            for (PhysicalCard trooper : troopers) {

                boolean hasParticipatedInBattle = GameConditions.hasParticipatedInBattleThisTurn(game, trooper);
                boolean movableAsRegularMove = Filters.movableAsRegularMove(playerId, false, 0, false, Filters.sameCardId(currentLocation)).accepts(game, trooper);
                if (!hasParticipatedInBattle && movableAsRegularMove) {
                    validTroopers.add(trooper);
                }
            }

            // We have the list of valid troopers. If we still have valid targets, allow the user to click them (one at a time)
            if (!validTroopers.isEmpty()) {
                Integer minimumTroopersToSelect = 0; // optional
                ChooseCardOnTableEffect chooseCardOnTableEffect = new ChooseCardOnTableEffect(action, playerId, "Choose trooper to move", Filters.in(validTroopers), minimumTroopersToSelect) {
                    @Override
                    protected void cardSelected(PhysicalCard trooperToMove) {

                        action.addAnimationGroup(trooperToMove);
                        action.setActionMsg("Have " + GameUtils.getCardLink(trooperToMove) + " make a regular move");

                        // Perform result(s)
                        action.appendEffect(
                                new MoveCardAsRegularMoveEffect(action, playerId, trooperToMove, false, false, currentLocation));

                        // The rules team has ruled that each of the "move" actions is a separate action.
                        // However, all of these actions occur sequentially (without giving the opponent the chance to
                        // take an action in between!)
                        //
                        // To do this, we dynamically append an additional "ChooseCardOnTableEffect" if there are more
                        // valid targets. This happens repeatedly until the user cancels or we are out of targets
                        //
                        // Since these are all effects, the opponent won't be allowed to interrupt them
                        action.appendEffect(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Optionally, add on another "choose card" instance
                                        ChooseCardOnTableEffect bonusEffect = buildAnotherChooseCardEffect(game, self, troopersAtSitesNotAboardSomething, action, playerId, currentLocation);
                                        if (bonusEffect != null) {
                                            action.appendEffect(bonusEffect);
                                        }
                                    }
                                }
                        );
                    }
                };
                return chooseCardOnTableEffect;

            }
        }


        // No troopers to continue moving. No more 'bonus' actions
        return null;
    }

}
