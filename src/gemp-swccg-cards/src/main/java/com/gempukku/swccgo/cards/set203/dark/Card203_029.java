package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAttachModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Device
 * Title: Deflector Shield Generators (V)
 */
public class Card203_029 extends AbstractDevice {
    public Card203_029() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Deflector Shield Generators", Uniqueness.UNRESTRICTED, ExpansionSet.SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Located atop the superstructure of a Star Destroyer, the generator towers create an energy shield which repels solid objects and weapons fire.");
        setGameText("Deploy on a capital starship. Starship is power and armor +2 and immune to attrition < 5. May cancel Combined Attack or Power Pivot targeting this starship. Starfighters here may not 'attach'.");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_3);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.capital_starship);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.capital_starship;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, hasAttached, 2));
        modifiers.add(new ArmorModifier(self, hasAttached, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, hasAttached, 5));
        modifiers.add(new MayNotAttachModifier(self, Filters.and(Filters.starfighter, Filters.here(self))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.or(Filters.Combined_Attack, Filters.Power_Pivot), Filters.hasAttached(self))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canUseDevice(game, self)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendUsage(new UseDeviceEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}