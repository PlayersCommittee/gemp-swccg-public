package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfLightsaberCombatActionProxyEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Lost
 * Title: Clinging To The Edge
 */
public class Card13_010 extends AbstractLostInterrupt {
    public Card13_010() {
        super(Side.LIGHT, 5, "Clinging To The Edge", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("There are some times, more than others, when you should not look down.");
        setGameText("If opponent's Dark Jedi with at least one combat card just initiated lightsaber combat against your Jedi with none, draw 3 destiny and choose 2 to use for lightsaber combat destiny. You may take other card into hand, or return it to top of Reserve Deck.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (!TriggerConditions.lightsaberCombatInitiated(game, effectResult)) {
            return null;
        }

        LightsaberCombatState lightsaberCombatState = game.getGameState().getLightsaberCombatState();
        if (lightsaberCombatState == null || !lightsaberCombatState.canContinue(game)) {
            return null;
        }

        // LS-only response: opponent's Dark Jedi initiated against your Jedi with no combat cards
        if (!playerId.equals(game.getLightPlayer())) {
            return null;
        }
        if (!game.getDarkPlayer().equals(lightsaberCombatState.getPlayerInitiatedLightsaberCombat())) {
            return null;
        }

        PhysicalCard darkJedi = lightsaberCombatState.getCharacter(game.getDarkPlayer());
        PhysicalCard jedi = lightsaberCombatState.getCharacter(playerId);
        if (darkJedi == null || jedi == null) {
            return null;
        }
        if (!Filters.and(Filters.opponents(self), Filters.Dark_Jedi, Filters.hasStacked(Filters.combatCard)).accepts(game, darkJedi)) {
            return null;
        }
        if (!Filters.and(Filters.your(self), Filters.Jedi, Filters.not(Filters.hasStacked(Filters.combatCard))).accepts(game, jedi)) {
            return null;
        }

        final int permCardId = self.getPermanentCardId();
        final int gameTextSourceCardId = self.getCardId();

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Draw 3 lightsaber combat destiny and choose 2");
        // Allow response(s)
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // When LS is about to draw lightsaber combat destiny, replace with draw 3 choose 2;
                        // leftover may go to hand or top of Reserve Deck.
                        action.appendEffect(
                                new AddUntilEndOfLightsaberCombatActionProxyEffect(action,
                                        new AbstractActionProxy() {
                                            private boolean _triggered;

                                            @Override
                                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game2, EffectResult effectResult2) {
                                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                final PhysicalCard card = game2.findCardByPermanentId(permCardId);
                                                if (!_triggered
                                                        && TriggerConditions.isAboutToDrawLightsaberCombatDestiny(game2, effectResult2, playerId)
                                                        && GameConditions.canDrawDestinyAndChoose(game2, 3)) {

                                                    _triggered = true;
                                                    final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(card, gameTextSourceCardId);
                                                    action2.setText("Draw three and choose two");
                                                    action2.appendEffect(
                                                            new DrawDestinyAndChooseInsteadEffect(action2, 3, 2, true));
                                                    actions.add(action2);
                                                }
                                                return actions;
                                            }
                                        }
                                )
                        );
                    }
                }
        );
        return Collections.singletonList(action);
    }
}
