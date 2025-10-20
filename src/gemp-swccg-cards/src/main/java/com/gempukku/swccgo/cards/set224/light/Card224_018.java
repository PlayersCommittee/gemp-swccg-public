package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 24
 * Type: Character
 * Subtype: Alien
 * Title: Jedi Marshal Avar Kriss
 */
public class Card224_018 extends AbstractJediMaster {
    public Card224_018() {
        super(Side.LIGHT, 2, 5, 5, 7, 7, "Jedi Marshal Avar Kriss", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Female musician. High Republic.");
        setGameText("[Pilot] 2, 3: any capital starship. While piloting a capital starship, it is immune to attrition < 5. Once per game, may [upload] (or retrieve) a musician. Immune to attrition < 5.");
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_24);
        addKeywords(Keyword.FEMALE, Keyword.MUSICIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.capital_starship)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.capital_starship, Filters.hasPiloting(self)), 5));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.AVAR_KRISS__MUSICIAN;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            // Check condition(s)
            if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId, true)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Retrieve a musician");
                action.appendUsage(
                    new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new RetrieveCardEffect(action, playerId, Filters.musician));
                // Perform result(s)
                actions.add(action);
            }
            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take a musician into hand from Reserve Deck");
                action.appendUsage(
                    new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.musician, true));
                // Perform result(s)
                actions.add(action);
            }
        }
        return actions;
    }

}