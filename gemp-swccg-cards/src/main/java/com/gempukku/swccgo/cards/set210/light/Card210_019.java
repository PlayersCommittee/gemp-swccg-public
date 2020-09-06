package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.filters.Filter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Character
 * Subtype: Jedi Master
 * Title: Kit Fisto
 */
public class Card210_019 extends AbstractJediMaster {
    public Card210_019() {
        super(Side.LIGHT, 1, 6, 5, 7, 7, "Kit Fisto", Uniqueness.UNIQUE);
        setLore("Nautolan Jedi Council member.");
        setGameText("[Pilot] 2. Deploys -1 to an [Episode I] site (-2 if underwater). Landspeed = 2. Once per turn, may use 1 Force (or place your combat card here in Used Pile) to draw top card of Reserve Deck. Immune to attrition < 5.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.EPISODE_I, Icon.WARRIOR, Icon.PILOT);
        setSpecies(Species.NAUTOLAN);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter  EP1_UNDERWATER = Filters.and(Icon.EPISODE_I, Icon.UNDERWATER);
        Filter  EP1_NOT_UNDERWATER = Filters.and(Icon.EPISODE_I, Filters.not(Icon.UNDERWATER));
        modifiers.add(new DeployCostToLocationModifier(self, -1, EP1_NOT_UNDERWATER));
        modifiers.add(new DeployCostToLocationModifier(self, -2, EP1_UNDERWATER));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Use 1 Force to draw");
            action.setActionMsg("Draw top card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            actions.add(action);
        }

        Filter yourCharactersWithCombatCardsHere = Filters.and(Filters.here(self), Filters.your(playerId), Filters.hasStacked(Filters.combatCard));
        Collection<PhysicalCard> combatCards = Filters.filterStacked(game, Filters.and(Filters.combatCard, Filters.stackedOn(self, yourCharactersWithCombatCardsHere)));

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)
                && combatCards.size() > 0) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Combat Card in Used Pile to draw");
            action.setActionMsg("Draw top card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, Filters.any, Filters.in(combatCards)) {
                        @Override
                        protected void cardSelected(final PhysicalCard combatCard) {
                            // Pay cost(s)
                            action.appendCost(
                                    new PutStackedCardInUsedPileEffect(action, playerId, combatCard, true));
                            action.appendEffect(
                                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
