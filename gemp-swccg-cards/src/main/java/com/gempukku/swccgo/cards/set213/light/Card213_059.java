package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 13
 * Type: Weapon
 * Subtype: Character
 * Title: Rock
 */
public class Card213_059 extends AbstractCharacterWeapon {
    public Card213_059() {
        super(Side.LIGHT, 5, "Rock");
        setLore("");
        setGameText("Ewok weapon. Deploy on your warrior or Ewok. May 'throw' (place in Used Pile) to target a character. For remainder of turn, target is power -3 (and if Proxima, she cannot battle). If on a Corellian and a battle just initiated at same site, may 'throw' Rock.");
        addIcon(Icon.VIRTUAL_SET_13);
        addKeyword(Keyword.EWOK_WEAPON);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter warriorOrEwok = Filters.and(Filters.your(self), Filters.or(Filters.warrior, Filters.Ewok));
        return Filters.and(Filters.your(self), warriorOrEwok);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        Filter warriorOrEwok = Filters.and(Filters.your(self), Filters.or(Filters.warrior, Filters.Ewok));
        return Filters.and(Filters.your(self), warriorOrEwok);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.OTHER).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponRockAction();
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isAttachedTo(game, self, Filters.and(Filters.Corellian, Filters.armedWith(self), Filters.participatingInBattle, Filters.at(Filters.site)))
                && Filters.canBeFired(self, 0).accepts(game, self)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);

            action.setText("Throw " + GameUtils.getFullName(self));
            action.setActionMsg("Throw " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new FireWeaponEffect(action, self, false, Filters.character));
            return Collections.singletonList(action);
        }
        return null;
    }
}
