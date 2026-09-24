package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PutCardFromCardPileOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Frozen Assets
 */
public class Card5_023 extends AbstractNormalEffect {
    public Card5_023() {
        // playCardZoneOption null = multiple play options
        super(Side.LIGHT, 5, null, Title.Frozen_Assets, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Molten carbonite is released into the chamber and then flash frozen, releasing a blast of air. The subject is instantly covered in the newly solidified material.");
        setGameText("Deploy on your side of table. At every site where there is a 'frozen' captive, your Rebels deploy -2 and are power +2 in battle. OR Deploy on top of opponent's Force Pile. Force below this card may not be drawn or used. Effect lost at end of opponent's next turn.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Deploy on your side of table"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.OPPONENTS_FORCE_PILE, "Deploy on top of opponent's Force Pile"));
        return playCardOptions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition tableOption = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1);
        Filter sitesWithFrozenCaptive = Filters.sameSiteAs(self, SpotOverride.INCLUDE_CAPTIVE, Filters.frozenCaptive);
        Filter yourRebels = Filters.and(Filters.your(self), Filters.Rebel);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourRebels, tableOption, -2, sitesWithFrozenCaptive));
        // Power +2 in battle at those sites
        modifiers.add(new PowerModifier(self,
                Filters.and(yourRebels, Filters.participatingInBattle, Filters.at(sitesWithFrozenCaptive)),
                tableOption, 2));
        return modifiers;
    }

    /**
     * Frozen Pile is not in-play, so the usual in-play required-trigger walk never sees this Effect.
     * Force-pile mode still needs thaw / end-of-turn lose while sitting on Frozen Pile.
     */
    @Override
    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = super.getRequiredAfterTriggers(game, effectResult, self);
        // Relocate within Frozen Pile clears playCardOptionId; zone is the force-pile-mode signal.
        if ((self.getZone() == Zone.FROZEN_PILE || self.getZone() == Zone.TOP_OF_FROZEN_PILE)
                && !self.getZone().isInPlay()) {
            List<RequiredGameTextTriggerAction> frozenPileActions = getGameTextRequiredAfterTriggers(game, effectResult, self, self.getCardId());
            if (frozenPileActions != null && !frozenPileActions.isEmpty()) {
                List<TriggerAction> combined = new LinkedList<TriggerAction>();
                if (actions != null) {
                    combined.addAll(actions);
                }
                combined.addAll(frozenPileActions);
                return combined;
            }
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Force-pile mode (VHD): FA sits Active at TOP of FROZEN_PILE (zoneOwner = Force pile owner).
        // Lost at end of opponent's next turn; unfreeze Frozen Pile when leaving table.
        // SSA relocates FA to Frozen Pile bottom: auto-thaw cards now above it.
        boolean onFrozenPile = self.getZone() == Zone.FROZEN_PILE || self.getZone() == Zone.TOP_OF_FROZEN_PILE;
        if (!onFrozenPile && !TriggerConditions.isAboutToLeaveTable(game, effectResult, self)) {
            return null;
        }

        final String forcePileOwner = self.getZoneOwner();
        boolean notTopOfFrozenPile = onFrozenPile
                && game.getGameState().getTopOfFrozenPile(forcePileOwner) != self
                && game.getGameState().getFrozenForceSize(forcePileOwner) > 0;
        boolean leaving = TriggerConditions.isAboutToLeaveTable(game, effectResult, self);
        boolean endOfOpponentsTurn = onFrozenPile && TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self);

        if (!notTopOfFrozenPile && !leaving && !endOfOpponentsTurn) {
            return null;
        }

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText(endOfOpponentsTurn ? "Make Frozen Assets lost" : "Unfreeze Force Pile");
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getGameState().moveFrozenPileToForcePile(forcePileOwner);
                    }
                }
        );
        if (endOfOpponentsTurn) {
            // Frozen Pile is not onTable, so LoseCardFromTableEffect would no-op.
            action.appendEffect(
                    new PutCardFromCardPileOnBottomOfCardPileEffect(action, self.getOwner(), self, Zone.LOST_PILE, false));
        }
        return Collections.singletonList(action);
    }
}
