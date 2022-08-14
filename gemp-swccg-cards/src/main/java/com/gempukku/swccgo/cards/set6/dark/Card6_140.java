package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetLandspeedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Device
 * Title: Jet Pack
 */
public class Card6_140 extends AbstractCharacterDevice {
    public Card6_140() {
        super(Side.DARK, 4, Title.Jet_Pack, Uniqueness.RESTRICTED_3);
        setLore("Mitrinomon Z-6 jet pack. Exhaust vents are used to maneuver in mid-flight. Gyro-stabilizer automatically applies counterthrust when landing.");
        setGameText("Use 3 Force to deploy on any alien (except Boba Fett or Jabba). May 'fly' (landspeed = 3). May use 2 Force to move as a 'react'.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.alien, Filters.except(Filters.or(Filters.Boba_Fett, Filters.Jabba)));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.alien, Filters.except(Filters.or(Filters.Boba_Fett, Filters.Jabba)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetLandspeedModifier(self, Filters.hasAttached(self), 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        PhysicalCard attachedTo = self.getAttachedTo();

        // Check condition(s)
        if (attachedTo != null
                && (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                || TriggerConditions.battleInitiated(game, effectResult, opponent))
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canUseDevice(game, self)) {
            if (Filters.canMoveAsReactAsActionFromOtherCard(self, true, 0, false).accepts(game, attachedTo)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Move as a 'react'");
                action.setActionMsg("Move " + GameUtils.getCardLink(attachedTo) + " as a 'react'");
                // Update usage limit(s)
                action.appendUsage(
                        new UseDeviceEffect(action, self));
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 2));
                // Perform result(s)
                action.appendEffect(
                        new MoveAsReactEffect(action, attachedTo, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}