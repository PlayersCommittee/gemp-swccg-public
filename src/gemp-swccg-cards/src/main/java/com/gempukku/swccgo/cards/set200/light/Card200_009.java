package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Rebel
 * Title: Daughter Of Skywalker (V)
 */
public class Card200_009 extends AbstractRebel {
    public Card200_009() {
        super(Side.LIGHT, 1, 4, 4, 5, 8, Title.Daughter_Of_Skywalker, Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Scout. Leader. Made friends with Wicket. Negotiated an alliance with the Ewoks. Leia found out the truth about her father from Luke in the Ewok village.");
        setGameText("While at an Endor site, adds one [Light Side] icon here. During battle, may target one opponent's character present. Draw destiny. If destiny > ability, target is power -2 and its game text is canceled. Your scouts here are immune to Sniper, You Are Beaten, and attrition < 4.");
        addPersona(Persona.LEIA);
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT, Keyword.LEADER, Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter scoutsHere = Filters.and(Filters.your(self), Filters.scout, Filters.here(self));

        modifiers.add(new IconModifier(self, Filters.sameSite(self), new AtCondition(self, Filters.Endor_site), Icon.LIGHT_FORCE, 1));
        modifiers.add(new ImmuneToTitleModifier(self, scoutsHere, Title.Sniper));
        modifiers.add(new ImmuneToTitleModifier(self, scoutsHere, Title.You_Are_Beaten));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, scoutsHere, 4));

        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        // once per battle
        GameTextActionId gameTextActionId = GameTextActionId.DAUGHTER_OF_SKYWALKER_VIRTUAL_GAMETEXT_ONCE_PER_BATTLE;

        // "target one opponent's character present"
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.present(self));

        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, targetFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Target opponent's character");

            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            // Once Per battle
                            action.appendUsage(new OncePerBattleEffect(action));
                            // Allow response(s)
                            action.allowResponses("Targeting " + GameUtils.getCardLink(targetedCard),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(finalTarget);
                                                        }

                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalTarget);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                            if (totalDestiny > ability) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(new ModifyPowerEffect(action, targetedCard, -2));
                                                                action.appendEffect(new CancelGameTextEffect(action, finalTarget));
                                                            } else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
