package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.OccupiesWithEvaluator;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Objective
 * Title: Diplomatic Mission To Alderaan / A Weakness Can Be Found
 */
public class Card203_019_BACK extends AbstractObjective {
    public Card203_019_BACK() {
        super(Side.LIGHT, 7, Title.A_Weakness_Can_Be_Found, ExpansionSet.SET_3, Rarity.V);
        setGameText("While this side up, your total battle destiny is +X, where X = number of battlegrounds occupied by Rebels of ability < 4. Once per turn, may [download] a Tatooine battleground site. Once per turn, if you just won a battle, may retrieve 1 Force. Flip this card if you do not occupy a battleground site and a battleground system (or you do not control any locations).");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, new OccupiesWithEvaluator(self, playerId, Filters.battleground, Filters.and(Filters.Rebel, Filters.abilityLessThan(4))), playerId));
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
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.wonBattle(game, effectResult, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
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
                && (!GameConditions.occupies(game, playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.battleground_site)
                || !GameConditions.occupies(game, playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.battleground_system)
                || !GameConditions.controls(game, playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.location))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
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