package com.gempukku.swccgo.cards.set218.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 18
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: He's No Good To Me Dead
 */
public class Card218_032 extends AbstractUsedInterrupt {
    public Card218_032() {
        super(Side.DARK, 4, "He's No Good To Me Dead", Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        setGameText("For remainder of turn, your Fetts add 1 to their weapon destiny draws and targets they 'hit' are forfeit = 0. OR Once per game, cancel a battle just initiated where a Fett is escorting a captive; opponent's cards there move for free this turn. [Immune to Sense.]");
        addIcons(Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        if (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Fett))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Affect your Fetts using weapons");
            action.setImmuneTo(Title.Sense);

            action.allowResponses("Add to Fett weapon destinies and make targets they hit forfeit = 0", new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    final int permCardId = self.getPermanentCardId();
                    final int gameTextSourceCardId = self.getCardId();
                    action.appendEffect(new AddUntilEndOfTurnModifierEffect(action, new EachWeaponDestinyModifier(self, Filters.any, Filters.and(Filters.your(self), Filters.Fett), 1), "Add 1 to your Fetts' weapon destinies"));

                    action.appendEffect(
                            new AddUntilEndOfTurnActionProxyEffect(action, new AbstractActionProxy() {
                                @Override
                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                    final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                    // Check condition(s)
                                    if (TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.and(Filters.your(self), Filters.Fett))) {
                                        PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

                                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                        action.setText("Reset " + GameUtils.getFullName(cardHit) + "'s forfeit to 0");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ResetForfeitUntilEndOfTurnEffect(action, cardHit, 0));
                                        return Collections.singletonList((TriggerAction) action);
                                    }
                                    return null;
                                }
                            }
                            )
                    );
                }
            });

            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.HES_NO_GOOD_TO_ME_DEAD__CANCEL_BATTLE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameLocationAs(self, Filters.and(Filters.Fett, Filters.escorting(Filters.captive))))) {

            final PhysicalCard battleLocation = Filters.findFirstFromTopLocationsOnTable(game, Filters.battleLocation);

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Cancel battle");
            action.setImmuneTo(Title.Sense);

            action.appendUsage(
                    new OncePerGameEffect(action));
            // Allow response(s)
            action.allowResponses("Cancel battle",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelBattleEffect(action));
                            Collection<PhysicalCard> opponentsCardsThere = Filters.filterActive(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.at(battleLocation), Filters.canBeTargetedBy(self)));
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action, new MovesForFreeModifier(self, Filters.in(opponentsCardsThere)), "makes " + opponent + "'s cards move for free"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}