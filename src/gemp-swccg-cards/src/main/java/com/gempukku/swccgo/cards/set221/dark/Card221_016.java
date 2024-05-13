package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.TargetedByWeaponCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Imperial
 * Title: Corporal Oberk (V)
 */
public class Card221_016 extends AbstractImperial {
    public Card221_016() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Corporal Oberk", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Originally from Ukio. Stormtrooper assigned to search for Rebel activity on Endor. Trying to impress the commander of his biker scout detachment.");
        setGameText("[Pilot] 2, 3: any speeder bike. When deployed, may deploy a battleground or speeder bike from Used Pile; reshuffle. When firing a Scout Blaster, adds 1 to his total weapon destiny (2 if targeting an alien).");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.BIKER_SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.speeder_bike)));
        modifiers.add(new TotalWeaponDestinyForWeaponFiredByModifier(self, new ConditionEvaluator(1, 2, new TargetedByWeaponCondition(Filters.alien, Filters.and(Filters.scout_blaster, Filters.attachedTo(self)))), Filters.scout_blaster));
        return modifiers;
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CORPORAL_OBERK_V__DEPLOY_CARD_FROM_USED_PILE;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromUsedPile(game, playerId, self, gameTextActionId, true)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Used Pile");
            action.setActionMsg("Deploy a battleground or speeder bike from Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromUsedPileEffect(action, Filters.or(Filters.battleground, Filters.speeder_bike), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
