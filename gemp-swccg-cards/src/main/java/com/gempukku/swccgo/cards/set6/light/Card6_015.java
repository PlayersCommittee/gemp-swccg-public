package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Geezum
 */
public class Card6_015 extends AbstractAlien {
    public Card6_015() {
        super(Side.LIGHT, 2, 3, 2, 1, 2, "Geezum", Uniqueness.UNIQUE);
        setLore("Snivvian scout. Enjoys exploring new planets and charting difficult terrain. Paid very well by Jabba to do so. Unsure of his employer's motivations.");
        setGameText("During your control phase, if at an exterior planet site, may use 2 Force to search your Reserve Deck, take a related site into hand and reshuffle. While at Audience Chamber, all your other Snivvians are forfeit +2.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.SNIVVIAN);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GEEZUM__UPLOAD_RELATED_SITE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.exterior_planet_site)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PhysicalCard site = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            if (site != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card into hand from Reserve Deck");
                action.setActionMsg("Take a related site into hand from Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 2));
                // Perform result(s)
                action.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.site, Filters.relatedLocationEvenWhenNotInPlay(site)), true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Snivvian),
                new AtCondition(self, Filters.Audience_Chamber), 2));
        return modifiers;
    }
}
