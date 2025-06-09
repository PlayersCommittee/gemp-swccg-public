package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractAlienImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Mitth'raw'nuruodo
 */
public class Card211_001 extends AbstractAlienImperial {
    public Card211_001() {
        super(Side.DARK, 2, 3, 3, 3, 6, "Mitth'raw'nuruodo", Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setLore("Thrawn. Chiss commander. Leader.");
        setGameText("[Pilot] 3. Once per turn, may target a related location; opponent loses 1 Force the next time they move to that location this turn. May lose 1 Force to cancel a just-drawn weapon destiny targeting a starship he is piloting. Thrawn's game text may not be canceled.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_11);
        addPersona(Persona.THRAWN);
        addKeywords(Keyword.LEADER, Keyword.COMMANDER);
        setSpecies(Species.CHISS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_7;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Target a location.");
            action.setActionMsg("Target a location.");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            //Select a location
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a related location", Filters.relatedLocation(self)){
                @Override
                protected void cardTargeted(int targetGroupId, final PhysicalCard location) {
                    action.addAnimationGroup(location);
                    // Allow response(s)
                    action.allowResponses("Target " + GameUtils.getCardLink(location),
                            new UnrespondableEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new SetWhileInPlayDataEffect(action, location, new WhileInPlayData())
                                    );
                                    action.appendEffect(
                                            new AddUntilEndOfTurnActionProxyEffect(action,
                                                    new AbstractActionProxy() {
                                                        @Override
                                                        public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                            List<TriggerAction> actions = new LinkedList<>();

                                                            // Check condition(s)
                                                            if (TriggerConditions.movedToLocation(game, effectResult, Filters.opponents(self), location)
                                                                    && GameConditions.cardHasWhileInPlayDataSet(location)) {

                                                                final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, self.getCardId());
                                                                action2.setText("Make opponent lose 1 force.");
                                                                action2.setActionMsg("Make opponent lose 1 force.");
                                                                // Actually retrieve the force
                                                                action2.appendEffect(new SetWhileInPlayDataEffect(action, location, null));
                                                                action2.appendEffect(new LoseForceEffect(action2, game.getOpponent(playerId), 1));
                                                                actions.add(action2);
                                                            }
                                                            return actions;
                                                        }
                                                    }
                                            ));
                                }
                            }
                    );
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.and(Filters.starship, Filters.hasPiloting(self)))
                && GameConditions.canCancelDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel weapon destiny");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true)
            );
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
