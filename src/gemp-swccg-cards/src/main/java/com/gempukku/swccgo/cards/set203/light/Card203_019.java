package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Objective
 * Title: Diplomatic Mission To Alderaan / A Weakness Can Be Found
 */
public class Card203_019 extends AbstractObjective {
    public Card203_019() {
        super(Side.LIGHT, 0, Title.Diplomatic_Mission_To_Alderaan, ExpansionSet.SET_3, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Tatooine (with Tantive IV, non-[Reflections II] R2-D2, and Stolen Data Tapes there) and Dune Sea. For remainder of game, you may not deploy Sandwhirl, Strike Planning, Admiral's Orders or [Episode I] Jedi. While this side up, you Force drains at Tatooine system are -2. Once per turn, may [download] Alderaan or a Tatooine battleground site. Until the start of your first turn, Tantive IV may be forfeited to satisfy to satisfy all battle damage against you. Flip this card if Stolen Data Tapes 'delivered' and Rebels control two battlegrounds (a site and a system).");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_3);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Tatooine_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Tatooine to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.Tantive_IV, Filters.Tatooine_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Tantive IV to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.R2D2, Filters.not(Icon.REFLECTIONS_II)), Filters.Tatooine_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose non-[Reflections II] R2-D2 to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.Stolen_Data_Tapes, Filters.Tatooine_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Stolen Data Tapes to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Dune_Sea, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Dune Sea to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.or(Filters.Sandwhirl, Filters.Strike_Planning, Filters.Admirals_Order, Filters.and(Icon.EPISODE_I, Filters.Jedi)), playerId), null));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.Tatooine_system, -2, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DIPLOMATIC_MISSION_TO_ALDERAAN__DOWNLOAD_ALDERAAN_OR_TATOOINE_BATTLEGROUND_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Alderaan or a Tatooine battleground site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Alderaan_system, Filters.Tatooine_site), Filters.or(Filters.Alderaan_system, Filters.battleground), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && !GameConditions.isReachedPlayersTurnNumber(game, playerId, 1)) {
            PhysicalCard tantiveIV = Filters.findFirstActive(game, self, Filters.and(Filters.Tantive_IV, Filters.participatingInBattle));
            if (tantiveIV != null
                    && GameConditions.canForfeitToSatisfyBattleDamage(game, playerId, tantiveIV)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Forfeit Tantive IV to satisfy all battle damage");
                action.setActionMsg("Forfeit " + GameUtils.getCardLink(tantiveIV) + " to satisfy all battle damage");
                // Pay cost(s)
                action.appendCost(
                        new ForfeitCardFromTableEffect(action, tantiveIV));
                // Perform result(s)
                action.appendEffect(
                        new SatisfyAllBattleDamageEffect(action, playerId));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, Filters.delivered_Stolen_Data_Tapes)
                && GameConditions.controlsWith(game, self, playerId, Filters.battleground_site, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Rebel)
                && GameConditions.controlsWith(game, self, playerId, Filters.battleground_system, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Rebel)) {

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