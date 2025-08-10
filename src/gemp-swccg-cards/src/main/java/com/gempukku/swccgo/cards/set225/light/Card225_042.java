package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ResetDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Interrupt
 * Subtype: Lost
 * Title: Courage Of A Skywalker (V)
 */
public class Card225_042 extends AbstractLostInterrupt {
    public Card225_042() {
        super(Side.LIGHT, 2, Title.Courage_Of_A_Skywalker, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Despite being alone, trapped and desperately outmatched, Luke continued his battle with the Dark Lord of the Sith.");
        setGameText("If a [Skywalker] Effect on table, destiny +2 when drawn for destiny. Take a lightsaber into hand from Force Pile; reshuffle. OR Retrieve Anakin's Lightsaber. OR Once per game, during a battle or duel involving a Skywalker and a Dark Jedi, make a just drawn destiny = 2.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_25);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Condition skywalkerEffectOnTable = new OnTableCondition(self, Filters.and(Icon.SKYWALKER, Filters.Effect));
        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, skywalkerEffectOnTable, 2));
        return modifiers;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.COURAGE_OF_A_SKYWALKER__UPLOAD_LIGHTSABER_FROM_FORCE_PILE;
        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take a lightsaber into hand from Force Pile");
            // Allow response(s)
            action.allowResponses("Take a lightsaber into hand from Force Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromForcePileEffect(action, playerId, Filters.lightsaber, true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.COURAGE_OF_A_SKYWALKER__RETRIEVE_ANAKINS_LIGHTSABER;
        // Check condition(s)
        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
            
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve Anakin's Lightsaber");

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, Filters.Anakins_Lightsaber));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.COURAGE_OF_A_SKYWALKER_V__DESTINY_EQUALS_2;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && (TriggerConditions.isDestinyJustDrawn(game, effectResult))
                && (((GameConditions.isDuringBattleWithParticipant(game, Filters.Skywalker))
                && (GameConditions.isDuringBattleWithParticipant(game, Filters.Dark_Jedi)))
                || ((GameConditions.isDuringDuelWithParticipant(game, Filters.Skywalker))
                && (GameConditions.isDuringDuelWithParticipant(game, Filters.Dark_Jedi))))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Set destiny to 2");
            action.appendUsage(new OncePerGameEffect(action));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new ResetDestinyEffect(action, 2));
                }
            });
            actions.add(action);
        }

        return actions;
    }
}
