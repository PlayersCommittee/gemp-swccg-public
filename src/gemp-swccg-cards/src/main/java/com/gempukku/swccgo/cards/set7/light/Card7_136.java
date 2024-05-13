package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: Hidden Base / Systems Will Slip Through Your Fingers
 */
public class Card7_136 extends AbstractObjective {
    public Card7_136() {
        super(Side.LIGHT, 0, Title.Hidden_Base, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Rendezvous Point. Place a planet system (with a parsec number from 1 to 8) from outside your deck face down on your side of table (not in play); that card indicates the planet where your 'Hidden Base' is located. While this side up, once during each of your deploy phases, may deploy one system from Reserve Deck; reshuffle. Opponent loses no more than 1 Force from each of your Force drains at systems and sectors. Flip this card any time after you have deployed five battleground systems and your 'Hidden Base' system.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Rendezvous_Point), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Rendezvous Point to deploy";
                    }
                });
        action.appendRequiredEffect(
                new StackCardFromOutsideDeckEffect(action, playerId, self, Filters.and(Filters.planet_system, Filters.planetSystemInParsecRange(1, 8))) {
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose 'Hidden Base'";
                    }
                });
        action.appendRequiredEffect(
                new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(new HashMap<String, Boolean>())));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.HIDDEN_BASE__DOWNLOAD_SYSTEM;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy system from Reserve Deck");
            action.setActionMsg("Deploy a system from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.system, true));
            actions.add(action);
        }

        // Check condition(s)
        if (!GameConditions.cardHasWhileInPlayDataSet(self)
                && GameConditions.canBeFlipped(game, self)) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.setActionMsg("Flip " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (GameConditions.cardHasWhileInPlayDataSet(self)
                && TriggerConditions.justDeployed(game, effectResult, playerId, Filters.system)) {

            PhysicalCard deployedSystem = ((PlayCardResult) effectResult).getPlayedCard();
            boolean isBattleground = Filters.battleground_system.accepts(game.getGameState(), game.getModifiersQuerying(), deployedSystem);
            String hiddenBaseTitle = game.getGameState().getStackedCards(self).iterator().next().getBlueprint().getTitle();

            if (isBattleground || deployedSystem.getBlueprint().getTitle().equals(hiddenBaseTitle)) {
                // Add this card to the set of played systems (battleground or 'hidden base') mapped to whether that system is a battleground
                Map<String, Boolean> systemTitlesMap = self.getWhileInPlayData().getStringBooleanMap();
                systemTitlesMap.put(deployedSystem.getBlueprint().getTitle(), isBattleground);

                // Now check if five battleground systems (and the 'hidden base' have been deployed) by owner
                Boolean isHiddenBaseBattleground = systemTitlesMap.get(hiddenBaseTitle);
                if (isHiddenBaseBattleground != null) {
                    if (systemTitlesMap.keySet().size() >= 6
                            || (isHiddenBaseBattleground && systemTitlesMap.keySet().size() >= 5)) {
                        self.setWhileInPlayData(null);
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.system_or_sector, 1, opponent));
        return modifiers;
    }
}