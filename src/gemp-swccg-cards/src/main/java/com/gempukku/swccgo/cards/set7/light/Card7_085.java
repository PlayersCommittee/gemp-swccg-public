package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Lost
 * Title: Darklighter Spin
 */
public class Card7_085 extends AbstractLostInterrupt {
    public Card7_085() {
        super(Side.LIGHT, 3, Title.Darklighter_Spin, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Biggs improvised this maneuver shortly after joining the Alliance. By spinning his starfighter while firing all four cannons, he was able to destroy multiple incoming targets.");
        setGameText("During a battle at a system or sector, if you are about to draw a card for battle destiny, you may instead use the maneuver number of your starfighter in that battle.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final Filter yourStarfighterInBattle = Filters.and(Filters.your(self), Filters.starfighter, Filters.participatingInBattle, Filters.hasManeuverDefined);

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.isDuringBattleAt(game, Filters.system_or_sector)
                && GameConditions.canTarget(game, self, yourStarfighterInBattle)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Substitute destiny");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", yourStarfighterInBattle) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard starfighter) {
                            action.addAnimationGroup(starfighter);
                            float maneuver = game.getModifiersQuerying().getManeuver(game.getGameState(), starfighter);
                            // Allow response(s)
                            action.allowResponses("Substitute " + GameUtils.getCardLink(starfighter) + "'s maneuver value of " + GuiUtils.formatAsString(maneuver) + " for battle destiny",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            float finalManeuver = game.getModifiersQuerying().getManeuver(game.getGameState(), finalTarget);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new SubstituteDestinyEffect(action, finalManeuver));
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