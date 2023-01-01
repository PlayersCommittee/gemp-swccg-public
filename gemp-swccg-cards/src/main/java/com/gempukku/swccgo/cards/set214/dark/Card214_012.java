package com.gempukku.swccgo.cards.set214.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardsOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Effect
 * Title: The First Order Was Just The Beginning
 */
public class Card214_012 extends AbstractNormalEffect {
    public Card214_012() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The First Order Was Just The Beginning", Uniqueness.UNIQUE, ExpansionSet.SET_14, Rarity.V);
        setGameText("Deploy on table. Your [Episode VII] troopers are forfeit +1. If [Episode VII] Emperor on table, once per turn may deploy Kijimi from Reserve Deck (reshuffle) or place any three cards out of play from your Lost Pile to deploy a non-unique [Episode VII] trooper from Lost Pile. [Immune to Alter.]");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_14);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.icon(Icon.EPISODE_VII), Filters.trooper), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_FIRST_ORDER_WAS_JUST_THE_BEGINNING__DOWNLOAD_KIJIMI_OR_DEPLOY_TROOPER_FROM_LOST_PILE;

        List<TopLevelGameTextAction> actions = new LinkedList<>();

        if (GameConditions.canSpot(game, self, Filters.and(Filters.icon(Icon.EPISODE_VII), Filters.Emperor))
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)) {

            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Kijimi)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Kijimi from Reserve Deck");
                action.setActionMsg("Deploy Kijimi from Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Kijimi), true));

                actions.add(action);
            }

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)
                    && GameConditions.numCardsInLostPile(game, playerId) >= 3) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy [Episode VII] trooper from Lost Pile");
                action.setActionMsg("Deploy [Episode VII] trooper from Lost Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));

                action.appendCost(new PlaceCardsOutOfPlayFromLostPileEffect(action, playerId, playerId, 3, 3, false));
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.and(Filters.icon(Icon.EPISODE_VII), Filters.non_unique, Filters.trooper), false));

                actions.add(action);
            }
        }
        return actions;
    }
}