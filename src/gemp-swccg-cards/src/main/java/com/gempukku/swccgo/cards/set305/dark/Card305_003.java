package com.gempukku.swccgo.cards.set305.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A Better Tomorrow
 * Type: Effect
 * Title:  Conquering Caperion
 */
public class Card305_003 extends AbstractNormalEffect {
    public Card305_003() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Conquering_Caperion, Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.V);
        setLore("The conquest of the Caperion system relied on swift troop dispatch from docking bays.");
        setGameText("Deploy on table. Your Force generation is +1 at each docking bay you occupy (or +2 if you control). Once per game, you may take one Seraph, Ulress, Danktooine, Ragnath or Myryakur system or SARLaC into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.docking_bay, Filters.occupies(playerId), Filters.not(Filters.controls(playerId))), 1, playerId));
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.docking_bay, Filters.controls(playerId)), 2, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CONQUERING_CAPERION__UPLOAD_SYSTEM_OR_SARLAC;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take system or SARLaC into hand from Reserve Deck");
            action.setActionMsg("Take Seraph, Ulress, Danktooine, Ragnath, or Myryakur system or SARLaC into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Seraph_system, Filters.Ulress_system,
                            Filters.Danktooine_system, Filters.Ragnath_system, Filters.Myryakur_system, Filters.SARLAC), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}