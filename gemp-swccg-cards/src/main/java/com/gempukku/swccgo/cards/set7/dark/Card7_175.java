package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Imperial
 * Title: Darth Vader, Dark Lord Of The Sith
 */
public class Card7_175 extends AbstractImperial {
    public Card7_175() {
        super(Side.DARK, 1, 6, 6, 6, 8, Title.Darth_Vader_Dark_Lord_of_the_Sith, Uniqueness.UNIQUE);
        setLore("Formerly Anakin Skywalker, Jedi Knight. Became Darth Vader. Ordered by Emperor Palpatine to deal with Luke Skywalker, but bargained for his son's life instead.");
        setGameText("Adds 3 to power of anything he pilots. When in a battle, may target one opponent's character present. Draw destiny. Target 'choked' (lost) if destiny > ability. Immune to attrition < 5.");
        addPersona(Persona.VADER);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)) {
            TargetingReason targetingReason = TargetingReason.TO_BE_CHOKED;
            Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.present(self));
            if (GameConditions.canTarget(game, self, targetingReason, filter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("'Choke' a character");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target character", targetingReason, filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses(playerId + " targets to 'choke' " + GameUtils.getCardLink(targetedCard),
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard cardToChoke = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                return Collections.singletonList(cardToChoke);
                                                            }

                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                GameState gameState = game.getGameState();
                                                                if (totalDestiny == null) {
                                                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                    return;
                                                                }

                                                                float ability = game.getModifiersQuerying().getAbility(game.getGameState(), cardToChoke);
                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                                if (totalDestiny > ability) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new LoseCardFromTableEffect(action, cardToChoke));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
