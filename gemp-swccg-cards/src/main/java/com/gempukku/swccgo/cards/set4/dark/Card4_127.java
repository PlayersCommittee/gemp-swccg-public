package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.effects.RelocateFromLostInSpaceOrWeatherVaneToStarshipOrVehicle;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.CaptureCharacterFromLostInSpaceOrWeatherVaneEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByWeaponsAsIfPresentModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByWeaponsLikeStarfighterModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDefenseValueModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Lost In Space
 */
public class Card4_127 extends AbstractImmediateEffect {
    public Card4_127() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Lost In Space", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Congratulations on purchasing the TIE/ln starfighter by Sienar. Equipped with a jettison device and distress beacon, it should provide you with years of worry-free subjugation.");
        setGameText("If a pilot was just lost from a system or sector, deploy on that location and stack pilot here. Pilot may be rescued or captured by any capital starship present here during any move phase, and may be targeted by weapons (except during battle) as if present (treat as a starfighter with defense value = 0). Lost if no pilot here.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.pilot, Filters.system_or_sector)) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            PhysicalCard pilotLost = lostFromTableResult.getCard();
            PhysicalCard location = lostFromTableResult.getFromLocation();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(location), null);
            if (action != null) {
                action.setText("Deploy due to lost " + GameUtils.getFullName(pilotLost));
                // Remember the pilot lost
                action.appendBeforeCost(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(pilotLost)));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard pilot = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            action.setText("Stack " + GameUtils.getFullName(pilot));
            action.setActionMsg("Stack " + GameUtils.getCardLink(pilot));
            // Perform result(s)
            action.appendEffect(
                    new StackOneCardFromLostPileEffect(action, pilot, self, false, true, true));
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && !GameConditions.hasStackedCards(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        return getRescueOrCaptureActions(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        return getRescueOrCaptureActions(playerId, game, self, gameTextSourceCardId);
    }

    /**
     * Gets the top-level actions to rescue or capture the character on the Lost In Space.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions
     */
    private List<TopLevelGameTextAction> getRescueOrCaptureActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)) {
            if (GameConditions.hasStackedCards(game, self, Filters.and(Filters.your(playerId), Filters.character))) {
                final PhysicalCard attachedCharacter = game.getGameState().getStackedCards(self).get(0);
                Filter capitalStarshipFilter = Filters.and(Filters.your(playerId), Filters.capital_starship, Filters.piloted,
                        Filters.present(self), Filters.or(Filters.hasAvailablePilotCapacity(attachedCharacter), Filters.hasAvailablePassengerCapacity(attachedCharacter)));
                if (GameConditions.canSpot(game, self, capitalStarshipFilter)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Rescue " + GameUtils.getFullName(attachedCharacter));
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose capital starship to rescue " + GameUtils.getCardLink(attachedCharacter), capitalStarshipFilter) {
                                @Override
                                protected void cardSelected(final PhysicalCard capitalStarship) {
                                    action.addAnimationGroup(attachedCharacter);
                                    action.addAnimationGroup(capitalStarship);
                                    // Need to determine capacity slot for character
                                    final boolean canBePilot = Filters.hasAvailablePilotCapacity(attachedCharacter).accepts(game, capitalStarship);
                                    boolean canBePassenger = Filters.hasAvailablePassengerCapacity(attachedCharacter).accepts(game, capitalStarship);

                                    if (canBePilot && canBePassenger) {
                                        String[] seatChoices = {"Pilot", "Passenger"};

                                        // Ask player to choose pilot/driver or passenger capacity slot
                                        action.appendTargeting(
                                                new PlayoutDecisionEffect(action, playerId,
                                                        new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(attachedCharacter) + " aboard " + GameUtils.getCardLink(capitalStarship), seatChoices) {
                                                            @Override
                                                            protected void validDecisionMade(int index, String result) {
                                                                final boolean asPilot = (index == 0);

                                                                // Allow response(s)
                                                                action.allowResponses("Have " + GameUtils.getCardLink(capitalStarship) + " rescue " + GameUtils.getCardLink(attachedCharacter),
                                                                        new UnrespondableEffect(action) {
                                                                            @Override
                                                                            protected void performActionResults(Action targetingAction) {
                                                                                // Perform result(s)
                                                                                action.appendEffect(
                                                                                        new RelocateFromLostInSpaceOrWeatherVaneToStarshipOrVehicle(action, attachedCharacter, capitalStarship, asPilot));
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                        }
                                                )
                                        );
                                    }
                                    else {
                                        // Allow response(s)
                                        action.allowResponses("Have " + GameUtils.getCardLink(capitalStarship) + " rescue " + GameUtils.getCardLink(attachedCharacter),
                                                new UnrespondableEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new RelocateFromLostInSpaceOrWeatherVaneToStarshipOrVehicle(action, attachedCharacter, capitalStarship, canBePilot));
                                                    }
                                                }
                                        );
                                    }
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
            // Check condition(s)
            if (playerId.equals(game.getDarkPlayer())
                    && GameConditions.hasStackedCards(game, self, Filters.and(Filters.opponents(playerId), Filters.character))) {
                final PhysicalCard attachedCharacter = game.getGameState().getStackedCards(self).get(0);
                Filter capitalStarshipFilter = Filters.and(Filters.your(playerId), Filters.capital_starship, Filters.piloted, Filters.present(self));
                if (GameConditions.canSpot(game, self, capitalStarshipFilter)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Capture " + GameUtils.getFullName(attachedCharacter));
                    action.addAnimationGroup(attachedCharacter);
                    // Allow response(s)
                    action.allowResponses("Capture " + GameUtils.getCardLink(attachedCharacter),
                            new UnrespondableEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new CaptureCharacterFromLostInSpaceOrWeatherVaneEffect(action, attachedCharacter, self.getAttachedTo()));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter pilot = Filters.and(Filters.character, Filters.stackedOn(self));
        Condition exceptDuringBattle = new NotCondition(new DuringBattleCondition());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeTargetedByWeaponsAsIfPresentModifier(self, pilot, exceptDuringBattle));
        modifiers.add(new MayBeTargetedByWeaponsLikeStarfighterModifier(self, pilot, exceptDuringBattle));
        modifiers.add(new ResetDefenseValueModifier(self, pilot, 0));
        return modifiers;
    }
}