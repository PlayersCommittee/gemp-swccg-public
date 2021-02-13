package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Effect
 * Title: Quick Draw (V)
 */
public class Card601_056 extends AbstractNormalEffect {
    public Card601_056() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Quick Draw");
        setVirtualSuffix(true);
        setLore("It's high noon on Dagobah, his droid's reined in and Luke's got a fistful of credits.");
        setGameText("Unless Inner Strength on table, deploy on table.  Your lightsabers may target vehicles using 1 Force (each destiny draw is -2).  Once per turn, may lose 1 Force to deploy a character weapon (except a grenade) from Lost Pile.  Once per turn, may deploy [Virtual Block 1] Sai'torr Kal Fas from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.BLOCK_4, Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !Filters.canSpot(game, self, Filters.Inner_Strength);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.lightsaber), Filters.and(Filters.opponents(self), Filters.vehicle)));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(self), Filters.lightsaber), Filters.any, -2, Filters.and(Filters.opponents(self), Filters.vehicle)));
        modifiers.add(new FireWeaponFiredAtCostModifier(self, 1, Filters.and(Filters.your(self), Filters.lightsaber), Filters.and(Filters.opponents(self), Filters.vehicle)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__QUICK_DRAW__DEPLOY_WEAPON_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {


            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy weapon from Lost Pile");
            action.setActionMsg("Deploy character weapon from Lost Pile");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromLostPileEffect(action, Filters.and(Filters.character_weapon, Filters.except(Filters.Concussion_Grenade)), false));
            actions.add(action);

        }

        GameTextActionId gameTextActionId2 = GameTextActionId.LEGACY__QUICK_DRAW__DEPLOY_SAITORR_KAL_FAS;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId2)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId2, Title.Saitorr_Kal_Fas)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);
            action.setText("Deploy Sai'torr Kal Fas");
            action.setActionMsg("Deploy [Block 1] Sai'torr Kal Fas");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.BLOCK_1, Filters.Saitorr_Kal_Fas), true));
            actions.add(action);
        }

        return actions;
    }
}