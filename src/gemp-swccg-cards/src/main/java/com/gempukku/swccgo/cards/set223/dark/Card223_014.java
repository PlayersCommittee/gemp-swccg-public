package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInForcePileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used
 * Title: Imperial Assault
 */
public class Card223_014 extends AbstractUsedInterrupt {
    public Card223_014() {
        super(Side.DARK, 3, "Imperial Assault", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setGameText("Destiny +1 for each of your battlegrounds on table when drawn for weapon or battle destiny (limit +4). If you have deployed four battlegrounds this game: place this card in your force pile and shuffle. OR During battle, your total power is +2.");
        addIcons(Icon.VIRTUAL_SET_23);
    }    
    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();;
        String playerId = self.getOwner();

        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, self, new MaxLimitEvaluator(new OnTableEvaluator(self, Filters.and(Filters.your(playerId), Filters.battleground)), 4)));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, self, new MaxLimitEvaluator(new OnTableEvaluator(self, Filters.and(Filters.your(playerId), Filters.battleground)), 4)));

        return modifiers;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {

       List<PlayInterruptAction> actions = new LinkedList<>();

       // Check condition(s)
        if (GameConditions.hasDeployedAtLeastXCardsThisGame(game, playerId, 4, Filters.battleground)) 
        {

            final PlayInterruptAction forcePileAction = new PlayInterruptAction(game, self);
            forcePileAction.setText("Place this card in your force pile");
            // Allow response(s)
            forcePileAction.allowResponses(
                    new RespondablePlayCardEffect(forcePileAction) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            forcePileAction.appendEffect(
                                    new PutCardFromVoidInForcePileEffect(forcePileAction, playerId, self));
                            forcePileAction.appendEffect(
                                                new ShufflePileEffect(forcePileAction, playerId, Zone.FORCE_PILE));
                        }
                    }
            );
            actions.add(forcePileAction);

            if (GameConditions.isDuringBattle(game)) {
                final PlayInterruptAction powerAction = new PlayInterruptAction(game, self);
                powerAction.setText("Add 2 to total power");

                powerAction.allowResponses("Add 2 to total power",
                        new RespondablePlayCardEffect(powerAction) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                powerAction.appendEffect(
                                                    new ModifyTotalPowerUntilEndOfBattleEffect(powerAction, 2, playerId, "Adds 2 to total power"));
                            }
                        }
                );
                actions.add(powerAction);
            }

        }
        return actions;
    }

}
