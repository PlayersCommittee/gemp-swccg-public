package com.gempukku.swccgo.cards.set111.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddToBlownAwayForceLossEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Third Anthology) 
 * Type: Objective 
 * Title: Set Your Course For Alderaan / The Ultimate Power In The Universe
 */
public class Card111_006_BACK extends AbstractObjective {
    public Card111_006_BACK() {
        super(Side.DARK, 7, Title.The_Ultimate_Power_In_The_Universe);
        setGameText("While this side up, once during each of your deploy phases, may deploy one battleground system from Reserve Deck; reshuffle. Your Star Destroyers deploy -2 (or -1 if Victory class) to Death Star system. Your Force drains at battleground systems where you have a Star Destroyer are each +2 (or +1 if Victory class). If Yavin 4 system is 'blown away,' adds 3 to Force lost for each opponent's Yavin 4 site. Place out of play if Death Star is 'blown away.'");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Yavin_4, true)))) {
            int amountToAddToForceLoss =
                3 * Filters.countTopLocationsOnTable(game,
                        Filters.and(Filters.opponents(self), Filters.Yavin_4_site,
                            Filters.notIgnoredDuringEpicEventCalculation));
            if (amountToAddToForceLoss > 0) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.skipInitialMessageAndAnimation();

                // Perform result(s)
                action.appendEffect(new AddToBlownAwayForceLossEffect(action, game.getOpponent(self.getOwner()),
                        amountToAddToForceLoss));
                return Collections.singletonList(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Death_Star, true)))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");

            // Perform result(s)
            action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game,
            final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId =
            GameTextActionId.THE_ULTIMATE_POWER_IN_THE_UNIVERSE__DOWNLOAD_BATTLEGROUND_SYSTEM;

        // Check condition(s)
        if (
            GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId,
                    Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action =
                new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy battleground system from Reserve Deck");
            action.setActionMsg("Deploy a battleground system from Reserve Deck");

            // Update usage limit(s)
            action.appendUsage(new OncePerPhaseEffect(action));

            // Perform result(s)
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.system, Filters.battleground,
                    true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Star_Destroyer, new CardMatchesEvaluator(-2, -1, Filters.Victory_class_Star_Destroyer), Filters.Death_Star_system));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.battleground_system, Filters.sameSystemAs(self, Filters.Star_Destroyer)), new CardMatchesEvaluator(2, 1, Filters.and(Filters.battleground_system, Filters.sameSystemAs(self, Filters.Victory_class_Star_Destroyer))),
                playerId));
        return modifiers;
    }
}
