package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: K'vin J. Lawdogg
 */
public class Card304_140 extends AbstractAlien {
    public Card304_140() {
        super(Side.DARK, 1, 5, 1, 2, 6, "K'vin J. Lawdogg", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("K'vin is a good boy. While he studied law he's found his main role is keeping Thran out of trouble. You can see in his eyes the number of times he's found Thran in a compromising position.");
        setGameText("Deploys -3 to Thran's site. When with Thran, adds one battle destiny. Once per turn, you may use 1 Force to peek at top card of opponent's Reserve Deck; may place that card on bottom of that Reserve Deck. Immune to attrition < 4.");
        addPersona(Persona.KVIN);
        addIcons(Icon.CSP);
        addKeywords(Keyword.MALE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -3, Filters.sameSiteAs(self, Filters.Thran)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.Thran), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasReserveDeck(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top of opponent's Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, opponent) {
                        @Override
                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new YesNoDecision("Do you want to place " + GameUtils.getCardLink(peekedAtCard) + " on bottom of Reserve Deck?") {
                                                @Override
                                                protected void yes() {
                                                    action.appendEffect(
                                                            new PutCardFromReserveDeckOnBottomOfCardPileEffect(action, peekedAtCard, Zone.RESERVE_DECK, true));
                                                }
                                                protected void no() {
                                                    game.getGameState().sendMessage(playerId + " chooses to not place card on bottom of Reserve Deck");
                                                }
                                            }
                                    ));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
