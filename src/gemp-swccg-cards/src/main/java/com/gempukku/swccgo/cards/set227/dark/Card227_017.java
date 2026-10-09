package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CostToDrawDestinyCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Character
 * Subtype: Imperial
 * Title: Warrant Officer Bachenkall
 */
public class Card227_017 extends AbstractImperial {
    public Card227_017() {
        super(Side.DARK, 2, 2, 2, 2, 4, "Warrant Officer Bachenkall", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLore("Warrant Officer Bachenkall is typical of the many graduates of the Imperial Training Academy on Raithal. The sector naval school trains pilots in capital starship helm tactics.");
        setGameText("[Pilot] 2. Devastator deploys -2 here. While piloting a Star Destroyer, it is hyperspeed +1 and opponent must first use 1 Force (if possible) to draw a card for battle destiny here. Once per game, may deploy Imperial Pilot here from outside your deck.");
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingStarDestroyer = new PilotingCondition(self, Filters.Star_Destroyer);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Devastator, -2, Filters.here(self)));
        modifiers.add(new HyperspeedModifier(self, Filters.hasPiloting(self), pilotingStarDestroyer, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isCheckingCostsToDrawBattleDestiny(game, effectResult, opponent)
            && GameConditions.isPiloting(game, self, Filters.Star_Destroyer)
            && GameConditions.isDuringBattleAt(game, Filters.here(self))) {
            final GameState gameState = game.getGameState();
            final CostToDrawDestinyCardResult result = (CostToDrawDestinyCardResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add cost to draw battle destiny");
            action.setActionMsg("Make opponent use 1 Force (if possible) to draw a card for battle destiny");
            // Perform result(s)
            action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        if (GameConditions.canUseForce(game, opponent, 1)) {
                            // Ask player to Use Force or card for battle destiny is not drawn
                            action.appendEffect(
                                new PlayoutDecisionEffect(action, opponent,
                                    new YesNoDecision("Do you want to use 1 Force to draw a card for battle destiny?") {
                                        @Override
                                        protected void yes() {
                                            action.appendEffect(
                                                new UseForceEffect(action, opponent, 1));
                                        }

                                        @Override
                                        protected void no() {
                                            gameState.sendMessage(opponent + " chooses to not use 1 Force to draw a card for battle destiny");
                                            result.costToDrawCardFailed(true);
                                        }
                                    }
                                )
                            );
                        }
                        else {
                            // "(if possible)": opponent cannot use Force, so the card is drawn without paying the cost
                            gameState.sendMessage(opponent + " is unable to use 1 Force, so draws a card for battle destiny without using Force");
                        }
                    }
                });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WARRANT_OFFICER_BACHENKALL__DEPLOY_IMPERIAL_PILOT_FROM_OUTSIDE_DECK;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Imperial Pilot from outside your deck");
            action.setActionMsg("Deploy Imperial Pilot here from outside your deck");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                new DeployCardFromPileEffect(action, playerId, Zone.OUTSIDE_OF_DECK, Filters.title("Imperial Pilot"),Filters.locationAndCardsAtLocation(Filters.here(self)), null, null, null, false, null, 0, null, null, null, false, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
