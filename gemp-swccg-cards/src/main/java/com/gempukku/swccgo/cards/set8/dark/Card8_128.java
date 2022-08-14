package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveOrBattleUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Perimeter Patrol
 */
public class Card8_128 extends AbstractNormalEffect {
    public Card8_128() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Perimeter_Patrol, Uniqueness.UNIQUE);
        setLore("Heavy Imperial patrols on Endor forced the Rebels to deploy covertly. The required stealth measures created many complications.");
        setGameText("Deploy on Bunker. While you control Bunker, each time opponent deploys a vehicle, starship or Rebel to an Endor site (except Rebel Landing Site), that card cannot move or battle for remainder of turn and opponent must lose 1 Force. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Bunker;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.or(Filters.vehicle, Filters.starship, Filters.Rebel),
                Filters.and(Filters.Endor_site, Filters.except(Filters.Rebel_Landing_Site)))) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard location = playCardResult.getToLocation();
            if ((Filters.Bunker.accepts(game, location) && playCardResult.isPlayedToSiteControlledByOpponent())
                    || GameConditions.controls(game, playerId, Filters.Bunker)) {
                PhysicalCard cardDeployed = playCardResult.getPlayedCard();

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Respond to " + GameUtils.getFullName(cardDeployed) + " just deployed");
                action.setActionMsg("Make " + opponent + " lose 1 Force and prevent " + GameUtils.getCardLink(cardDeployed) + " from moving or battling until end of turn");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 1));
                action.appendEffect(
                        new MayNotMoveOrBattleUntilEndOfTurnEffect(action, cardDeployed));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}