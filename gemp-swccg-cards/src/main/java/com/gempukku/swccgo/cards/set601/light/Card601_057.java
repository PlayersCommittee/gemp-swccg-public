package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Block 7
 * Type: Interrupt
 * Subtype: Lost
 * Title: Run Luke, Run! (V)
 */
public class Card601_057 extends AbstractUsedOrLostInterrupt {
    public Card601_057() {
        super(Side.LIGHT, 6, Title.Run_Luke_Run, Uniqueness.UNIQUE);
        setLore("After seeing Vader strike down Obi-Wan, Luke attacked recklessly until he heard the old Jedi Master's voice warn, 'Run Luke, Run!'");
        setGameText("USED: During battle, if Obi-Wan out of play, cancel an attempt to target a Rebel with a weapon. \n" +
                "LOST: During battle at a site, place non-[Episode I] Obi-Wan out of play (even from hand); Your other characters there may move away for free.");
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.ObiWan, Filters.not(Icon.EPISODE_I)))
                || GameConditions.hasInHand(game, playerId, Filters.and(Filters.ObiWan, Filters.not(Icon.EPISODE_I))))) {
            final PhysicalCard battleLocation = game.getGameState().getBattleLocation();
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            //choose Obi-Wan to place out of play
            
            //move other characters away for free [probably TargetCardsOnTableEffect and you choose the group to move away?]

        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.Rebel, Filters.weapon)
            && GameConditions.isOutOfPlay(game, Filters.ObiWan)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel weapon targeting");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(
                            new CancelWeaponTargetingEffect(action));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}