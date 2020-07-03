package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
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
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 13
 * Type: Weapon
 * Subtype: Character
 * Title: Inquisitor Lightsaber
 */
public class Card501_063 extends AbstractCharacterWeapon {
    public Card501_063() {
        super(Side.DARK, 3, Title.Inquisitor_Lightsaber, Uniqueness.RESTRICTED_3);
        setGameText("Deploys on, and is a matching weapon for, your Inquisitor. May target a character. Draw two destiny. Target hit, and may take any “hatred” cards on target into hand, if total destiny > total defense value. If just lost, may lose 1 force to relocate this card to Used pile.");
        addIcon(Icon.VIRTUAL_SET_13);
        setTestingText("•••Inquisitor Lightsaber");
        setMatchingCharacterFilter(Filters.inquisitor);
        addKeyword(Keyword.LIGHTSABER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.inquisitor);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.inquisitor;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, self)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place on top of Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " on top of Used Pile");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new PreventEffectOnCardEffect(action, ((AboutToLeaveTableResult) effectResult).getPreventableCardEffect(), self, null));
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponInquisitorLightsaberAction();
            return Collections.singletonList(action);
        }
        return null;
    }
}
