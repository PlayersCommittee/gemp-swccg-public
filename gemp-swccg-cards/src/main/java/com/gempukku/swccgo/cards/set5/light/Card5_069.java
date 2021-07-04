package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
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
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Smoke Screen
 */
public class Card5_069 extends AbstractLostInterrupt {
    public Card5_069() {
        super(Side.LIGHT, 3, Title.Smoke_Screen, Uniqueness.UNIQUE);
        setLore("Warning: The Alderaanian Medical Association has determined that inhaling carbon-freezing smoke can be hazardous to your health.");
        setGameText("During a battle at a site, if you are about to draw a card for battle destiny, you may instead use the ability number of one of your characters involved in the battle.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final Filter yourCharacterInBattle = Filters.and(Filters.your(self), Filters.character, Filters.participatingInBattle, Filters.hasAbility);

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.canSubstituteDestiny(game)
                && GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canSpot(game, self, yourCharacterInBattle)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Substitute destiny");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", yourCharacterInBattle) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard character) {
                            action.addAnimationGroup(character);
                            final float ability = game.getModifiersQuerying().getAbility(game.getGameState(), character);
                            // Allow response(s)
                            action.allowResponses("Substitute " + GameUtils.getCardLink(character) + "'s ability value of " + GuiUtils.formatAsString(ability) + " for battle destiny",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            float finalAbility = game.getModifiersQuerying().getAbility(game.getGameState(), action.getPrimaryTargetCard(targetGroupId));
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new SubstituteDestinyEffect(action, finalAbility));
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