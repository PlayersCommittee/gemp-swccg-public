package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.TopLocationsOnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Character
 * Subtype: Alien
 * Title: Harc Seff (V)
 */
public class Card200_015 extends AbstractAlien {
    public Card200_015() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Harc, Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setArmor(3);
        setLore("Ishi Tib accountant. Experienced administrator. Freelance consultant. Worked for many planetary governors. Knows how to get the most out of any taxation scheme.");
        setGameText("[Pilot] 2. For each of opponent's non-battleground locations on table, your Force generation is +1. Once per game, may [upload] an Immediate Effect.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.ACCOUNTANT);
        setSpecies(Species.ISHI_TIB);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalForceGenerationModifier(self, new TopLocationsOnTableEvaluator(Filters.and(Filters.opponents(self), Filters.non_battleground_location)), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HARC_SEFF__UPLOAD_IMMEDIATE_EFFECT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Immediate Effect into hand from Reserve Deck");
            action.setActionMsg("Take an Immediate Effect into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Immediate_Effect, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
