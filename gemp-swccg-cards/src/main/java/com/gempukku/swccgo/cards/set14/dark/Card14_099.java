package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Effect
 * Title: Open Fire!
 */
public class Card14_099 extends AbstractNormalEffect {
    public Card14_099() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Open_Fire, Uniqueness.UNIQUE);
        setLore("When given the attack coordinates, AAT weaponry can be programmed to track and fire automatically for maximum destruction.");
        setGameText("Deploy on your AAT. Once during your control phase may use 3 Force to fire your AAT Laser Cannon on this AAT for free.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.AAT);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 3)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.AAT_Laser_Cannon, Filters.attachedTo(Filters.hasAttached(self)), Filters.canBeFiredForFreeAt(self, 3, Filters.canBeTargetedBy(self)));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Fire a weapon");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose AAT Laser Cannon to fire", weaponFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard weapon) {
                                action.addAnimationGroup(weapon);
                                action.setActionMsg("Fire " + GameUtils.getCardLink(weapon));
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 3));
                                // Perform result(s)
                                action.appendEffect(
                                        new FireWeaponEffect(action, weapon, true, Filters.canBeTargetedBy(self)));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}