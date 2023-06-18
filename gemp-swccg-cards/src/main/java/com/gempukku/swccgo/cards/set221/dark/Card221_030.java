package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 21
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Tarl (V)
 */
public class Card221_030 extends AbstractImperial {
    public Card221_030() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Sergeant Tarl", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Took part in the capture of the Rebel Blockade Runner Tantive IV. Stormtrooper trained on Corulag. Corellia native.");
        setGameText("When deployed, power +2 for remainder of turn. During battle with two ISB agents (or troopers), may target a Rebel present; target is immune to attrition and target's game text is canceled.");
        addIcons(Icon.ENDOR, Icon.WARRIOR, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.STORMTROOPER);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setText("Add 2 to power");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfTurnEffect(action, self, 2));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && (GameConditions.isInBattleWith(game, self, 2, Filters.ISB_agent)
                || GameConditions.isInBattleWith(game, self, 2, Filters.trooper))
                && GameConditions.isInBattleWith(game, self, Filters.and(Filters.presentAt(Filters.here(self)), Filters.Rebel, Filters.canBeTargetedBy(self)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Target Rebel");
            action.setActionMsg("Target a Rebel to make it immune to attrition and cancel its game text");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a Rebel", Filters.and(Filters.participatingInBattle, Filters.Rebel, Filters.presentAt(Filters.here(self)))) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action, new ImmuneToAttritionModifier(self, finalTarget), "Makes " + GameUtils.getCardLink(finalTarget) + " immune to attrition"));
                            action.appendEffect(
                                    new CancelGameTextUntilEndOfBattleEffect(action, finalTarget));
                        }
                    });
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
