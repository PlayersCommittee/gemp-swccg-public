package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveAwayFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Subtype: Utinni
 * Title: Meteor Impact?
 */
public class Card3_107 extends AbstractUtinniEffect {
    public Card3_107() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Meteor Impact?", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R1);
        setLore("'There's a meteorite that hit the ground near here. I wanna check it out. Won't take long.'");
        setGameText("Use 2 Force to deploy on an exterior planet site. Target a character on same planet. Character may not leave planet or move away from Utinni Effect. Utinni Effect canceled when reached by target.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.exterior_planet_site;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.character, Filters.onSamePlanet(deployTarget));
    }

    @Override
    public void updateTargetFiltersAfterTargetsChosen(Action action, SwccgGame game, PhysicalCard self) {
        int deployTargetGroupId = self.getTargetGroupId(TargetId.DEPLOY_TARGET);
        int utinniEffectTarget1GroupId = self.getTargetGroupId(TargetId.UTINNI_EFFECT_TARGET_1);
        action.updatePrimaryTargetFilter(deployTargetGroupId, Filters.and(Filters.exterior_planet_site, Filters.onSamePlanetAs(self, Filters.inActionTargetGroup(action, utinniEffectTarget1GroupId))));
        action.updatePrimaryTargetFilter(utinniEffectTarget1GroupId, Filters.and(Filters.character, Filters.onSamePlanetAs(self, Filters.inActionTargetGroup(action, deployTargetGroupId))));
    }

    @Override
    public void updateTargetFiltersAfterOnTable(SwccgGame game, PhysicalCard self) {
        self.updateValidTargetedFilter(TargetId.UTINNI_EFFECT_TARGET_1, Filters.and(Filters.character, Filters.onSamePlanet(self)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter targetFilter = Filters.targetedByCardOnTableAsTargetId(self, TargetId.UTINNI_EFFECT_TARGET_1);
        Filter filter = Filters.and(Filters.or(targetFilter, Filters.hasAttachedWithRecursiveChecking(targetFilter)), Filters.atSameOrRelatedLocation(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveAwayFromLocationModifier(self, filter, Filters.sameLocation(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && target != null
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))
                && GameConditions.canBeCanceled(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}