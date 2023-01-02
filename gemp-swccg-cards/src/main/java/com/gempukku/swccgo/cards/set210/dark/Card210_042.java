package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 10
 * Type: Objective
 * Title: Ralltiir Operations (V) / In The Hands Of The Empire (V)
 */
public class Card210_042 extends AbstractObjective {
    public Card210_042() {
        super(Side.DARK, 0, Title.Ralltiir_Operations, ExpansionSet.SET_10, Rarity.V);
        setVirtualSuffix(true);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy [Set 20] Ralltiir system. " +
                "For remainder of game, spaceport sites are immune to Always Thinking With Your Stomach, He Hasn't Come Back Yet, and non-[Set 18] Ounee Ta. You may not play [Coruscant] Do They Have A Code Clearance?. Your Force generation is +1 at each Ralltiir site. " +
                "While this side up, once per turn, may [download] a site (or non-unique Imperial) to Ralltiir. " +
                "Flip this card if Imperials control at least three Ralltiir sites and opponent controls no Ralltiir locations.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return deployCardWithObjectiveText(self, Filters.and(Icon.VIRTUAL_SET_20, Filters.Ralltiir_system), "[Set 20] Ralltiir system");
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(spaceportSitesImmuneToHeHasntComeBackYetForRemainderOfGame(self, action));
        action.appendEffect(spaceportSitesImmuneToOuneeTaForRemainderOfGame(self, action));
        action.appendEffect(mayNotPlayAlwaysThinkingWithYourStomach(self, action));
        action.appendEffect(mayNotPlayCoruscantCodeClearance(self, action));
        yourForceGenPlusOneAtEachRalltiirSite(self, game);
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.RALLTIIR_OPERATIONS__DOWNLOAD_SITE_OR_NONUNIQUE_IMPERIAL_TO_RALLTIIR;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a site or non-unique Imperial from Reserve Deck to Ralltiir");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.or(Filters.site, Filters.and(Filters.non_unique, Filters.Imperial)), Title.Ralltiir, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controlsWith(game, self, playerId, 3, Filters.Ralltiir_site, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Imperial)
                && !GameConditions.controls(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Ralltiir_location)) {

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

    private void yourForceGenPlusOneAtEachRalltiirSite(PhysicalCard self, SwccgGame game) {
        if (!GameConditions.cardHasAnyForRemainderOfGameDataSet(self)) {
            self.setForRemainderOfGameData(self.getCardId(), new ForRemainderOfGameData());
            game.getModifiersEnvironment().addUntilEndOfGameModifier(
                    new ForceGenerationModifier(self, Filters.Ralltiir_site, 1, self.getOwner()));
        }
    }

    private AddUntilEndOfGameModifierEffect spaceportSitesImmuneToHeHasntComeBackYetForRemainderOfGame(PhysicalCard self, RequiredGameTextTriggerAction action) {
        return new AddUntilEndOfGameModifierEffect(action,
                new ImmuneToTitleModifier(self, Filters.at(Filters.spaceport_site), Title.He_Hasnt_Come_Back_Yet), null);
    }

    private AddUntilEndOfGameModifierEffect spaceportSitesImmuneToOuneeTaForRemainderOfGame(PhysicalCard self, RequiredGameTextTriggerAction action) {
        return new AddUntilEndOfGameModifierEffect(action,
                new ImmuneToTitleModifier(self, Filters.spaceport_site, new OnTableCondition(self, Filters.and(Filters.not(Icon.VIRTUAL_SET_18), Filters.title(Title.Ounee_Ta))), Title.Ounee_Ta), null);
    }

    private AddUntilEndOfGameModifierEffect mayNotPlayAlwaysThinkingWithYourStomach(PhysicalCard self, RequiredGameTextTriggerAction action) {
        return new AddUntilEndOfGameModifierEffect(action,
                new ImmuneToTitleModifier(self, Filters.spaceport_site, Title.Always_Thinking_With_Your_Stomach), null);
    }

    private AddUntilEndOfGameModifierEffect mayNotPlayCoruscantCodeClearance(PhysicalCard self, RequiredGameTextTriggerAction action) {
        return new AddUntilEndOfGameModifierEffect(action,
                new MayNotPlayModifier(self, Filters.and(Icon.CORUSCANT, Filters.title(Title.Do_They_Have_A_Code_Clearance)), self.getOwner()), null);
    }
}