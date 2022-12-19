package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ChoiceMadeResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 17
 * Type: Location
 * Subtype: Site
 * Title: Ajan Kloss: Training Course
 */
public class Card217_027 extends AbstractSite {
    public Card217_027() {
        super(Side.LIGHT, "Ajan Kloss: Training Course", Title.Ajan_Kloss, Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setLocationDarkSideGameText("");
        setLocationLightSideGameText("Deploys only as a starting location. If you just chose You Have That Power, Too on your [Skywalker] Epic Event, [download] My Parents Were Strong.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.SKYWALKER, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_VII, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        // Deploys only as a starting location.
        return GameConditions.isDuringStartOfGame(game)
                && game.getModifiersQuerying().getStartingLocation(playerId) == null
                && game.getGameState().getObjectivePlayed(playerId) == null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.AJAN_KLOSS_TRAINING_GROUND__DEPLOY_EFFECT;

        if (TriggerConditions.justMadeChoice(game, effectResult, playerOnLightSideOfLocation, Filters.and(Icon.SKYWALKER, Filters.Epic_Event))
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId, true, false)
                && "You Have That Power, Too".equals(((ChoiceMadeResult)effectResult).getChoice())) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerOnLightSideOfLocation);
            action.setText("Deploy My Parents Were Strong");
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.title("My Parents Were Strong"), GameConditions.isDuringStartOfGame(game), !GameConditions.isDuringStartOfGame(game)));
            return Collections.singletonList(action);
        }
        return null;
    }
}