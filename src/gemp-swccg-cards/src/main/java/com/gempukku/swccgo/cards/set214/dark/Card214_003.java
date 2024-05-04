package com.gempukku.swccgo.cards.set214.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StealOneCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 14
 * Type: Weapon
 * Subtype: Character
 * Title: Darksaber
 */
public class Card214_003 extends AbstractCharacterWeapon {
    public Card214_003() {
        super(Side.DARK, 2, Title.Darksaber, Uniqueness.UNIQUE, ExpansionSet.SET_14, Rarity.V);
        setLore("");
        setGameText("Deploy on Gideon, [Set 13] Maul, or your Mandalorian. May add 1 to Force drain where present. May target a character. Draw destiny. Target hit, and its forfeit = 0, if destiny +2 > defense value. If just lost from table or hand, opponent may steal this weapon into hand.");
        addIcons(Icon.VIRTUAL_SET_14);
        addKeywords(Keyword.LIGHTSABER);
        setMatchingCharacterFilter(Filters.or(Filters.Gideon, Filters.Maul));
}

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Gideon, Filters.and(Icon.VIRTUAL_SET_13, Filters.Maul), Filters.Mandalorian));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Gideon, Filters.Maul, Filters.Mandalorian);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 2, Statistic.DEFENSE_VALUE, true, 0);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.wherePresent(self))
                && GameConditions.canUseWeapon(game, self.getAttachedTo(), self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to Force drain");
            // Perform result(s)
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public List<OptionalGameTextTriggerAction> getOpponentsCardGameTextLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLost(game, effectResult, self)
            && GameConditions.canSteal(game, self)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, game.getOpponent(self.getOwner()), gameTextSourceCardId);
            action.setText("Steal into hand");
            action.appendEffect(new StealOneCardIntoHandEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (effectResult.getType() == EffectResult.Type.FORCE_LOST) {
            LostForceResult result = (LostForceResult)effectResult;
            if (result.getZone() == Zone.HAND && Filters.and(self).accepts(game, result.getCardLost())) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, game.getOpponent(self.getOwner()), gameTextSourceCardId);
                action.setText("Steal into hand");
                action.appendEffect(new StealOneCardIntoHandEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
