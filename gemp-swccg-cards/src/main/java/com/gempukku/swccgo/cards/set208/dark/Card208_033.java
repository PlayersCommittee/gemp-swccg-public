package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.LoseDestinyCardEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Dopheld Mitaka
 */
public class Card208_033 extends AbstractFirstOrder {
    public Card208_033() {
        super(Side.DARK, 6, 0, 2, 2, 3, "Lieutenant Dopheld Mitaka", Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setGameText("[Pilot] 2. When drawn for destiny, use 1 Force or this card is lost. During your deploy phase, may use 3 Force to relocate Kylo from here to same site as a Resistance Agent.");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredDrawnAsDestinyTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Use 1 Force or lose card");
            action.setActionMsg("Use 1 Force or lose " + GameUtils.getCardLink(self));
            // Perform result(s)
            List<StandardEffect> choices = new LinkedList<StandardEffect>();
            choices.add(new UseForceEffect(action, playerId, 1));
            choices.add(new LoseDestinyCardEffect(action));
            action.appendEffect(
                    new ChooseEffectEffect(action, playerId, choices));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)) {
            final Filter sameSiteAsResistanceAgent = Filters.sameSiteAs(self, Filters.Resistance_Agent);
            Filter kyloFilter = Filters.and(Filters.Kylo, Filters.here(self), Filters.canBeRelocatedToLocation(sameSiteAsResistanceAgent, 3));
            if (GameConditions.canSpot(game, self, kyloFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate Kylo to another site");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Kylo", kyloFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard kyloTargeted) {
                                Filter siteToRelocateKylo = Filters.and(sameSiteAsResistanceAgent, Filters.locationCanBeRelocatedTo(kyloTargeted, 3));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(kyloTargeted) + " to", siteToRelocateKylo) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                action.addAnimationGroup(kyloTargeted);
                                                action.addAnimationGroup(siteSelected);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, kyloTargeted, siteSelected, 3));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(kyloTargeted) + " to " + GameUtils.getCardLink(siteSelected),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, kyloTargeted, siteSelected));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
