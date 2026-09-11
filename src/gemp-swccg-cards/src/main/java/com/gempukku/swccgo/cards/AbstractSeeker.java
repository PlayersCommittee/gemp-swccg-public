package com.gempukku.swccgo.cards;


import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysAndMovesLikeUndercoverSpyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The abstract class providing the common implementation for seekers.
 */
public abstract class AbstractSeeker extends AbstractAutomatedWeapon {

    /**
     * Creates a blueprint for a seeker.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractSeeker(Side side, float destiny, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, PlayCardZoneOption.OPPONENTS_SIDE_OF_LOCATION, title, uniqueness, expansionSet, rarity);
        addKeyword(Keyword.SEEKER);
    }

    @Override
    public final boolean isMovesLikeCharacter() {
        return true;
    }


    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.unoccupied, Filters.site);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        modifiers.add(new DeploysAndMovesLikeUndercoverSpyModifier(self));
        return modifiers;
    }

    /**
     * Gets the filter for characters that this Seeker can make lost (without presentWith / canBeTargetedBy wrappers).
     */
    protected abstract Filter getEligibleSeekerTargetFilter(SwccgGame game, PhysicalCard self);

    private Filter getFullEligibleTargetFilter(SwccgGame game, PhysicalCard self) {
        return Filters.and(getEligibleSeekerTargetFilter(game, self), Filters.presentWith(self),
                Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST));
    }

    private boolean mayIgnoreTargets(SwccgGame game, PhysicalCard self) {
        return GameConditions.isFlagActiveForPlayer(game, ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, self.getOwner());
    }

    private Set<String> getIgnoreList(PhysicalCard self) {
        WhileInPlayData data = self.getWhileInPlayData();
        if (data == null) {
            return new HashSet<String>();
        }
        return data.getTextValues();
    }

    private void ensureIgnoreList(PhysicalCard self) {
        if (self.getWhileInPlayData() == null) {
            self.setWhileInPlayData(new WhileInPlayData(new HashSet<String>()));
        }
    }

    private void clearIgnoreList(PhysicalCard self) {
        self.setWhileInPlayData(null);
    }

    private void maintainIgnoreList(SwccgGame game, PhysicalCard self) {
        if (!mayIgnoreTargets(game, self)) {
            clearIgnoreList(self);
            return;
        }
        Set<String> ignoreList = getIgnoreList(self);
        if (ignoreList.isEmpty()) {
            return;
        }
        Collection<PhysicalCard> presentWithSeeker = Filters.filterActive(game, self, Filters.presentWith(self));
        Set<String> presentIds = new HashSet<String>();
        for (PhysicalCard card : presentWithSeeker) {
            presentIds.add(String.valueOf(card.getCardId()));
        }
        ignoreList.removeIf(id -> !presentIds.contains(id));
        if (ignoreList.isEmpty()) {
            clearIgnoreList(self);
        }
    }

    private Filter getNonIgnoredEligibleTargetFilter(SwccgGame game, final PhysicalCard self) {
        final Set<String> ignoreList = getIgnoreList(self);
        return Filters.and(getFullEligibleTargetFilter(game, self), new Filter() {
            @Override
            public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !ignoreList.contains(String.valueOf(physicalCard.getCardId()));
            }
        });
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        maintainIgnoreList(game, self);

        if (!TriggerConditions.isTableChanged(game, effectResult)
                || !mayIgnoreTargets(game, self)) {
            return null;
        }

        Filter nonIgnored = getNonIgnoredEligibleTargetFilter(game, self);
        if (!GameConditions.canSpot(game, self, nonIgnored)) {
            return null;
        }

        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
        action.setSingletonTrigger(true);
        action.setText("Ignore all potential targets");
        action.setActionMsg("Ignore all potential targets for " + GameUtils.getCardLink(self));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> targets = Filters.filterActive(game, self, getFullEligibleTargetFilter(game, self));
                        ensureIgnoreList(self);
                        Set<String> ignoreList = getIgnoreList(self);
                        for (PhysicalCard target : targets) {
                            ignoreList.add(String.valueOf(target.getCardId()));
                        }
                    }
                }
        );
        return Collections.singletonList(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        maintainIgnoreList(game, self);

        String playerId = self.getOwner();
        Filter filter;
        if (mayIgnoreTargets(game, self)) {
            // Only force a target if at least one eligible target is not ignored
            filter = getNonIgnoredEligibleTargetFilter(game, self);
        } else {
            filter = getFullEligibleTargetFilter(game, self);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, filter)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make a character lost");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose character to make lost", filter) {
                        @Override
                        protected void cardSelected(final PhysicalCard character) {
                            action.addAnimationGroup(character);
                            action.setActionMsg("Make " + GameUtils.getCardLink(character) + " lost");
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseCardsFromTableSimultaneouslyEffect(action, Arrays.asList(character, self), true, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        maintainIgnoreList(game, self);

        if (!mayIgnoreTargets(game, self) || getIgnoreList(self).isEmpty()) {
            return null;
        }

        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
        action.setText("Stop ignoring potential targets");
        action.setActionMsg("Stop ignoring potential targets for " + GameUtils.getCardLink(self));
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        clearIgnoreList(self);
                    }
                }
        );
        return Collections.singletonList(action);
    }
}
