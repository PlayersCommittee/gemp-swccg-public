package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 0
 * Type: Weapon
 * Subtype: Character
 * Title: Han's Heavy Blaster Pistol (V) (Errata)
 */
public class Card501_034 extends AbstractCharacterWeapon {
    public Card501_034() {
        super(Side.LIGHT, 2, Title.Hans_Heavy_Blaster_Pistol, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("BlasTech DL-44 heavy pistol. Short range, but relatively powerful. Carries energy for 25 shots. Illegal or restricted on most systems.");
        setGameText("Deploy on Han or Beckett. May target a character or creature for free. Draw destiny. Target hit, its forfeit = 0, if destiny +2 > defense value. If on non-spy Han, may fire once during your control phase or place in Used Pile to cancel a weapon destiny targeting Han.");
        addKeywords(Keyword.BLASTER);
        setMatchingCharacterFilter(Filters.or(Filters.Beckett, Filters.Han));
        setTestingText("Han's Heavy Blaster Pistol (V) (Errata)");
    }


    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Han, Filters.Beckett));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Han, Filters.Beckett);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 2, Statistic.DEFENSE_VALUE, true, 0, 1);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isAttachedTo(game, self, Filters.and(Filters.not(Filters.spy), Filters.Han))
                && Filters.canBeFired(self, 0).accepts(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Fire " + GameUtils.getFullName(self));
            action.setActionMsg("Fire " + GameUtils.getCardLink(self));
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new FireWeaponEffect(action, self, false, Filters.any));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.Han)
                && GameConditions.canCancelDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel weapon destiny");
            action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(new CancelDestinyEffect(action));

            return Collections.singletonList(action);
        }

        return null;
    }
}
