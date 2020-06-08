package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.TransferDeviceOrWeaponEffect;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeDisarmedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Device
 * Title: Retractable Arm
 */
public class Card4_015 extends AbstractCharacterDevice {
    public Card4_015() {
        super(Side.LIGHT, 4, "Retractable Arm");
        setLore("2 kilo lifting capacity and 85 meter reach. Better than 1 micrometer placement accuracy. Feisty Rebel droids can use this tool for sneaky purposes.");
        setGameText("Deploy on any R-unit droid to give that droid thief skill. Once during each of your control phases, you may use 1 Force to target a weapon or device present. Draw destiny. If destiny > 2, target is 'stolen.' Target may then be transferred for free. Droid may not be Disarmed.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.DEVICE_THAT_DEPLOYS_ON_DROIDS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.R_unit);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.R_unit;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, hasAttached, Keyword.THIEF));
        modifiers.add(new MayNotBeDisarmedModifier(self, Filters.hasAttached(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.canUseForce(game, playerId, 1)
                && self.getAttachedTo() != null) {
            TargetingReason targetingReason = TargetingReason.TO_BE_STOLEN;
            final Filter filter = Filters.and(Filters.or(Filters.device, Filters.weapon), Filters.present(self), Filters.canBeTargetedBy(self.getAttachedTo(), targetingReason));
            if (GameConditions.canTarget(game, self, targetingReason, filter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Steal weapon or device");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                action.appendUsage(
                        new UseDeviceEffect(action, self));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose weapon or device to steal", targetingReason, filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("Steal " + GameUtils.getCardLink(targetedCard),
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard cardToSteal = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                GameState gameState = game.getGameState();
                                                                if (totalDestiny == null) {
                                                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                    return;
                                                                }

                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                                if (totalDestiny > 2) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new StealCardAndAttachFromTableEffect(action, cardToSteal, self.getAttachedTo()));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canUseDevice(game, self)
                && self.getAttachedTo() != null) {
            final Filter yourCharacterPresent = Filters.and(Filters.your(self), Filters.character, Filters.present(self));
            Collection<PhysicalCard> stolenDevicesAndWeapons = Filters.filterActive(game, self, SpotOverride.INCLUDE_STOLEN,
                    Filters.and(Filters.or(Filters.device, Filters.weapon), Filters.stolen, Filters.attachedTo(self.getAttachedTo()), Filters.deviceOrWeaponCanBeTransferredTo(true, yourCharacterPresent)));
            if (!stolenDevicesAndWeapons.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Transfer weapon or device");
                // Update usage limit(s)
                action.appendUsage(
                        new UseDeviceEffect(action, self));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose weapon or device to transfer", stolenDevicesAndWeapons) {
                            @Override
                            protected void cardSelected(final PhysicalCard weaponOrDevice) {
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose where to transfer " + GameUtils.getCardLink(weaponOrDevice),
                                                Filters.and(yourCharacterPresent, Filters.canTransferDeviceOrWeaponTo(weaponOrDevice, true))) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard transferTo) {
                                                // Allow response(s)
                                                action.allowResponses("Transfer " + GameUtils.getCardLink(weaponOrDevice) + " to " + GameUtils.getCardLink(transferTo),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new TransferDeviceOrWeaponEffect(action, weaponOrDevice, transferTo, true));
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}