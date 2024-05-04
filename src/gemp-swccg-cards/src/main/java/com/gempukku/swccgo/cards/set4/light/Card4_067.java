package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Tunnel Vision
 */
public class Card4_067 extends AbstractLostInterrupt {
    public Card4_067() {
        super(Side.LIGHT, 3, Title.Tunnel_Vision, Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("Han could always see the light at the end of the tunnel.");
        setGameText("Search your Force Pile and take one card into hand. Shuffle, cut and replace.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.TUNNEL_VISION__UPLOAD_CARD_FROM_FORCE_PILE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Force Pile");
            // Allow response(s)
            action.allowResponses("Take a card into hand from Force Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromForcePileEffect(action, playerId, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}