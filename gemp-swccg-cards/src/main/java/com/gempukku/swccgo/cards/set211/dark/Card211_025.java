package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Virtual Set 11
 * Type: Weapon
 * Subtype: Character
 * Title: Dark Jedi Lightsaber (V)
 */
public class Card211_025 extends AbstractCharacterWeapon {
    public Card211_025() {
        super(Side.DARK, 2, "Dark Jedi Lightsaber");
        setLore("Multifaceted jewels focus light into a deadly blade. Projects a meter-long beam of pure energy. A lightsaber is constructed personally by a Jedi as a part of training.");
        setGameText("Deploy on Aurra Sing, Grievous, or your warrior of ability > 4. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition, if total destiny > defense value.");
        addKeywords(Keyword.LIGHTSABER);
        addIcons(Icon.VIRTUAL_SET_11, Icon.EPISODE_I);
        // Not considered matching weapons for anyone.  Need to be unique to be matching. (per discussion with Aglets)
        setVirtualSuffix(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return (Filters.or(Filters.Aurra_Sing, Filters.Grievous, Filters.and(Filters.your(self), Filters.warrior, Filters.abilityMoreThan(4))));
    }

    // Jim: Dunno when this would be different from the deploy.  maybe when weapon lev is involved?  Basically I'm not sure the purpose of this section even though it's part of what I copied from..
    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return (Filters.or(Filters.Aurra_Sing, Filters.Grievous, Filters.and(Filters.your(self), Filters.warrior, Filters.abilityMoreThan(4))));
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
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            // Build action using common utility
            FireWeaponAction action = actionBuilder.builderDarkAndLightJediLightSaberV();
            return Collections.singletonList(action);
        }
        return null;
    }

}