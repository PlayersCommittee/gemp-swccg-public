package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardTypeAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseOneForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Hiding In The Garbage
 */
public class Card4_025 extends AbstractNormalEffect {
    public Card4_025() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Hiding In The Garbage", Uniqueness.UNIQUE);
        setLore("Rebels often exploit loopholes in Imperial procedures to gain an advantage.");
        setGameText("Use 2 Force to deploy on your side of table. At any time, you may declare a card type. Use Force one by one, revealing each card used, until a card of that type is revealed (take it into hand) or Force Pile is depleted.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Use Force and search for card type");
            // Pay cost(s)
            action.appendCost(
                    new PlayoutDecisionEffect(action, playerId,
                            new CardTypeAwaitingDecision(game, "Declare a card type") {
                                @Override
                                protected void cardTypeChosen(final CardType cardType) {
                                    game.getGameState().sendMessage(playerId + " declares " + cardType.getHumanReadable() + " card type");
                                    action.setActionMsg("Use Force and search for " + cardType.getHumanReadable() + " card type");
                                    // Perform result(s)
                                    action.appendEffect(
                                            new UseOneForceEffect(action, playerId, true) {
                                                @Override
                                                protected void forceUsed(PhysicalCard card) {
                                                    if (game.getModifiersQuerying().getCardTypes(game.getGameState(), card).contains(cardType)) {
                                                        action.appendEffect(
                                                                new TakeCardIntoHandFromUsedPileEffect(action, playerId, card, false));
                                                    }
                                                    else if (GameConditions.hasForcePile(game, playerId)) {
                                                        // Perform this UseOneForceEffect again
                                                        action.appendEffect(this);
                                                    }
                                                }
                                            });
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}