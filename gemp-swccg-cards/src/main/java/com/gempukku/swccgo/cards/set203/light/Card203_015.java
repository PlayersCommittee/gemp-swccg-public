package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Effect
 * Title: The Shield Is Down! (V)
 */
public class Card203_015 extends AbstractNormalEffect {
    public Card203_015() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Shield Is Down!", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Lando's confidence in the Rebel strike team on the forest moon was rewarded, and proved to be a decisive factor at the Battle of Endor.");
        setGameText("Deploy on table. Your scouts move for free to and from Back Door when using their landspeed. Once per turn, may [download] an Endor battleground site. While an Explosive Charge at Bunker, flip Rebel Strike Team and Garrison Destroyed may not flip. [Immune to Alter]");
        addIcons(Icon.TATOOINE, Icon.VIRTUAL_SET_3);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourScouts = Filters.and(Filters.your(self), Filters.scout);
        Filter backDoor = Filters.Back_Door;
        Condition explosiveChargeAtBunker = new AtCondition(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Explosive_Charge, Filters.Bunker);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeToLocationUsingLandspeedModifier(self, yourScouts, backDoor));
        modifiers.add(new MovesFreeFromLocationUsingLandspeedModifier(self, yourScouts, backDoor));
        modifiers.add(new MayNotBeFlippedModifier(self, explosiveChargeAtBunker, Filters.Garrison_Destroyed));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_SHIELD_IS_DOWN__DOWNLOAD_ENDOR_BATTLEGROUND_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Endor battleground site from Reserve Deck");
            action.setActionMsg("Deploy an Endor battleground site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Endor_site, Filters.battleground, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Explosive_Charge, Filters.at(Filters.Bunker)))) {
            PhysicalCard rst = Filters.findFirstActive(game, self, Filters.Rebel_Strike_Team);
            if (rst != null
                    && GameConditions.canBeFlipped(game, rst)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setSingletonTrigger(true);
                action.setText("Flip " + GameUtils.getFullName(rst));
                action.setActionMsg("Flip " + GameUtils.getCardLink(rst));
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, rst));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}