package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfLightsaberCombatModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.NumLightsaberCombatDestinyDrawsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
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

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Draw 3 destiny and choose 2");
        // Allow response(s)
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Draw 3 destiny now (while this interrupt is resolving). The 2 chosen values are
                        // lightsaber combat destiny; the leftover may go to hand or top of Reserve Deck.
                        // Those destinies replace the later lightsaber combat draws, so combat cards
                        // ("instead of drawing") are not offered afterward.
                        DrawDestinyEffect drawDestiny = new DrawDestinyEffect(action, playerId, 3, 2, DestinyType.DESTINY) {
                            @Override
                            protected void destinyDraws(SwccgGame game2, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                LightsaberCombatState state = game2.getGameState().getLightsaberCombatState();
                                if (state != null && totalDestiny != null) {
                                    int drawn = destinyDrawValues != null ? destinyDrawValues.size() : 0;
                                    state.increaseTotalLightsaberCombatDestinyFromDraws(playerId, totalDestiny, drawn);
                                }
                            }
                        };
                        drawDestiny.setMayTakeOtherIntoHandOrReturnToTopOfReserve(true);
                        action.appendEffect(drawDestiny);
                        action.appendEffect(
                                new AddUntilEndOfLightsaberCombatModifierEffect(action,
                                        new NumLightsaberCombatDestinyDrawsModifier(self, -2, playerId),
                                        "Use Clinging To The Edge destinies for lightsaber combat"));
                    }
                }
        );
        return Collections.singletonList(action);
    }
}
