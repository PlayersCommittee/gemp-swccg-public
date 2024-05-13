package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: Local Uprising / Liberation
 */
public class Card7_137 extends AbstractObjective {
    public Card7_137() {
        super(Side.LIGHT, 0, Title.Local_Uprising, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy any planet system and one ◇ site to that system. This system is the Subjugated planet. While this side up, once during each of your deploy phases, you may deploy one ◇ site to the Subjugated planet from Reserve Deck; reshuffle. Flip this card if your matching operatives control at least three battleground sites related to the Subjugated planet.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.planet_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Subjugated planet";
                    }
                    @Override
                    protected void cardDeployed(PhysicalCard card) {
                        String systemName = card.getBlueprint().getSystemName();
                        game.getGameState().setSubjugatedPlanet(systemName);
                        game.getModifiersQuerying().setExtraInformationForArchetypeLabel(playerId, systemName);

                        action.appendRequiredEffect(
                                new DeployCardToSystemFromReserveDeckEffect(action, Filters.and(Filters.generic, Filters.site), systemName, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose site to deploy";
                                    }
                                });
                    }
                });
        return action;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        String planet = game.getGameState().getSubjugatedPlanet();
        if (planet != null) {
            return "Subjugated planet is " + planet;
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String subjugatedPlanet = game.getGameState().getSubjugatedPlanet();

        GameTextActionId gameTextActionId = GameTextActionId.LOCAL_UPRISING__DOWNLOAD_SITE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy site from Reserve Deck");
            action.setActionMsg("Deploy a ◇ site to " + subjugatedPlanet + " from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.and(Filters.generic, Filters.site), subjugatedPlanet, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controlsWith(game, self, playerId, 3, Filters.and(Filters.battleground_site, Filters.Subjugated_planet_location), SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE,
                Filters.and(Filters.your(self), Filters.matchingOperativeToSubjugatedPlanet))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}