package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.DuringForceDrainAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Deneb Both
 */
public class Card11_002 extends AbstractAlien {
    public Card11_002() {
        super(Side.LIGHT, 2, 3, 1, 1, 5, "Deneb Both", Uniqueness.UNIQUE);
        setLore("Shy Ithorian forester. She seeks to leave Tatooine for a better life. Firm adherent to Ithorian philosophy of planting two trees for every one harvested.");
        setGameText("When deployed, draw up to 2 cards from top of Reserve Deck. Unless Graak on table, while at a battleground and opponent is losing Force from Force drains at same or adjacent site, lost Force must come from Force Pile, if possible.");
        addIcons(Icon.TATOOINE);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.ITHORIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, new AndCondition(new CantSpotCondition(self, Filters.Graak),
                new AtCondition(self, Filters.battleground), new DuringForceDrainAtCondition(Filters.sameOrAdjacentSite(self))),
                ModifierFlag.FORCE_DRAIN_LOST_FROM_FORCE_PILE, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            int maxCardsToDraw = Math.min(2, game.getGameState().getReserveDeckSize(playerId));
            if (maxCardsToDraw > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Draw cards from Reserve Deck");
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose number of cards to draw ", 1, maxCardsToDraw, maxCardsToDraw) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.setActionMsg("Draw " + result + " card" + GameUtils.s(result) + " from top of Reserve Deck");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, result));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
