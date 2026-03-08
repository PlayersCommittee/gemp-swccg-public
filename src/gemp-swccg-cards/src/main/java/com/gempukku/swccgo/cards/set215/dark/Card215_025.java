package com.gempukku.swccgo.cards.set215.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.ResetForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Epic Event
 * Title: Emperor's Orders
 */
public class Card215_025 extends AbstractEpicEventDeployable {
    public Card215_025() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Emperors_Orders, Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setGameText("If you did not deploy an Objective, deploy on table. We're Not Going To Attack?: Once per turn, may [download] a battleground system. You may not deploy non-Imperial starships. Unless you occupy a battleground site, Dreaded Imperial Starfleet is suspended. The Alliance Will Die...: Flagship Operations does not require any Executor sites on table to deploy, and does not target squadrons. ...As Will Your Friends: At battleground systems where you have a piloted capital starship / TIE pair, Force drains = 2. During battle at a site related to a system you occupy, may add one destiny to total power and/or re-circulate.");
        addIcons(Icon.VIRTUAL_SET_15);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.didNotDeployAnObjective(game, playerId);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourPilotedCapitalStarship = Filters.and(Filters.your(playerId), Filters.piloted, Filters.capital_starship);
        Filter yourPilotedTIE = Filters.and(Filters.your(playerId), Filters.piloted, Filters.TIE);

        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.starship, Filters.not(Filters.Imperial_starship)), playerId));
        modifiers.add(new SuspendsCardModifier(self, Filters.title(Title.Dreaded_Imperial_Starfleet), new UnlessCondition(new OccupiesCondition(playerId, Filters.battleground_site))));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Flagship_Operations, ModifyGameTextType.FLAGSHIP_OPERATIONS__MAY_IGNORE_DEPLOYMENT_RESTRICTIONS));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.squadron, Filters.Flagship_Operations));
        modifiers.add(new ResetForceDrainModifier(self, Filters.and(Filters.battleground_system, Filters.sameLocationAs(self, Filters.and(yourPilotedCapitalStarship, Filters.with(self, yourPilotedTIE)))), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        
        Filter sitesRelatedToSystemsYouOccupy = Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId)));

        GameTextActionId gameTextActionId = GameTextActionId.EMPERORS_ORDERS__DOWNLOAD_SYSTEM;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy battleground system from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.system, Filters.battleground, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, sitesRelatedToSystemsYouOccupy)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny to total power");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyToTotalPowerEffect(action, 1));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, sitesRelatedToSystemsYouOccupy)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Re-circulate");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RecirculateEffect(action, playerId));
            actions.add(action);
        }
        return actions;
    }
}
