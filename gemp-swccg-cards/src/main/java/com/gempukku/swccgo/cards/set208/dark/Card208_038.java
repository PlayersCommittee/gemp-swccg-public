package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 8
 * Type: Effect
 * Title: Alert My Star Destroyer! (V)
 */
public class Card208_038 extends AbstractNormalEffect {
    public Card208_038() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Alert My Star Destroyer!", Uniqueness.UNRESTRICTED, ExpansionSet.SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("For important Imperial dignitaries, an individual Star Destroyer is placed at their disposal.");
        setGameText("Deploy on table. During your deploy phase, you may reveal one Star Destroyer from hand to [upload] its matching pilot character (or vice versa) and deploy both simultaneously (for -1 Force each). [Immune to Alter]");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_8);
        addImmuneToCardTitle(Title.Alter);
    }
    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter starDestroyer = Filters.and(Filters.Star_Destroyer);
        Filter filter = Filters.and(Filters.or(Filters.pilot, starDestroyer), Filters.isUniquenessOnTableNotReached);

        GameTextActionId gameTextActionId = GameTextActionId.ALERT_MY_STAR_DESTROYER__DEPLOY_MATCHING_STAR_DESTROYER_OR_PILOT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal pilot or Star Destroyer from hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (Filters.character.accepts(game, selectedCard)) {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching Star Destroyer from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.and(starDestroyer, Filters.matchingStarship(selectedCard));
                            }
                            else {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching pilot from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.matchingPilot(selectedCard);
                            }
                            // Perform result(s)
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, -1, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}