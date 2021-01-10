package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseTractorBeamEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: In Range
 */
public class Card7_254 extends AbstractUsedInterrupt {
    public Card7_254() {
        super(Side.DARK, 6, "In Range", Uniqueness.UNIQUE);
        setLore("'They'll be in range of our tractor beam in moments, my lord.' 'Good. Prepare the boarding party and set your weapons for stun.'");
        setGameText("If you have a Star Destroyer in a battle, during the weapons segment use its tractor beam for free. Add 2 to tractor beam destiny if targeting a unique (*) starship. If not captured, target is power and maneuver -3 for remainder of turn.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.I_CANT_SHAKE_HIM__DOWNLOAD_STARSHIP_WEAPON_OR_TRACTOR_BEAM;

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game,
                Filters.and(Filters.your(playerId), Filters.Star_Destroyer, Filters.hasAttached(Filters.tractor_beam)))
                ) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Use tractor beam");
            // Allow response(s)
            Filter tractorBeamFilter = Filters.and(Filters.tractor_beam, Filters.attachedTo(Filters.and(Filters.participatingInBattle, Filters.your(playerId), Filters.Star_Destroyer)));
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose tractor beam to use", tractorBeamFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard tractorBeam) {
                            action.addAnimationGroup(tractorBeam);
                            // Pay cost(s)
                            action.allowResponses("Use tractor beam",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new UseTractorBeamEffect(action, tractorBeam, true));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}