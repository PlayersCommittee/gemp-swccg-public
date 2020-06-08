package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Blasted Droid
 */
public class Card5_132 extends AbstractUsedInterrupt {
    public Card5_132() {
        super(Side.DARK, 3, Title.Blasted_Droid, Uniqueness.UNIQUE);
        setLore("'Oh my! Oh, uh, I'm, I'm terribly sorry. I. . . I didn't mean to intrude. No, no, no, please don't get up. No!'");
        setGameText("During your control phase, fire (for free) one of your blasters carried by a trooper or one of your automated weapons. Any hit targets are immediately lost.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.blaster, Filters.attachedTo(Filters.trooper)),
                    Filters.and(Filters.trooper, Filters.hasPermanentBlaster), Filters.automated_weapon), Filters.canBeFiredForFreeAt(self, 0, Filters.canBeTargetedBy(self)));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Fire a weapon");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose weapon to fire", weaponFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard weapon) {
                                action.addAnimationGroup(weapon);
                                // Allow response(s)
                                action.allowResponses("Fire " + GameUtils.getCardLink(weapon),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new FireWeaponEffect(action, weapon, true, Filters.canBeTargetedBy(self)));
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