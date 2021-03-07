package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Effect
 * Title: Jabba's Haven
 */
public class Card601_010 extends AbstractNormalEffect {
    public Card601_010() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Jabba's Haven", Uniqueness.UNIQUE);
        setLore("Jabba has won the service of many of his guards and other henchbeings through games of chance.");
        setGameText("Deploy on table. May deploy Nal Hutta from Reserve Deck; reshuffle. Once per game, may retrieve an alien or [Independent] starship into hand. While Fearless And Inventive on table, once per battle, may lose 1 force; your battle destiny modifiers affect your total battle destiny instead. [Immune to Alter.]");
        addIcons(Icon.LEGACY_BLOCK_7, Icon.JABBAS_PALACE);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.JABBAS_HAVEN__DOWNLOAD_NAL_HUTTA;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Nal_Hutta)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Nal Hutta from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Nal_Hutta_system, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.Fearless_And_Inventive)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Change your battle destiny modifiers");
            action.setActionMsg("Make " + playerId + "'s battle destiny modifiers affect " + playerId + "'s total battle destiny instead");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new SpecialFlagModifier(self, ModifierFlag.BATTLE_DESTINY_MODIFIERS_AFFECT_TOTAL_BATTLE_DESTINY_INSTEAD, playerId),
                            "Makes " + playerId + "'s battle destiny modifiers affect " + playerId + "'s total battle destiny instead"));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.JABBAS_HAVEN__RETRIEVE_CARD_INTO_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve card into hand");
            action.setActionMsg("Retrieve an alien or [Independent] starship into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, Filters.or(Filters.alien, Filters.and(Filters.starship, Icon.INDEPENDENT))));
            actions.add(action);
        }

        return actions;
    }
}