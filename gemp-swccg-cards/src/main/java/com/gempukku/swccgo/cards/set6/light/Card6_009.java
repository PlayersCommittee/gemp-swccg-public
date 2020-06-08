package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.RevealRandomCardInOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromOpponentsHand;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Chadra-Fan
 */
public class Card6_009 extends AbstractAlien {
    public Card6_009() {
        super(Side.LIGHT, 4, 2, 1, 2, 2, "Chadra-Fan");
        setLore("Short, intelligent and selfish. Their enhanced vision and smell make them outstanding Thieves. Communicate with pheromones and high-pitched squeaks.");
        setGameText("Power and forfeit +2 while Kabe at Audience Chamber. During your control phase, may glance at one card randomly selected from opponent's hand; if that card is a character weapon, may 'steal' it into your hand.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.CHADRAFAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whileKabeAtAudienceChamber = new AtCondition(self, Filters.Kabe, Filters.Audience_Chamber);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, whileKabeAtAudienceChamber, 2));
        modifiers.add(new ForfeitModifier(self, whileKabeAtAudienceChamber, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasHand(game, game.getOpponent(playerId))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Reveal random card in opponent's hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RevealRandomCardInOpponentsHandEffect(action, playerId) {
                        @Override
                        protected void cardRevealed(final PhysicalCard revealedCard) {
                            if (Filters.character_weapon.accepts(game, revealedCard)
                                    && GameConditions.canSteal(game, self)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want 'steal' " + GameUtils.getCardLink(revealedCard) + " into hand?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new StealCardIntoHandFromOpponentsHand(action, revealedCard));
                                                    }
                                                    @Override
                                                    protected void no() {
                                                        game.getGameState().sendMessage(playerId + " chooses to not to 'steal' " + GameUtils.getCardLink(revealedCard) + " into hand");
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
