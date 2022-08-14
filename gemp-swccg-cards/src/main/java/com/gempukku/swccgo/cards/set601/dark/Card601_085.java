package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.*;

/**
 * Set: Block 4
 * Type: Effect
 * Title: Gift Of The Master
 */
public class Card601_085 extends AbstractNormalEffect {
    public Card601_085() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Gift Of The Master", Uniqueness.UNIQUE);
        setLore("'Give yourself to the dark side. It is the only way you can save your friends.'");
        setGameText("Unless Deep Hatred on table, deploy on table.  Once per turn, may deploy Blaster Rack or The Force Unleashed from Reserve Deck; reshuffle.  Once per turn, may lose 1 Force to deploy a character weapon (except Thermal Detonator) from Lost Pile.  'Vader' on The Empire's Back may be treated as 'Galen.' (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_4);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !Filters.canSpot(game, self, Filters.Deep_Hatred);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.title("The Empire's Back"), ModifyGameTextType.LEGACY__THE_EMPIRES_BACK__VADER_MAY_BE_TREATED_AS_GALEN));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__GIFT_OF_THE_MASTER__DEPLOY_BLASTER_RACK_OR_FORCE_UNLEASHED;
        List<String> titles = Arrays.asList(Title.Blaster_Rack, "The Force Unleashed");
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, titles)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Blaster Rack or The Force Unleashed");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Blaster_Rack, Filters.title("The Force Unleashed")), true));

            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__GIFT_OF_THE_MASTER__DEPLOY_WEAPON_FROM_LOST_PILE;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a weapon from Lost Pile");
            action.setActionMsg("Deploy a character weapon from Lost Pile");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new DeployCardFromLostPileEffect(action, Filters.and(Filters.character_weapon, Filters.not(Filters.title("Thermal Detonator"))), false));

            actions.add(action);
        }

        return actions;
    }
}