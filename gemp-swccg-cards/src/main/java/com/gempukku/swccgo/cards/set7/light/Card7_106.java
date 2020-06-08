package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Suppressive Fire
 */
public class Card7_106 extends AbstractUsedInterrupt {
    public Card7_106() {
        super(Side.LIGHT, 3, "Suppressive Fire", Uniqueness.UNIQUE);
        setLore("Echo Base heavy weapons units provided covering fire, allowing Alliance personnel to escape Hoth.");
        setGameText("During your control phase, fire (for free) one of your blasters carried by a trooper or one of your automated weapons. 'Hit' target is lost.");
        addIcons(Icon.SPECIAL_EDITION);
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