package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleActionProxyEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Gambler's Luck
 */
public class Card5_048 extends AbstractLostInterrupt {
    public Card5_048() {
        super(Side.LIGHT, 4, "Gambler's Luck", Uniqueness.UNIQUE);
        setLore("Works every time.");
        setGameText("If your gambler is defending a battle alone at a site, add one battle destiny (draw two destiny, and choose one). OR if your Lando is defending a battle alone at a site, add two battle destiny (draw three and choose two).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final int gameTextSourceCardId = self.getCardId();

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.gambler, Filters.defendingBattle, Filters.alone))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                                final int permCardId = self.getPermanentCardId();
                                action.appendEffect(
                                        new AddUntilEndOfBattleActionProxyEffect(action,
                                                new AbstractActionProxy() {
                                                    @Override
                                                    public List<TriggerAction> getOptionalAfterTriggers(String playerId2, SwccgGame game, EffectResult effectResult) {
                                                        List<TriggerAction> actions2 = new LinkedList<TriggerAction>();
                                                        final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                        // Check condition(s)
                                                        if (playerId2.equals(playerId)
                                                                && TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                                                                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                                                                && GameConditions.canDrawDestinyAndChoose(game, 2)) {

                                                            final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                                                            action2.setText("Draw two and choose one");
                                                            // Update usage limit(s)
                                                            action2.appendUsage(
                                                                    new OncePerBattleEffect(action2));
                                                            // Perform result(s)
                                                            action2.appendEffect(
                                                                    new DrawDestinyAndChooseInsteadEffect(action2, 2, 1));
                                                            actions2.add(action2);
                                                        }
                                                        return actions2;
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                );
                actions.add(action);
            }
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Lando, Filters.defendingBattle, Filters.alone))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add two battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 2));
                                final int permCardId = self.getPermanentCardId();
                                action.appendEffect(
                                        new AddUntilEndOfBattleActionProxyEffect(action,
                                                new AbstractActionProxy() {
                                                    @Override
                                                    public List<TriggerAction> getOptionalAfterTriggers(String playerId2, SwccgGame game, EffectResult effectResult) {
                                                        List<TriggerAction> actions2 = new LinkedList<TriggerAction>();
                                                        final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                        // Check condition(s)
                                                        if (playerId2.equals(playerId)
                                                                && TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                                                                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                                                                && GameConditions.canDrawDestinyAndChoose(game, 3)) {

                                                            final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                                                            action2.setText("Draw three and choose two");
                                                            // Update usage limit(s)
                                                            action2.appendUsage(
                                                                    new OncePerBattleEffect(action2));
                                                            // Perform result(s)
                                                            action2.appendEffect(
                                                                    new DrawDestinyAndChooseInsteadEffect(action2, 3, 2));
                                                            actions2.add(action2);
                                                        }
                                                        return actions2;
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}