package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.effects.CancelAttackEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DefeatedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Subtype: Immediate
 * Title: WHAAAAAAAAAOOOOW!
 */
public class Card4_071 extends AbstractUsedInterrupt {
    public Card4_071() {
        super(Side.LIGHT, 7, "WHAAAAAAAAAOOOOW!", Uniqueness.UNIQUE);
        setLore("'");
        setGameText("");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        if(TriggerConditions.justDefeatedBy(game, effectResult, Filters.character, Filters.or(Filters.Dragonsnake, Filters.Rancor, Filters.Sarlacc, Filters.Dianoga, Keyword.WAMPA))){
            DefeatedResult defeatedResult = (DefeatedResult) effectResult;
            final PhysicalCard characterDefeated = defeatedResult.getCardDefeated();
            Collection<PhysicalCard> siteToRelocateTo = Filters.filterTopLocationsOnTable(game,
                    Filters.and(Filters.adjacentSite(characterDefeated), Filters.locationCanBeRelocatedTo(characterDefeated, true, false, true, 0, true)));
            if(!siteToRelocateTo.isEmpty()){
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(characterDefeated) + " to", Filters.in(siteToRelocateTo)) {
                            @Override
                            protected void cardSelected(final PhysicalCard siteSelected) {
                                action.appendEffect(new RelocateBetweenLocationsEffect(action, characterDefeated, siteSelected));

                                action.addAnimationGroup(characterDefeated);
                                action.addAnimationGroup(siteSelected);
                                // Allow response(s)
                                action.allowResponses("Instead of being 'eaten', 'hurl' (relocate) " + GameUtils.getCardLink(characterDefeated) + " to " + GameUtils.getCardLink(siteSelected),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelAttackEffect(action));
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, characterDefeated, siteSelected));
                                            }
                                        }
                                );
                            }
                        });

                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
