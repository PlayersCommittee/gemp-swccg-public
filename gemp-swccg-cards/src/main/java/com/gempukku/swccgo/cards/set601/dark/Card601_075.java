package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Close Call (V)
 */
public class Card601_075 extends AbstractUsedOrLostInterrupt {
    public Card601_075() {
        super(Side.DARK, 2, Title.Close_Call, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("If this little one doesn't pulverize you, the next one just might.");
        setGameText("USED: During battle, cancel and redraw a just drawn destiny. LOST: A just drawn battle or weapon destiny is -3");
        addIcons(Icon.DAGOBAH, Icon.LEGACY_BLOCK_2);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Cancel destiny and cause re-draw");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyAndCauseRedrawEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                || TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Subtract 3 from destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, -3));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}