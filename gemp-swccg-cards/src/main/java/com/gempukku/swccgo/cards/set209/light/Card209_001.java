package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Rebel
 * Title: Admiral Raddus
 */
public class Card209_001 extends AbstractRebel {
    public Card209_001() {
        super(Side.LIGHT, 2, 0, 2, 3, 4, "Admiral Raddus", Uniqueness.UNIQUE);
        setLore("Mon Calamari leader.");
        setGameText("Your capital starships here are power +1. Once per game, may [upload] Rogue One, a Hammerhead corvette, or a non-unique corvette.");
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
        setSpecies(Species.MON_CALAMARI);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ADMIRAL_RADDUS__UPLOAD_ROGUE_ONE_OR_CORVETTE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Rogue One, a Hammerhead corvette, or a non-unique corvette into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Rogue_One, Filters.Hammerhead, Filters.and(Filters.non_unique, Filters.corvette)), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.capital_starship, Filters.here(self)), 1));
        return modifiers;
    }
}
