package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Character
 * Subtype: Alien
 * Title: Wuta (V)
 */
public class Card225_010 extends AbstractAlien {
    public Card225_010() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, Title.Wuta, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Ewok explorer. Scout. Searches for fallen trees to make tools. Tracks predators. First to notice the Imperial presence on Endor.");
        setGameText("Deploys only on Endor. Once per game, may [upload] an Endor site. Game text of your other scouts here may not be canceled. During any deploy phase, if an Imperial at an adjacent site, Wuta may move to that site (using landspeed) as a regular move.");
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_25);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.EWOK);
        setVirtualSuffix(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Endor;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter yourOtherScountsAtSameSite = Filters.and(Filters.your(self), Filters.other(self), Filters.scout, Filters.atSameSite(self));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, yourOtherScountsAtSameSite));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.WUTA__UPLOAD_ENDOR_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take an Endor site into hand from Reserve Deck");
            action.setActionMsg("Take an Endor site into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Endor_site, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.DEPLOY)
            && Filters.movableAsRegularMove(playerOnLightSideOfLocation, false, 0, false, Filters.and(Filters.adjacentSite(self), Filters.sameSiteAs(self, Filters.Imperial))).accepts(game, self)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Move Wuta to an Imperial");
            action.appendTargeting(
                new ChooseCardOnTableEffect(action, playerOnLightSideOfLocation, "Choose site to move to", Filters.and(Filters.adjacentSite(self), Filters.sameSiteAs(self, Filters.Imperial))) {
                    @Override
                    protected void cardSelected(PhysicalCard targetSite) {
                        action.addAnimationGroup(self);
                        action.setActionMsg("Move " + GameUtils.getCardLink(self) + " to an adjacent site where there is an Imperial");
                                // Perform result(s)
                        action.appendEffect(
                        new MoveCardAsRegularMoveEffect(action, playerOnLightSideOfLocation, self, false, false, Filters.sameTitle(targetSite)));
                    }
                }
            );
            actions.add(action);
        }
        return actions;
    }
}
