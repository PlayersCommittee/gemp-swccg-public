package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Admiral's Order
 * Title: Fighter Cover
 */
public class Card9_093 extends AbstractAdmiralsOrder {
    public Card9_093() {
        super(Side.DARK, "Fighter Cover", ExpansionSet.DEATH_STAR_II, Rarity.R);
        setGameText("Each starfighter that fires a weapon in battle is power +3 for the remainder of battle. Once per turn you may cancel and redraw your starship weapon destiny just drawn. At sites related to systems you occupy, your characters who have immunity to attrition each add 2 to immunity and 1 to each of that character's weapon destiny draws.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.weaponJustFiredBy(game, effectResult, Filters.weapon, Filters.and(Filters.starfighter, Filters.participatingInBattle))) {
            final PhysicalCard cardFiringWeapon = ((FiredWeaponResult) effectResult).getCardFiringWeapon();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 3 to power of " + GameUtils.getFullName(cardFiringWeapon));
            action.setActionMsg("Add 3 to power of " + GameUtils.getCardLink(cardFiringWeapon));
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfBattleEffect(action, cardFiringWeapon, 3));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, playerId, Filters.starship_weapon)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter sitesRelatedToSystemsYouOccupy = Filters.and(Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId))));
        Filter yourCharactersWithImmunityToAttrition = Filters.and(Filters.your(playerId), Filters.character, Filters.alreadyHasImmunityToAttrition(self));
        Filter filter = Filters.and(yourCharactersWithImmunityToAttrition, Filters.at(sitesRelatedToSystemsYouOccupy));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmunityToAttritionChangeModifier(self, filter, 2));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, filter, 1));
        return modifiers;
    }
}
