package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Turn It Off! Turn It Off!
 */
public class Card3_139 extends AbstractUsedOrLostInterrupt {
    public Card3_139() {
        super(Side.DARK, 5, Title.Turn_It_Off_Turn_It_Off, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C1);
        setLore("'Turn it off! Turn it off! Off! TURN IT OFF!'");
        setGameText("USED: Cancel any attempt to place a 'hit' starship, vehicle or droid in the Used Pile rather than the Lost Pile. OR Cancel Han's Toolkit. LOST: Cancel Crash Site Memorial.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s) - USED: Cancel Han's Toolkit being played
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Hans_Toolkit)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        // Check condition(s) - LOST: Cancel Crash Site Memorial being played
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Crash_Site_Memorial)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        // TODO(Chief/shared engine): USED cancel of attempt to place hit starship/vehicle/droid
        // in Used Pile instead of Lost Pile needs Chewbacca/WED Techie to emit cancelable targeting
        // (TargetingReason.TO_BE_USED_INSTEAD_OF_LOST) or PlaceInCardPileInsteadOfLostEffect.
        // See GitHub #96 / #997 and Gergall Doc plan. Do not implement broad engine here without Chief.
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s) - USED: Cancel Han's Toolkit on table
        if (GameConditions.canTargetToCancel(game, self, Filters.Hans_Toolkit)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Hans_Toolkit, Title.Hans_Toolkit);
            actions.add(action);
        }
        // Check condition(s) - LOST: Cancel Crash Site Memorial on table
        if (GameConditions.canTargetToCancel(game, self, Filters.Crash_Site_Memorial)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Crash_Site_Memorial, Title.Crash_Site_Memorial);
            actions.add(action);
        }
        return actions;
    }
}
