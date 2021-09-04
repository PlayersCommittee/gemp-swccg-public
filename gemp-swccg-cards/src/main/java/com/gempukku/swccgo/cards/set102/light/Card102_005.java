package com.gempukku.swccgo.cards.set102.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premium (Jedi Pack)
 * Type: Character
 * Subtype: Alien
 * Title: Tedn Dahai
 */
public class Card102_005 extends AbstractAlien {
    public Card102_005() {
        super(Side.LIGHT, 3, 2, 1, 1, 4, "Tedn Dahai", Uniqueness.UNIQUE);
        setLore("Male Bith musician. Member in good standing of the Intergalactic Federation of Musicians. Plays the Fanfar in Figrin D'an's band. Once worked for the Empire as a scout.");
        setGameText("For each other musician at same site, during any control phase you may use 1 Force to choose one opponent's alien present. That alien cannot utilize its game text for remainder of that turn.");
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.MUSICIAN, Keyword.SCOUT);
        setSpecies(Species.BITH);
        addPersona(Persona.TEDN_DAHAI);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.CONTROL)) {
            int count = Filters.countActive(game, self, Filters.and(Filters.other(self), Filters.musician, Filters.atSameSite(self)));
            if (count > 0) {
                final Filter targetFilter = Filters.and(Filters.opponents(self), Filters.alien, Filters.present(self));
                if (GameConditions.isNumTimesDuringEitherPlayersPhase(game, self, playerId, count, gameTextSourceCardId, Phase.CONTROL)
                        && GameConditions.canUseForce(game, playerId, 1)
                        && GameConditions.canTarget(game, self, targetFilter)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Cancel an alien's game text");
                    // Update usage limit(s)
                    action.appendUsage(
                            new NumTimesPerPhaseEffect(action, count));
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose alien", targetFilter) {
                                @Override
                                protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                    action.addAnimationGroup(targetedCard);
                                    // Pay cost(s)
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, 1));
                                    // Allow response(s)
                                    action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                            new UnrespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new CancelGameTextUntilEndOfTurnEffect(action, targetedCard));
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
