package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtRandomCardInOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Alien
 * Title: Talon Karrde
 */
public class Card10_024 extends AbstractAlien {
    public Card10_024() {
        super(Side.LIGHT, 3, 3, 2, 3, 4, "Talon Karrde", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("Smuggler and information broker. Operates intelligence network. Pragmatic businessman. Strong sense of personal honor. Bounty placed on his head by Grand Admiral Thrawn.");
        setGameText("Adds 2 to power of anything he pilots. Once during each of your control phases, may peek at one card from opponent's hand or top card of opponent's Reserve Deck. At same location, your smugglers are each forfeit +2 and defense value +2.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER, Keyword.INFORMATION_BROKER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourSmugglersAtSameLocation = Filters.and(Filters.your(self), Filters.smuggler, Filters.atSameLocation(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForfeitModifier(self, yourSmugglersAtSameLocation, 2));
        modifiers.add(new DefenseValueModifier(self, yourSmugglersAtSameLocation, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            if (GameConditions.hasHand(game, opponent)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Peek at card in opponent's hand");
                action.setActionMsg("Peek at a card from opponent's hand");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PeekAtRandomCardInOpponentsHandEffect(action, playerId));
                actions.add(action);
            }

            if (GameConditions.hasReserveDeck(game, opponent)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Peek at top of opponent's Reserve Deck");
                action.setActionMsg("Peek at top card of opponent's Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PeekAtTopCardOfReserveDeckEffect(action, playerId, opponent));
                actions.add(action);
            }
        }
        return actions;
    }
}
