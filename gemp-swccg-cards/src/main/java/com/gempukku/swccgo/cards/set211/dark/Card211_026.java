package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 11
 * Type: Objective
 * Title: A Stunning Move / A Valuable Hostage
 */
public class Card211_026 extends AbstractObjective {
    public Card211_026() {
        super(Side.DARK, 0, Title.A_Stunning_Move);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy 500 Republica (with Insidious Prisoner there) and Private Platform.\n" +
                "For remainder of game, you may not deploy Sidious, First Order characters, or Imperials. Grievous is immunity to attrition +2. Once per turn, may \\/ an Invisible Hand site or a non-unique [Separatist] droid. \n" +
                "Flip this card if Insidious Prisoner is at an Invisible Hand site.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction actions = new ObjectiveDeployedTriggerAction(self);
        actions.appendRequiredEffect(getDeployCardFromReserveDeckEffect(actions, Filters._500_Republica, "Choose 500 Republica to deploy"));
        actions.appendRequiredEffect(getDeployCardToTargetFromReserveDeckEffect(actions, Filters.Insidious_Prisoner, Filters._500_Republica, "Choose Insidious Prisoner to deploy to 500 Republica"));
        actions.appendRequiredEffect(getDeployCardFromReserveDeckEffect(actions, Filters.Private_Platform, "Choose Private Platform to deploy"));
        return actions;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        Filter cardsThatMayNotDeployForRemainderOfGame = Filters.or(Filters.Sidious, Filters.First_Order_character, Filters.Imperial);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, cardsThatMayNotDeployForRemainderOfGame, playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ImmunityToAttritionChangeModifier(self, Filters.Grievous, 2), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // This is technically text for the front side, but it seemed needlessly complicated
        //   to attempt to code it as a AddUntilEndOfGameModifierEffect. If there ever is a
        //   "place out of play" condition for this objective, this will need to change.
        GameTextActionId gameTextActionId = GameTextActionId.A_STUNNING_MOVE__DOWNLOAD_SITE_OR_NONUNIQUE_SEPARATIST_DROID;
        Filter invisibleHandSite = Filters.siteOfStarshipOrVehicle(Persona.INVISIBLE_HAND, true);
        Filter separatistDroid = Filters.and(Icon.SEPARATIST, Filters.droid);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy an Invisible Hand site or non-unique [Separatist] Droid");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(invisibleHandSite, separatistDroid), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard insidiousPrisoner = Filters.findFirstActive(game, self, Filters.Insidious_Prisoner);
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (GameConditions.isAttachedTo(game, insidiousPrisoner, Filters.Invisible_Hand_site))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}