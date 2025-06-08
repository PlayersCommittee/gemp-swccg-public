package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.MayEscortAnyNumberOfCaptivesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalCarbonFreezingDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.StolenResult;
import com.gempukku.swccgo.logic.timing.results.TransferredDeviceOrWeaponResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Device
 * Title: Binders
 */
public class Card5_106 extends AbstractCharacterDevice {
    public Card5_106() {
        super(Side.DARK, 6, Title.Binders, Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("Because standard binders are durable but not easily adaptable, bounty hunters often carry special binders which automatically tighten around a captive's appendages.");
        setGameText("Deploy on one of your warriors or bounty hunters. May now escort any number of captives. If device removed from your character, select one captive escorted by that character to remain and release all others.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.warrior, Filters.bounty_hunter));
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayEscortAnyNumberOfCaptivesModifier(self, Filters.hasAttached(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)) {
            return Collections.singletonList(releaseExtraCaptives(game, self, self.getAttachedTo(), gameTextSourceCardId));
        }
        else if (TriggerConditions.justTransferredDeviceOrWeaponToTarget(game, effectResult, self, Filters.any)) {
            var result = (TransferredDeviceOrWeaponResult)effectResult;
            return Collections.singletonList(releaseExtraCaptives(game, self, result.getTransferredFrom(), gameTextSourceCardId));
        }
        else if (TriggerConditions.justStolen(game, effectResult, self)) {
            var result = (StolenResult)effectResult;
            return Collections.singletonList(releaseExtraCaptives(game, self, result.getStolenFrom(), gameTextSourceCardId));
        }
        return null;
    }


    private RequiredGameTextTriggerAction releaseExtraCaptives(SwccgGame game, PhysicalCard self, PhysicalCard escort, int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Unbound captives break free.");

        var validCaptives = new ArrayList<>(getAllCaptivesOnEscort(game, self, escort));
        action.appendTargeting(
                new TargetCardOnTableEffect(action, escort.getOwner(), "Select one captive to remain (all others on this escort will be released)",
                        SpotOverride.INCLUDE_CAPTIVE, Filters.in(validCaptives)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        validCaptives.remove(targetedCard);

                        action.allowResponses(
                                new UnrespondableEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        action.addAnimationGroup(validCaptives);
                                        action.appendEffect(new ReleaseCaptivesEffect(action, validCaptives));
                                    }
                                }
                        );
                    }
                }
        );

        return action;
    }

    private List<PhysicalCard> getAllCaptivesOnEscort(SwccgGame game, PhysicalCard self, PhysicalCard escort) {
        if(escort == null)
            return new ArrayList<>();
        final var escortedCaptivesOnBinderBearer = Filters.and(Filters.escortedCaptive, Filters.escortedBy(escort));
        var captives = Filters.filterActive(game, self, SpotOverride.INCLUDE_CAPTIVE, escortedCaptivesOnBinderBearer);
        return captives.stream().toList();
    }
}