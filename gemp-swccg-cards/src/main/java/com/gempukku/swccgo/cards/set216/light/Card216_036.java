package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CommuningCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Rebel
 * Title: Master Kenobi
 */
public class Card216_036 extends AbstractRebel {
    public Card216_036() {
        super(Side.LIGHT, 1, 5, 5, 6, 9, Title.Master_Kenobi, Uniqueness.UNIQUE);
        setLore("'Luminous beings are we, not this crude matter.' The inner consciousness of a Jedi can transcend even death.");
        setGameText("While 'communing': You may not deploy Jedi (except Yoda) or [Permanent Weapon] cards; if a Rebel in battle, may use 1 Force to add 3 to your total power (5 if Luke); once per turn, may [download] a battleground that is related to a location on table.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_16);
        addPersona(Persona.OBIWAN);
    }

    public List<Modifier> getWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.and(Filters.Jedi, Filters.except(Filters.Yoda)), Icon.PERMANENT_WEAPON), new CommuningCondition(self), self.getOwner()));
        return modifiers;
    }

    public List<TopLevelGameTextAction> getGameTextTopLevelWhileStackedActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.MASTER_KENOBI__DEPLOY_BATTLEGROUND;

        if (game.getModifiersQuerying().isCommuning(game.getGameState(), self)) {
            if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);

                action.setText("Deploy battleground from Reserve Deck");
                action.setActionMsg("Deploy a battleground from Reserve Deck that is related to a location on table");
                action.appendUsage(new OncePerTurnEffect(action));

                Collection<PhysicalCard> locationsOnTable = Filters.filterTopLocationsOnTable(game, Filters.any);
                Collection<PhysicalCard> reserveDeck = game.getGameState().getReserveDeck(playerId);
                Collection<PhysicalCard> locationsToDeployFromReserveDeck = new LinkedList<>();
                for (PhysicalCard c : locationsOnTable) {
                    locationsToDeployFromReserveDeck.addAll(Filters.filter(reserveDeck, game, Filters.relatedLocationEvenWhenNotInPlay(c)));
                }

                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.battleground, Filters.in(locationsToDeployFromReserveDeck)), true));

                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Rebel))
                && GameConditions.canUseForce(game, playerId, 1)) {

            int toAdd = 3;
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Rebel, Filters.Luke)))
                toAdd = 5;

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add " + toAdd + " to total power");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new ModifyTotalPowerUntilEndOfBattleEffect(action, toAdd, playerId, "Add " + toAdd + " to total power"));

            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.MASTER_KENOBI__SUBTRACT_FROM_BATTLE_DESTINY_IF_POWER_MODIFIED)) {
                action.appendEffect(
                        new ModifyTotalBattleDestinyEffect(action, game.getOpponent(playerId), -1, true));
            }
            actions.add(action);
        }

        return actions;
    }
}