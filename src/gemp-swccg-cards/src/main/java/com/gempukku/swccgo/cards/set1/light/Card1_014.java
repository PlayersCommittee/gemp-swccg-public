package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.TransferDeviceOrWeaponEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Kabe
 */
public class Card1_014 extends AbstractAlien {
    public Card1_014() {
        super(Side.LIGHT, 3, 2, 1, 1, 3, Title.Kabe, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Young female Chadra-Fan. Thief. Guardian is Muftak. Abandoned on Tatooine by slavers. Loves intoxicating juri juice. Once robbed Jabba the Hutt's dwelling in Mos Eisley.");
        setGameText("During your control phase, may target one opponent's weapon or device at same site. Draw destiny. If destiny < target's destiny number, Kabe 'steals' device to use, or weapon to hold and transfer (for free) to a warrior at same site.");
        addKeywords(Keyword.FEMALE, Keyword.THIEF);
        setSpecies(Species.CHADRAFAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        TargetingReason targetingReason = TargetingReason.TO_BE_STOLEN;
        final Filter filter = Filters.and(Filters.or(Filters.device, Filters.weapon), Filters.atSameSite(self), Filters.canBeTargetedBy(self, targetingReason));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canTarget(game, self, targetingReason, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Steal weapon or device");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose weapon or device to steal", targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
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
                                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                            final GameState gameState = game.getGameState();
                                                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            action.appendEffect(
                                                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(cardToSteal)) {
                                                                        @Override
                                                                        protected void refreshedPrintedDestinyValues() {
                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            float targetsDestiny = modifiersQuerying.getDestiny(gameState, cardToSteal);
                                                                            gameState.sendMessage("Target's destiny: " + GuiUtils.formatAsString(targetsDestiny));

                                                                            if (totalDestiny < targetsDestiny) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new StealCardAndAttachFromTableEffect(action, cardToSteal, self));
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
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            final Filter yourWarriorAtSameSite = Filters.and(Filters.your(self), Filters.warrior, Filters.atSameSite(self));
            Collection<PhysicalCard> stolenDevicesAndWeapons = Filters.filterActive(game, self, SpotOverride.INCLUDE_STOLEN,
                    Filters.and(Filters.or(Filters.device, Filters.weapon), Filters.stolen, Filters.attachedTo(self), Filters.deviceOrWeaponCanBeTransferredTo(true, yourWarriorAtSameSite)));
            if (!stolenDevicesAndWeapons.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Transfer weapon or device");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose weapon or device to transfer", stolenDevicesAndWeapons) {
                            @Override
                            protected void cardSelected(final PhysicalCard weaponOrDevice) {
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose where to transfer " + GameUtils.getCardLink(weaponOrDevice),
                                                Filters.and(yourWarriorAtSameSite, Filters.canTransferDeviceOrWeaponTo(weaponOrDevice, true))) {
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
