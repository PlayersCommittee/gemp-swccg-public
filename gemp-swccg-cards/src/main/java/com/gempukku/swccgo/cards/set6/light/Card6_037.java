package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
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
 * Title: Saelt-Marae
 */
public class Card6_037 extends AbstractAlien {
    public Card6_037() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Saelt-Marae", Uniqueness.UNIQUE);
        setLore("Male Yarkora. Mysterious member of Jabba's court. Posing as an alien artifact trader. Associates with the B'omarr monks at Jabba's palace. No one knows his true motivations.");
        setGameText("During battle, if an opponent's alien present, may use 3 Force to peek at top card of any Reserve Deck. While at Audience Chamber, all your other Yarkora are power and forfeit +2.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.YARKORA);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 3)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, game.getOpponent(playerId)))
                && GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.alien, Filters.present(self)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at top card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void pileChosen(final SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 3));
                            action.setActionMsg("Peek at top card of " + cardPileOwner + "'s Reserve Deck");
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtTopCardOfReserveDeckEffect(action, playerId, cardPileOwner));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourOtherYarkora = Filters.and(Filters.your(self), Filters.other(self), Filters.Yarkora);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourOtherYarkora, atAudienceChamber, 2));
        modifiers.add(new ForfeitModifier(self, yourOtherYarkora, atAudienceChamber, 2));
        return modifiers;
    }
}
