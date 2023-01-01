package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Interrupt
 * Subtype: Used
 * Title: Escape Pod & We're Doomed
 */
public class Card204_018 extends AbstractUsedInterrupt {
    public Card204_018() {
        super(Side.LIGHT, 5, "Escape Pod & We're Doomed", Uniqueness.UNRESTRICTED, ExpansionSet.SET_4, Rarity.V);
        addComboCardTitles(Title.Escape_Pod, Title.Were_Doomed);
        setGameText("Use 1 Force to [upload] a dejarik. OR For remainder of turn, you lose no Force to Cloud City Occupation, Rebel Base Occupation, Tatooine Occupation, That Thing's Operational, You May Start Your Landing, or You'll Be Dead!");
        addIcons(Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.ESCAPE_POD__UPLOAD_DEJARIK;

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take a dejarik into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.dejarik, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Prevent Force loss until end of turn");
        // Allow response(s)
        action.allowResponses("Prevent Force loss from Cloud City Occupation, Rebel Base Occupation, Tatooine Occupation, That Thing's Operational, You May Start Your Landing, or You'll Be Dead! for remainder of turn",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action,
                                        new NoForceLossFromCardModifier(self, Filters.or(Filters.Cloud_City_Occupation, Filters.Rebel_Base_Occupation,
                                                Filters.Tatooine_Occupation, Filters.That_Things_Operational, Filters.You_May_Start_Your_Landing, Filters.Youll_Be_Dead), playerId),
                                        "Prevents Force loss from Cloud City Occupation, Rebel Base Occupation, Tatooine Occupation, That Thing's Operational, You May Start Your Landing, or You'll Be Dead!"));
                    }
                }
        );
        actions.add(action);

        return actions;
    }
}