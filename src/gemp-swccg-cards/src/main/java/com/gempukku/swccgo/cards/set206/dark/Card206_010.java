package com.gempukku.swccgo.cards.set206.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 6
 * Type: Character
 * Subtype: Imperial
 * Title: Death Trooper
 */
public class Card206_010 extends AbstractImperial {
    public Card206_010() {
        super(Side.DARK, 2, 3, 3, 2, 4, "Death Trooper", Uniqueness.RESTRICTED_3, ExpansionSet.SET_6, Rarity.V);
        setArmor(3);
        setLore("Stormtrooper.");
        setGameText("While you have no other troopers here (except death troopers), adds one battle destiny at same site as Krennic, Tarkin, or Thrawn. May place character in Used Pile to cancel a just drawn weapon destiny targeting your leader here.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.DEATH_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new AtSameSiteAsCondition(self, Filters.or(Filters.Krennic, Filters.Tarkin, Filters.Thrawn)),
                new NotCondition(new HereCondition(self, Filters.and(Filters.other(self), Filters.your(self), Filters.trooper, Filters.except(Filters.death_trooper))))), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.and(Filters.your(self), Filters.leader, Filters.here(self)))
                && GameConditions.canCancelDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place character in Used Pile");
            action.setActionMsg("Cancel weapon destiny");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
