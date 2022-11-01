package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Resistance
 * Title: Tuanul Villager
 */
public class Card204_013 extends AbstractResistance {
    public Card204_013() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Tuanul Villager", Uniqueness.RESTRICTED_3, ExpansionSet.SET_4, Rarity.V);
        setGameText("If with your other Resistance character and present at a site, adds 1 to your Force drains here and may place this character in Used Pile to cancel a just drawn weapon destiny here.");
        addIcons(Icon.EPISODE_VII, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AndCondition(new WithCondition(self, Filters.and(Filters.your(self),
                Filters.other(self), Filters.Resistance_character)), new PresentAtCondition(self, Filters.site)), 1, playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult, Filters.here(self))
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.isWith(game, self, Filters.and(Filters.your(self), Filters.other(self), Filters.Resistance_character))
                && GameConditions.isPresentAt(game, self, Filters.site)) {

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
