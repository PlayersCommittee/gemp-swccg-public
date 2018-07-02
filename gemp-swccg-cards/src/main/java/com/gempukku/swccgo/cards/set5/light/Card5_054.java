package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RelocateFromLocationToWeatherVane;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Into The Ventilation Shaft, Lefty
 */
public class Card5_054 extends AbstractLostInterrupt {
    public Card5_054() {
        super(Side.LIGHT, 6, Title.Into_The_Ventilation_Shaft_Lefty);
        setLore("'Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa...'");
        setGameText("Relocate one of your characters from a Cloud City site to Weather Vane. May be played even after a battle has just been initiated.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        return getPlayInterruptAction(playerId, game, self);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.battleInitiated(game, effectResult)) {
            return getPlayInterruptAction(playerId, game, self);
        }
        return null;
    }

    private List<PlayInterruptAction> getPlayInterruptAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        if (GameConditions.canSpot(game, self, Filters.Weather_Vane)) {
            Filter filter = Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.Cloud_City_site));
            if (GameConditions.canTarget(game, self, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate character to Weather Vane");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                                action.addAnimationGroup(character);
                                // Allow response(s)
                                action.allowResponses("Relocate " + GameUtils.getCardLink(character) + " to Weather Vane",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                if (Filters.hit.accepts(game, finalCharacter)) {
                                                    action.appendEffect(new RestoreCardToNormalEffect(action, finalCharacter));
                                                }
                                                action.appendEffect(
                                                        new RelocateFromLocationToWeatherVane(action, finalCharacter));
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