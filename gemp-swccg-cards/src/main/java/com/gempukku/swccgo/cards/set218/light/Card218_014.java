package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Effect
 * Title: A Good Blaster At Your Side & Restricted Deployment
 */
public class Card218_014 extends AbstractNormalEffect {
    public Card218_014() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Good Blaster At Your Side & Restricted Deployment", Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        addComboCardTitles("A Good Blaster At Your Side", Title.Restricted_Deployment);
        setGameText("Deploy on table. Non-lightsaber weapons carried by your non-Jedi characters may not be stolen. During your control phase, if you control a battleground site with a non-unique, non-[Permanent Weapon] blaster present, opponent loses 1 Force. Rebel Artillery is a Lost Interrupt. During your deploy phase, may [download] a blaster (or use 2 Force to deploy a non-unique blaster from Lost Pile) on your character at a Death Star site. May not be canceled. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_18);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter weapons = Filters.and(Filters.weapon, Filters.not(Filters.lightsaber), Filters.attachedTo(Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.Jedi))));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeStolenModifier(self, weapons));
        modifiers.add(new LostInterruptModifier(self, Filters.Rebel_Artillery));
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int numForce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground_site, Filters.controls(playerId),
                    Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.blaster, Filters.not(Icon.PERMANENT_WEAPON)))));
            if (numForce > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose 1 Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 1));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.A_GOOD_BLASTER_AT_YOUR_SIDE_COMBO__DEPLOY_BLASTER;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.Death_Star_site)))) {

            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy blaster from Reserve Deck");
                action.setActionMsg("Deploy a blaster from Reserve Deck on your character at a Death Star site");

                action.appendUsage(
                        new OncePerTurnEffect(action));
                action.appendEffect(
                        new DeployCardToTargetFromReserveDeckEffect(action, Filters.blaster, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.Death_Star_site)), true));

                actions.add(action);
            }

            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)
                    && GameConditions.canUseForce(game, playerId, 2)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy blaster from Lost Pile");
                action.setActionMsg("Deploy a non-unique blaster from Lost Pile on your character at a Death Star site");

                action.appendUsage(
                        new OncePerTurnEffect(action));
                action.appendCost(
                        new UseForceEffect(action, playerId, 2));
                action.appendEffect(
                        new DeployCardToTargetFromLostPileEffect(action, Filters.and(Filters.non_unique, Filters.blaster), Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.Death_Star_site)), false, false));

                actions.add(action);
            }

        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int numForce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground_site, Filters.controls(playerId),
                    Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.blaster, Filters.not(Icon.PERMANENT_WEAPON)))));
            if (numForce > 0) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose 1 Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}