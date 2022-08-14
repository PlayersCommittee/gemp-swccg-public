package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ExplodingProgramTrapResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Device
 * Title: Fire Extinguisher
 */
public class Card2_025 extends AbstractCharacterDevice {
    public Card2_025() {
        super(Side.LIGHT, 5, Title.Fire_Extinguisher);
        setLore("Among the various special devices on droids like R2 units are gas-based fire extinguishers. Effective against electrical fires and can cause confusion during battle.");
        setGameText("Deploy on your astromech droid. Cancels an 'exploding' Program Trap here. Any starship it is aboard is immune to Lateral Damage and ion cannons. If deployed on R2-D2, may lose Fire Extinguisher to cancel a battle just initiated where present at a site.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEVICE_THAT_DEPLOYS_ON_DROIDS, Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.astromech_droid);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.astromech_droid;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.programTrapExploding(game, effectResult, Filters.here(self))) {
            final PhysicalCard programTrap = ((ExplodingProgramTrapResult) effectResult).getProgramTrap();
            if (GameConditions.canTargetToCancel(game, self, programTrap)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, programTrap, "'exploding' Program Trap");
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starshipAboard = Filters.and(Filters.starship, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, starshipAboard, Title.Lateral_Damage));
        modifiers.add(new MayNotBeTargetedByModifier(self, starshipAboard, Filters.ion_cannon));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.site, Filters.wherePresent(self)))
                && GameConditions.isAttachedTo(game, self, Filters.R2D2)
                && GameConditions.canUseDevice(game, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel battle");
            // Update usage limit(s)
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Pay cost(s)
            action.appendCost(
                    new LoseCardFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new CancelBattleEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}