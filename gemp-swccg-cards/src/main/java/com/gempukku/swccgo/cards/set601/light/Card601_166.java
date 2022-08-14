package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.effects.UseWeaponEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDuelTotalEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 5
 * Type: Weapon
 * Subtype: Character
 * Title: Jedi Lightsaber (V)
 */
public class Card601_166 extends AbstractCharacterWeapon {
    public Card601_166() {
        super(Side.LIGHT, 2, Title.Jedi_Lightsaber);
        setVirtualSuffix(true);
        setLore("Elegant sword of pure energy. 'This is the weapon of a Jedi Knight. Not as clumsy or as random as a blaster. A lightsaber can be dangerous to an unskilled user.'");
        setGameText("Deploy on Mace or Yoda. [Coruscant] sites may not cancel Force drain bonuses. May add 1 to a Force drain where present. May target a character or creature for free. Draw 2 destiny (3 if on Yoda and defending). Target hit, and its forfeit = 0, if total destiny > defense value.");
        addIcons(Icon.LEGACY_BLOCK_5);
        addKeywords(Keyword.LIGHTSABER);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Mace, Filters.Yoda));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Mace, Filters.Yoda);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        // should really create a new modifier that will stop the bonus canceling from working but this is easier and will work
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.CORUSCANT, Filters.site), ModifyGameTextType.LEGACY__CORUSCANT_ICON_SITES__MAY_NOT_CANCEL_FORCE_DRAIN_BONUSES));
        return modifiers;
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
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            int numDestiny = (GameConditions.isDuringBattleInitiatedBy(game, game.getOpponent(playerId)) && Filters.Yoda.accepts(game, self.getAttachedTo())? 3 : 2);
            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(numDestiny, Statistic.DEFENSE_VALUE, true, 0);
            return Collections.singletonList(action);
        }
        return null;
    }
}
