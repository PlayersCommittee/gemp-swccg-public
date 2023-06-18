package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Used
 * Title: Either Way, You Win (V)
 */
public class Card221_057 extends AbstractUsedInterrupt {
    public Card221_057() {
        super(Side.LIGHT, 4, "Either Way, You Win", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Deal!'");
        setGameText("If [Tatooine] or [Coruscant] Qui-Gon in battle, he is power +1 for each 'credit.' OR Once per game, if a battle just initiated at Watto's Junkyard involving Qui-Gon, target a character. Lightsabers may not be fired this battle. Unless target is Watto or a Dark Jedi, cancel target's game text.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {

        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.or(Icon.TATOOINE, Icon.CORUSCANT), Filters.QuiGon))) {

            int creditCount = Filters.countStacked(game, Filters.creditCard);

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make Qui-Gon power +"+creditCount);

            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target Qui-Gon", Filters.and(Filters.or(Icon.TATOOINE, Icon.CORUSCANT), Filters.QuiGon)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                            action.addAnimationGroup(character);
                            // Allow response(s)

                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalQuiGon = action.getPrimaryTargetCard(targetGroupId);
                                            int finalCreditCount = Filters.countStacked(game, Filters.creditCard);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action, new PowerModifier(self, finalQuiGon, finalCreditCount), "Makes " + GameUtils.getCardLink(finalQuiGon) + " power +" + finalCreditCount));
                                        }
                                    }
                            );
                        }

                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                    });

            return Collections.singletonList(action);
        }
        return null;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.EITHER_WAY_YOU_WIN_V__TARGET_CHARACTER;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.Wattos_Junkyard)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.QuiGon)
                && GameConditions.canTarget(game, self, Filters.and(Filters.character, Filters.participatingInBattle))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Target character");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target character", Filters.and(Filters.character, Filters.participatingInBattle)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                            action.addAnimationGroup(character);
                            // Allow response(s)

                            action.allowResponses(
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action, new MayNotBeFiredModifier(self, Filters.lightsaber), "Prevents lightsabers from being fired"));

                                            if (!Filters.or(Filters.Watto, Filters.Dark_Jedi).accepts(game, finalTarget)) {
                                                action.appendEffect(
                                                        new CancelGameTextUntilEndOfBattleEffect(action, finalTarget));
                                            }
                                        }
                                    }
                            );
                        }
                    });

            return Collections.singletonList(action);
        }
        return null;
    }
}