package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostUsingDejarikRulesModifier;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Objective
 * Title: Watch Your Step / This Place Can Be A Little Rough
 */
public class Card10_026 extends AbstractObjective {
    public Card10_026() {
        super(Side.LIGHT, 0, Title.Watch_Your_Step);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Cantina, Docking Bay 94 and Tatooine system. For remainder of game, opponent activates no Force at your Cantina. Your cards with ability are deploy +6 except Luke, smugglers, freighters, and [Independent] starships. Opponent's game text on Kessel is canceled. While this side up, once during each of your deploy phases, may take Corellia or Kessel into hand from Reserve Deck; reshuffle. Flip this card if you occupy two battlegrounds with smugglers or have completed two Kessel Runs.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Cantina, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Cantina to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Docking_Bay_94, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Docking Bay 94 to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Tatooine_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Tatooine system to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new GenerateNoForceModifier(self, Filters.and(Filters.your(self), Filters.Cantina), opponent), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new DeployCostModifier(self, Filters.and(Filters.your(self), Filters.hasAbilityOrHasPermanentPilotWithAbility,
                                Filters.not(Filters.or(Filters.Luke, Filters.smuggler, Filters.freighter, Filters.and(Icon.INDEPENDENT, Filters.starship)))), 6), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new DeployCostUsingDejarikRulesModifier(self, Filters.and(Filters.your(self), Filters.hasAbilityWhenUsingDejarikRules), 6), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new CancelsGameTextOnSideOfLocationModifier(self, Filters.Kessel_system, opponent), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WATCH_YOUR_STEP__UPLOAD_CORELLIA_OR_KESSEL;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Corellia or Kessel into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Corellia_system, Filters.Kessel_system), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (GameConditions.hasCompletedUtinniEffect(game, playerId, 2, Filters.Kessel_Run)
                || GameConditions.occupiesWith(game, self, playerId, 2, Filters.battleground, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.smuggler))) {

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