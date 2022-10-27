package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Defensive Shield
 * Title: I Find Your Lack Of Faith Disturbing
 */
public class Card200_097 extends AbstractDefensiveShield {
    public Card200_097() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE,"I Find Your Lack Of Faith Disturbing", ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Darth Vader ruthlessly used the Force to strike down enemies and soldiers who displeased him. He could choke victims from afar without touching them.");
        setGameText("Plays on table. Goo Nee Tay is canceled. Unless Deep Hatred on table, opponent may use only one combat card per turn. We'll Handle This may target only Undercover spies and 5D6-RA-7.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Goo_Nee_Tay)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Goo_Nee_Tay)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Goo_Nee_Tay, Title.Goo_Nee_Tay);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, new UnlessCondition(new OnTableCondition(self, Filters.Deep_Hatred)), ModifierFlag.MAY_ONLY_USE_ONE_COMBAT_CARD_PER_TURN, opponent));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Well_Handle_This, ModifyGameTextType.WELL_HANDLE_THIS__ONLY_TARGET_UNDERCOVER_SPIES_AND_5D6RA7));
        return modifiers;
    }
}