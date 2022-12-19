package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Jedi Temple
 */
public class Card219_031 extends AbstractSite {
    public Card219_031() {
        super(Side.LIGHT, "Coruscant: Jedi Temple", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLocationDarkSideGameText("");
        setLocationLightSideGameText("During your move phase, [Episode I] Jedi may move between here and a battleground site. " +
                                     "While a Jedi here, opponent's characters deploy +2 here and cancels Force drains at Jedi Council Chamber.");
        addIcon(Icon.DARK_FORCE, 0);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.character), new HereCondition(self, Filters.Jedi), 2, self));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.Jedi_Council_Chamber, new HereCondition(self, Filters.Jedi), game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter otherBattlegroundSites = Filters.and(Filters.other(self), Filters.battleground_site);


        // Move a Jedi Council member
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, otherBattlegroundSites)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game, Filters.and(Icon.EPISODE_I, Filters.Jedi), self, otherBattlegroundSites, false)) {
                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.and(Icon.EPISODE_I, Filters.Jedi), self, otherBattlegroundSites, false);
                action.setText("Move from here to other site");
                action.setActionMsg("Move an [Episode 1] Jedi from " + GameUtils.getCardLink(self) + " to other battleground (or Coruscant) site");
                actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerOnLightSideOfLocation, game,Filters.and(Icon.EPISODE_I, Filters.Jedi), otherBattlegroundSites, self, false)) {
                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnLightSideOfLocation, game, self, gameTextSourceCardId, Filters.and(Icon.EPISODE_I, Filters.Jedi), otherBattlegroundSites, self, false);
                action.setText("Move from other site to here");
                action.setActionMsg("Move an [Episode 1] Jedi from other battleground site to " + GameUtils.getCardLink(self));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerOnDarkSideOfLocation, Filters.Jedi_Council_Chamber)
                && GameConditions.isHere(game, self, Filters.Jedi)
                && GameConditions.canCancelForceDrain(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }

        return null;
    }
}
