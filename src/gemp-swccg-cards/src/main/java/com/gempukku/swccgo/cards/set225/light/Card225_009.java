package com.gempukku.swccgo.cards.set225.light;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: Set 25
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: My Sister Has It
 */
public class Card225_009 extends AbstractUsedOrLostInterrupt {
    public Card225_009() {
        super(Side.LIGHT, 5, "My Sister Has It", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("'He's my brother.'");
        setGameText("USED: Subtract 1 from a just drawn Force Lightning or 'choke' destiny (unless targeting an Undercover spy). OR If a [Skywalker] Effect on table, [upload] Chief Chirpa's Hut or Guest Quarters. LOST: If Leia in battle, all immunity to attrition there is canceled.");
        addIcons(Icon.SKYWALKER, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.MY_SISTER_HAS_IT__UPLOAD_SITE;
        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Icon.SKYWALKER, Filters.Effect))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take site into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Chief Chirpa's Hut or Guest Quarters into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Chief_Chirpas_Hut, Filters.title("Cloud City: Guest Quarters")), true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Leia)
                && GameConditions.canSpot(game, self, Filters.and(Filters.participatingInBattle, Filters.hasAnyImmunityToAttrition))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Cancel all immunity to attrition");
            // Allow response(s)
            action.allowResponses("Cancel all immunity to attrition in the battle",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelImmunityToAttritionUntilEndOfBattleEffect(action, Filters.participatingInBattle, "cancels immunity to attrition"));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if ((TriggerConditions.isDestinyJustDrawnFor(game, effectResult, Filters.Force_Lightning)
                || TriggerConditions.isDestinyDrawType(game, effectResult, DestinyType.CHOKE_DESTINY)) 
                && TriggerConditions.isDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.not(Filters.undercover_spy))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Subtract 1 from destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, -1));
                        }
                    }
            );
            actions.add(action);                    
        }
        return actions;
    }
}
