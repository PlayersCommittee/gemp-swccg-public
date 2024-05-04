package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Slayn & Korpil Facilities
 */
public class Card7_076 extends AbstractNormalEffect {
    public Card7_076() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Slayn & Korpil Facilities", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Based in the Roche Asteroid Field. Worked with Admiral Ackbar to develop the B-wing fighter. Verpine owned, and that means quality.");
        setGameText("Deploy on Roche system. Once during each of your deploy phases, you may deploy a starship weapon from Reserve Deck on your B-wing; reshuffle. Also, retrieve 2 Force whenever your starfighter or vehicle 'hits' an opponent's starship or vehicle. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Roche_system;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SLAYN_AND_KORPIL_FACILITIES__DOWNLOAD_STARSHIP_WEAPON;
        Filter yourBwing = Filters.and(Filters.your(self), Filters.B_wing);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, yourBwing)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy starship weapon from Reserve Deck");
            action.setActionMsg("Deploy a starship weapon on a B-wing from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.starship_weapon, yourBwing, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.and(Filters.opponents(self), Filters.or(Filters.starship, Filters.vehicle)),
                Filters.any, Filters.and(Filters.your(self), Filters.or(Filters.starfighter, Filters.vehicle)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 2 Force");
            action.setActionMsg("Have " + playerId + " retrieve 2 Force");
            // Perform result(s)
            if (TriggerConditions.justHitBy(game, effectResult, Filters.not(Filters.mayContributeToForceRetrieval), Filters.any, Filters.any)
                    || TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.not(Filters.mayContributeToForceRetrieval), Filters.any)
                    || TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.any, Filters.not(Filters.mayContributeToForceRetrieval))) {
                action.appendEffect(
                        new SendMessageEffect(action, "Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval"));
            }
            else {
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 2));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}