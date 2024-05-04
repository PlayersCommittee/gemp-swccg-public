package com.gempukku.swccgo.cards.set104.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
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
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Empire Strikes Back Introductory Two Player Game)
 * Type: Interrupt
 * Subtype: Lost
 * Title: Walker Garrison
 */
public class Card104_007 extends AbstractLostInterrupt {
    public Card104_007() {
        super(Side.DARK, 4, Title.Walker_Garrison, Uniqueness.UNRESTRICTED, ExpansionSet.ESB_INTRO_TWO_PLAYER, Rarity.PM);
        setLore("When efficiently deployed, a squadron of AT-ATs can quickly take control of a wide area, making it easy for imperial forces to dominate a planet.");
        setGameText("If Veers is at an exterior Hoth site, use 2 Force to search your Reserve Deck and take one AT-AT into your hand. OR If you have 3 AT-ATs at three different Hoth sites, your Force drains are +2 at Hoth locations this turn.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.WALKER_GARRISON__UPLOAD_ATAT;

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Veers, Filters.at(Filters.exterior_Hoth_site)))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take an AT-AT into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.AT_AT, true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.canSpotLocation(game, siteCountToGainForceDrainBonusEffect(game, self),
                Filters.and(Filters.Hoth_site, Filters.sameLocationAs(self, Filters.and(Filters.your(playerId), Filters.AT_AT))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Make Force drains +2 at Hoth locations");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                        new ForceDrainModifier(self, Filters.Hoth_location, 2, playerId),
                                            "Makes Force drains +2 at Hoth locations"));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    private int siteCountToGainForceDrainBonusEffect(SwccgGame game, PhysicalCard self) {
        return 3 + GameConditions.getGameTextModificationCount(game, self, ModifyGameTextType.WALKER_GARRISON__ADDITIONAL_SITE_TO_GAIN_FORCE_DRAIN_BONUS);
    }
}