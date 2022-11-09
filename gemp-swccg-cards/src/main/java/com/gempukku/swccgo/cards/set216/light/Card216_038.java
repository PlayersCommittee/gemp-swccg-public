package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CommuningCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Jedi Master
 * Title: Master Yoda
 */
public class Card216_038 extends AbstractJediMaster {
    public Card216_038() {
        super(Side.LIGHT, 1, 5, 2, 7, 9, Title.Master_Yoda, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLore("");
        setGameText("While 'communing': You may not deploy Jedi Knights or [Maintenance] cards; [Dagobah] Luke is deploy -1 and power and defense value +1; once per turn, may [download] a battleground with two [Dark Side]; once per game, may retrieve 1 Force.");
        addIcons(Icon.VIRTUAL_SET_16);
        addPersona(Persona.YODA);
    }

    public List<Modifier> getWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.Jedi_Knight, Icon.MAINTENANCE), new CommuningCondition(self), self.getOwner()));
        modifiers.add(new DeployCostModifier(self, Filters.and(Icon.DAGOBAH, Filters.Luke), new CommuningCondition(self), -1));
        modifiers.add(new PowerModifier(self, Filters.and(Icon.DAGOBAH, Filters.Luke), new CommuningCondition(self), 1));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Icon.DAGOBAH, Filters.Luke), new CommuningCondition(self), 1));

        return modifiers;
    }

    public List<TopLevelGameTextAction> getGameTextTopLevelWhileStackedActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MASTER_YODA__DEPLOY_BATTLEGROUND;
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        if (game.getModifiersQuerying().isCommuning(game.getGameState(), self)) {
            if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);

                action.setText("Deploy battleground from Reserve Deck");
                action.setActionMsg("Deploy a battleground with two [Dark Side] from Reserve Deck ");
                action.appendUsage(new OncePerTurnEffect(action));
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.battleground, Filters.iconCount(Icon.DARK_FORCE, 2)), true));

                actions.add(action);
            }
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.MASTER_YODA__RETRIEVE_FORCE;

        if (game.getModifiersQuerying().isCommuning(game.getGameState(), self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId2)
                && GameConditions.hasLostPile(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);

            action.setText("Retrieve 1 Force");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new RetrieveForceEffect(action, playerId, 1));

            actions.add(action);
        }
        return actions;
    }
}