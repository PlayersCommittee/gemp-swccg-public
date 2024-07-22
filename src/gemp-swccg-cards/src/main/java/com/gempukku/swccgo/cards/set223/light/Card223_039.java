package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 23
 * Type: Effect
 * Subtype: Normal
 * Title: Jabba's Last Chance
 */
public class Card223_039 extends AbstractNormalEffect {
    public Card223_039() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Jabba's Last Chance", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Jabba! This is your last chance. Free us or die.");
        setGameText("If Han is frozen, deploy on table. " +
                "Chewie, Lando, and Leia are immune to attrition < 4. " +
                "Once per turn, may deploy a unique character weapon from Reserve Deck; reshuffle. " +
                "Once per game, if R2-D2 in battle, may exchange a card from hand with any card from Reserve Deck; reshuffle. [Immune to Alter.]");
        addIcon(Icon.VIRTUAL_SET_23);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Han, Filters.frozenCaptive));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.or(Filters.Chewie, Filters.Lando, Filters.Leia), 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.JABBAS_LAST_CHANCE__DOWNLOAD_CHARACTER_WEAPON;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
            && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a unique character weapon from Reserve Deck");
            action.setActionMsg("Deploy a unique character weapon from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.unique, Filters.character_weapon), true)
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.JABBAS_LAST_CHANCE__EXCHANGE_CARD_WITH_CARD_IN_RESERVE_DECK;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId2)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.R2D2)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);
            action.setText("Exchange a card");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithCardInReserveDeckEffect(action, playerId, Filters.any, Filters.any, true)
            );
            actions.add(action);
        }
        return actions;
    }
}
