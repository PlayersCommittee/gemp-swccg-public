package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Effect
 * Title: Ni Chuba Na?? (V)
 */
public class Card601_091 extends AbstractNormalEffect {
    public Card601_091() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ni Chuba Na??", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'Your buddy here was about to be turned into orange goo. He picked a fight with a Dug. An especially dangerous Dug called Sebulba.'");
        setGameText("Deploy on table.  Your Force generation is +1.  Once per game, may relocate this Effect to a site.  At same and related locations, Revolution is canceled and your cards may not have their deploy costs modified by Goo Nee Tay.  Security Precautions is canceled. (Immune to Alter.)");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalForceGenerationModifier(self, 1, playerId));
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.your(self),
                new AtCondition(self, Filters.location), Filters.Goo_Nee_Tay, Filters.sameOrRelatedLocation(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__NI_CHUBA_NA__RELOCATE_TO_SITE;

        //TODO might need to do something to check if you can relocate the effect there
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.canSpot(game, self, Filters.site)) {

            Collection<PhysicalCard> sites =  Filters.filterTopLocationsOnTable(game, Filters.site);
            if (!sites.isEmpty()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate to a site");
                action.appendUsage(new OncePerGameEffect(action));
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Relocate "+ GameUtils.getCardLink(self)+" to which site?", Filters.in(sites)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.allowResponses(new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                action.appendEffect(
                                        new AttachCardFromTableEffect(action, self, finalTarget));
                            }
                        });
                    }
                });
                // Perform result(s)
                return Collections.singletonList(action);
            }
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Security_Precautions)
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
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Security_Precautions)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Security_Precautions, Title.Security_Precautions);
                actions.add(action);
            }

            if (GameConditions.canTargetToCancel(game, self, Filters.and(Filters.Revolution, Filters.at(Filters.sameOrRelatedLocation(self))))) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.and(Filters.Revolution, Filters.at(Filters.sameOrRelatedLocation(self))), Title.Revolution);
                actions.add(action);
            }
        }
        return actions;
    }
}