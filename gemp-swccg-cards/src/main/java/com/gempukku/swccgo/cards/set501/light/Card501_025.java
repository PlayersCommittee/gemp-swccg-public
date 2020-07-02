package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SatisfyAllAttritionEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Val
 */
public class Card501_025 extends AbstractAlien {
    public Card501_025() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Val", Uniqueness.UNIQUE);
        setLore("Female scout, smuggler, and thief.");
        setGameText("Once per turn, may 'smuggle' (deploy for free from Reserve Deck; reshuffle) a blaster onto your smuggler here. May forfeit instead of your 'hit' smuggler here, restoring that smuggler to normal (if Beckett, Val also satisfies all attrition against you).");
        addKeywords(Keyword.FEMALE, Keyword.SCOUT, Keyword.SMUGGLER, Keyword.THIEF);
        addIcons(Icon.VIRTUAL_SET_13, Icon.WARRIOR);
        setTestingText("Val");
        addPersona(Persona.VAL);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.VAL__DOWNLOAD_WEAPON;
        Filter smugglerHere = Filters.and(Filters.your(self), Filters.character, Filters.smuggler, Filters.present(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, smugglerHere)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy blaster from Reserve Deck");
            action.setActionMsg("Deploy (smuggle) a blaster from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.blaster, smugglerHere, true,true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.your(playerId), Filters.other(self), Filters.hit, Filters.character, Filters.smuggler, Filters.atSameSite(self));

        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Forfeit to restore 'hit' smuggler");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose 'hit' smuggler", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new ForfeitCardFromTableEffect(action, self));
                            // Allow response(s)
                            action.allowResponses("Restore " + GameUtils.getCardLink(cardTargeted) + " to normal",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, cardTargeted));

                                            if (Filters.Beckett.accepts(game, cardTargeted)) {
                                                action.appendEffect(
                                                        new SatisfyAllAttritionEffect(action, playerId)
                                                );
                                            }
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
