package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceBottomCardOfLostPileOnTopOfForcePileEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceForcePileOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Epic Event
 * Title: I Did It!
 */
public class Card11_025 extends AbstractEpicEventDeployable {
    public Card11_025() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.I_Did_It, Uniqueness.UNRESTRICTED, ExpansionSet.TATOOINE, Rarity.R);
        setGameText("Deploy on table if you've won a Podrace. Once per game, may place Boonta Eve Podrace out of play to retrieve 4 Force. If you occupy a battleground site and a battleground system, once during each of your control phases may reveal the bottom card of your Lost Pile and place it on top of your Force Pile. At the end of opponent's turn opponent must lose 2 Force or place their Force Pile onto their Used Pile.");
        addIcons(Icon.EPISODE_I, Icon.TATOOINE);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.hasWonPodrace(game, playerId);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.I_DID_IT__RETRIEVE_FORCE;
        TargetingReason targetingReason = TargetingReason.TO_BE_PLACED_OUT_OF_PLAY;
        Filter podraceFilter = Filters.and(Filters.your(self), Filters.Boonta_Eve_Podrace);
        
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTarget(game, self, targetingReason, podraceFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Boonta Eve Podrace out of play");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Boonta Eve Podrace", targetingReason, podraceFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            // Pay cost(s)
                            action.appendCost(
                                    new PlaceCardOutOfPlayFromTableEffect(action, targetedCard));
                            // Allow response(s)
                            action.allowResponses("Retrieve 4 Force",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(final Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RetrieveForceEffect(action, playerId, 4));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasLostPile(game, playerId)
                && GameConditions.occupies(game, playerId, Filters.battleground_site)
                && GameConditions.occupies(game, playerId, Filters.battleground_system)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place bottom card of Lost Pile on Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlaceBottomCardOfLostPileOnTopOfForcePileEffect(action, playerId, false));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(opponent);
            action.setText("Lose 2 Force or place Force Pile on Used Pile");
            // Perform result(s)
            List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
            effectsToChoose.add(new LoseForceEffect(action, opponent, 2, true));
            effectsToChoose.add(new PlaceForcePileOnUsedPileEffect(action, opponent));
            action.appendEffect(
                    new ChooseEffectEffect(action, opponent, effectsToChoose));
            return Collections.singletonList(action);
        }
        return null;
    }
}