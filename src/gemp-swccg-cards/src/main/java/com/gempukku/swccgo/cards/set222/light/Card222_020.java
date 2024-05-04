package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectOrderEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Character
 * Subtype: Rebel
 * Title: Echo Base Trooper (V)
 */
public class Card222_020 extends AbstractRebel {
    public Card222_020() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Echo Base Trooper", Uniqueness.UNRESTRICTED, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLore("The personnel assigned to protect Echo Base are veteran warriors. Troopers such as Jess Allashane are trained to counter Imperial tactics in cold environment.");
        setGameText("Power +1 on Hoth. Once per turn, may [download] a card with 'Echo' in title (except Echo Base Trooper or a location). " +
                "Once per turn, if on Hoth (or present with a Scomp link), may reveal the top card of each player's Reserve Deck.");
        addIcons(Icon.HOTH, Icon.WARRIOR, Icon.VIRTUAL_SET_22);
        addKeywords(Keyword.ECHO_BASE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OnCondition(self, Title.Hoth), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.ECHO_BASE_TROOPER_V__DEPLOY_ECHO_CARD;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card with 'Echo' in title");
            action.setActionMsg("Deploy a non-location card with 'Echo' in title from Reserve Deck");

            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.not(Filters.or(Filters.title("Echo Base Trooper"), Filters.location)), Filters.or(Filters.titleContains("Echo"), Filters.titleContains("Echos"), Filters.titleContains("Echoes"))), true));

            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        String opponent = game.getOpponent(playerId);

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.hasReserveDeck(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal top card of Reserve Decks");
            action.setActionMsg("Reveal top card of each player's Reserve Deck");

            action.appendUsage(
                    new OncePerTurnEffect(action));

            List<StandardEffect> effects = new LinkedList<>();
            effects.add(
                    new RevealTopCardOfReserveDeckEffect(action, playerId, playerId));
            effects.add(
                    new RevealTopCardOfReserveDeckEffect(action, playerId, opponent));

            action.appendEffect(
                    new ChooseEffectOrderEffect(action, effects));

            actions.add(action);
        }

        return actions;
    }
}
