package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 7
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Prepared Defenses (V)
 */
public class Card601_093 extends AbstractUsedOrStartingInterrupt {
    public Card601_093() {
        super(Side.DARK, 3, "Prepared Defenses", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("Since the debacle at Yavin, the Emperor places a premium on the security of his costly war machine.");
        setGameText("USED: Take one Death Squadron Star Destroyer, a Dreadnaught, or a Victory-class Star Destroyer into hand from Reserve Deck; reshuffle. STARTING: Deploy from Reserve Deck up to three cards that have the [Setup] icon or are Effects that deploy for free, are always [Immune to Alter], and deploy on table.  Place Interrupt in Lost Pile.");
        addIcons(Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_7);
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__PREPARED_DEFENSES__UPLOAD_STAR_DESTROYER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take a starship into hand from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            // Allow response(s)
            action.allowResponses("Take a Death Squadron Star Destroyer, Dreadnaught, or Victory-Class Star Destroyer into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Dreadnaught_class_cruisers, Filters.and(Filters.Star_Destroyer, Filters.or(Keyword.DEATH_SQUADRON, Filters.Victory_class_Star_Destroyer))), true));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy up to three [Setup] cards and/or Effects from Reserve Deck");
        // Allow response(s)
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DeployCardsFromReserveDeckEffect(action, Filters.or(Icon.SETUP, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter,
                                        Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table")))), 1, 3, true, false));
                        action.appendEffect(
                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                    }
                }
        );
        return action;
    }
}