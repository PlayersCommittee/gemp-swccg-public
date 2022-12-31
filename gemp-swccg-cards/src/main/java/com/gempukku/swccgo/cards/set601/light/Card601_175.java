package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 5
 * Type: Starship
 * Subtype: Starfighter
 * Title: Bravo Fighter (V)
 */
public class Card601_175 extends AbstractStarfighter {
    public Card601_175() {
        super(Side.LIGHT, 3, 3, 3, null, 5, 3, 4, Title.Bravo_Fighter, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Flown by an unknown pilot who likes to spin a lot. Played the deciding role in the attack against the Droid Control Ship.");
        setGameText("Permanent pilot provides ability of 4. May add 1 astromech. During battle, may target a starship with no character piloting. Draw destiny. If destiny > defense value, target hit. Immune to attrition < 3.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.REPUBLIC, Icon.PILOT, Icon.NAV_COMPUTER, Icon.LEGACY_BLOCK_5);
        addKeywords(Keyword.BRAVO_SQUADRON);
        addModelType(ModelType.N_1_STARFIGHTER);
        setAstromechCapacity(1);
        setAsLegacy(true);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(4) {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.starship, Filters.participatingInBattle, Filters.not(Filters.hasPiloting(self, Filters.character)));
        TargetingReason targetingReason = TargetingReason.TO_BE_HIT;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make a starship 'hit'");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " 'hit'",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(final Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard starship = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(starship);
                                                        }
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            float defenseValue = game.getModifiersQuerying().getDefenseValue(gameState, starship);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                            if (totalDestiny > defenseValue) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new HitCardEffect(action, starship, self));
                                                            } else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
