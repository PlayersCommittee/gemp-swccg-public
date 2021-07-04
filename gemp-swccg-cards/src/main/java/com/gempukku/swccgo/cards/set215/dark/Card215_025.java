package com.gempukku.swccgo.cards.set215.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 15
 * Type: Epic Event
 * Title: Emperor's Orders
 */
public class Card215_025 extends AbstractEpicEventDeployable {
    public Card215_025() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Emperors_Orders);
        setGameText("If you did not deploy an Objective, deploy on Executor." +
                "We're Not Going To Attack?: Your squadrons are destiny = 0. You may not deploy squadrons or non-Imperial starships. Unless you occupy a battleground site, Dreaded Imperial Starfleet is suspended." +
                "The Alliance Will Die...: Flagship Operations does not require any Executor sites on table to deploy." +
                "...As Will Your Friends: At battleground systems where you have a piloted capital starship and a piloted TIE, your Force drains = 2. At sites related to systems you control, during battle may add one destiny to total power. If just lost, lose 3 Force.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Executor;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.didNotDeployAnObjective(game, playerId);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Flagship_Operations, ModifyGameTextType.FLAGSHIP_OPERATIONS__MAY_IGNORE_DEPLOYMENT_RESTRICTIONS));
        modifiers.add(new ResetDestinyModifier(self, Filters.and(Filters.your(playerId), Filters.squadron), 0));
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.squadron, Filters.and(Filters.starship, Filters.not(Filters.Imperial_starship))), playerId));
        modifiers.add(new SuspendsCardModifier(self, Filters.title(Title.Dreaded_Imperial_Starfleet), new UnlessCondition(new OccupiesCondition(playerId, Filters.battleground_site))));
        modifiers.add(new ResetForceDrainModifier(self, Filters.and(Filters.battleground_system, Filters.sameLocationAs(self, Filters.and(Filters.your(self.getOwner()), Filters.and(Filters.piloted, Filters.TIE), Filters.with(self, Filters.capital_starship)))), 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose 3 Force");
            action.setActionMsg("Lose 3 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, self.getOwner(), 3)
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter sitesRelatedToSystemsYouControl = Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.controls(playerId)));

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleAt(game, sitesRelatedToSystemsYouControl)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add one battle destiny and one destiny to power");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyToTotalPowerEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
