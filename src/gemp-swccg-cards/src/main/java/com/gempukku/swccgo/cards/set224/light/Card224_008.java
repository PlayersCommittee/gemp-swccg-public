package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Effect
 * Title: A Cunning Warrior
 */
public class Card224_008 extends AbstractNormalEffect {
    public Card224_008() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Cunning Warrior", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Luke's experience on Dagobah gave him great skill in using the Force. Vader had to keep his focus on Luke at all times, or face the consequences.");
        setGameText("If your [Skywalker] Epic Event on table, deploy on table. Where you have a Skywalker, you initiate battles for free. Once per turn, may [download] Anakin's Lightsaber or a [Cloud City] corridor. Once per battle involving a Jedi Skywalker, may activate 1 Force. [Immune to Alter.]");
        addIcons(Icon.SKYWALKER, Icon.VIRTUAL_SET_24);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.your(self), Icon.SKYWALKER, Filters.Epic_Event));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        String playerId = self.getOwner();
        modifiers.add(new InitiateBattlesForFreeModifier(self, Filters.sameLocationAs(self, Filters.Skywalker), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.A_CUNNING_WARRIOR__DEPLOY_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter anakinsLightsaber = Filters.and(Filters.weapon, Filters.persona(Persona.ANAKINS_LIGHTSABER));
            Filter cloudCityCorridor = Filters.and(Icon.CLOUD_CITY, Filters.titleContains("Corridor"));

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Anakin's Lightsaber or a [Cloud City] corridor from Reserve Deck");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(anakinsLightsaber, cloudCityCorridor), true));

            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Jedi, Filters.Skywalker))
                && GameConditions.canActivateForce(game, playerId)) {
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            actions.add(action);
        }

        return actions;
    }

}
