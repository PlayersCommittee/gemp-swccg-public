package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Subtype: Utinni
 * Title: Death Star Plans
 */
public class Card1_046 extends AbstractUtinniEffect {
    public Card1_046() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, Title.Death_Star_Plans, Uniqueness.UNIQUE);
        setLore("'What's so important? What's he carrying?' 'The technical readouts of that battle station. I only hope that when the data is analyzed, a weakness can be found.'");
        setGameText("Deploy on any Death Star site (except docking bay). Target one of your droids (not on Death Star). When target reaches Utinni Effect, 'steal' plans. If target then moves to any Yavin 4 site, draw 3 destiny. Retrieve that much lost Force. Lose Utinni Effect.");
        addKeywords(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Death_Star_site, Filters.except(Filters.docking_bay));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.your(self), Filters.or(Filters.droid, Filters.grantedMayBeTargetedBy(self)), Filters.not(Filters.on(Title.Death_Star)));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.or(Filters.droid, Filters.grantedMayBeTargetedBy(self));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.or(Filters.droid, Filters.grantedMayBeTargetedBy(self));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (!GameConditions.isUtinniEffectReached(game, self)) {
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("'Steal'");
                action.setActionMsg("Have " + GameUtils.getCardLink(target) + " 'steal' " + GameUtils.getCardLink(self));
                // Update usage limit(s)
                action.appendUsage(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                            }
                        }
                );
                // Perform result(s)
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, target));
                return Collections.singletonList(action);
            }
        }
        else if (!GameConditions.isUtinniEffectCompleted(game, self)) {
            if (TriggerConditions.movedToLocation(game, effectResult, target, Filters.Yavin_4_site)) {
                int numDestinyToDraw = 3;
                if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.DEATH_STAR_PLANS__ADD_DESTINY_TO_FORCE_RETRIEVED)) {
                    numDestinyToDraw++;
                }

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Draw " + numDestinyToDraw + " destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                self.setUtinniEffectStatus(UtinniEffectStatus.COMPLETED);
                            }
                        }
                );
                // Perform result(s)
                action.appendEffect(
                        new RecordUtinniEffectCompletedEffect(action, self));
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId, numDestinyToDraw) {
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                if (totalDestiny == null) {
                                    return;
                                }
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, totalDestiny) {
                                            @Override
                                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                return Collections.singletonList(self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1));
                                            }
                                        });
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, self));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}