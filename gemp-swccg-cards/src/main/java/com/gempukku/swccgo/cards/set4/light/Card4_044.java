package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Yoda's Hope
 */
public class Card4_044 extends AbstractNormalEffect {
    public Card4_044() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Yodas_Hope, Uniqueness.UNIQUE);
        setLore("'You must feel the Force around you. Here, between you, me, the tree, the rock, everywhere! Yes, even between the land and the ship.'");
        setGameText("Deploy on a character. When on the mentor, adds 1 to training destiny draws. If on Yoda, you may lose Effect to search your Reserve Deck and take into hand one card with 'levitation' in the title. Shuffle, cut and replace.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.character;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachTrainingDestinyModifier(self, Filters.jediTestTargetingMentor(Filters.hasAttached(self)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.YODAS_HOPE__UPLOAD_LEVITATION_CARD;

        // Check condition(s)
        if (GameConditions.isAttachedTo(game, self, Filters.Yoda)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a card with 'levitation' in title into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new LoseCardFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.titleContains("levitation"), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}