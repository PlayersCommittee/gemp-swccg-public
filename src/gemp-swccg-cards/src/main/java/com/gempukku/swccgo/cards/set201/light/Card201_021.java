package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.effects.UseWeaponEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDuelTotalEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 1
 * Type: Weapon
 * Subtype: Character
 * Title: Mace Windu's Lightsaber
 */
public class Card201_021 extends AbstractCharacterWeapon {
    public Card201_021() {
        super(Side.LIGHT, 1, "Mace Windu's Lightsaber", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setLore("Elegant sword of pure energy. 'This is the weapon of a Jedi Knight. Not as clumsy or as random as a blaster. A lightsaber can be dangerous to an unskilled user.'");
        setGameText("Deploy on Mace. May add 1 to Force drain where present. Mace may add 1 to his duel total. May target a character or creature for free. Draw two destiny. Target hit, and it forfeit = 0, if total destiny > defense value.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.LIGHTSABER);
        setMatchingCharacterFilter(Filters.Mace);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Mace);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Mace;
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
        // Check condition(s)
        if (TriggerConditions.isDuelAddOrModifyDuelDestiniesStep(game, effectResult)
                && GameConditions.isDuringDuelWithParticipant(game, Filters.and(Filters.Mace, Filters.hasAttached(self)))
                && GameConditions.canUseWeapon(game, self.getAttachedTo(), self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to duel total");
            // Update usage limit(s)
            action.appendUsage(
                    new UseWeaponEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDuelTotalEffect(action, 1, playerId, "Adds 1 to duel total"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE, true, 0);
            return Collections.singletonList(action);
        }
        return null;
    }
}
