package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 1
 * Type: Effect
 * Title: Cell 2187 (V)
 */
public class Card601_129 extends AbstractNormalEffect {
    public Card601_129() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Cell_2187, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Aren't you a little short for a stormtrooper?'");
        setGameText("Deploy on table. Once per game, may deploy spy R2-D2 (for free) from Reserve Deck; reshuffle. During your control phase, if R2-D2 is with captive Leia, may use 1 Force to release her. While Sometimes I Amaze Even Myself on table, where Leia present, ignore Battle Order. May not be canceled. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_1);
        addKeyword(Keyword.CAN_RELEASE_CAPTIVES);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.wherePresent(self, Filters.Leia), new OnTableCondition(self, Filters.Sometimes_I_Amaze_Even_Myself), Title.Battle_Order));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CELL_2187_V__DEPLOY_R2D2;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.R2D2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy spy R2-D2");
            action.setActionMsg("Deploy spy R2-D2 from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.spy, Persona.R2D2),true, true));

            actions.add(action);
        }

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
            && GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, TargetingReason.TO_BE_RELEASED, Filters.and(Filters.Leia, Filters.captive, Filters.with(self, Filters.R2D2)))
            && GameConditions.canUseForce(game, playerId, 1)) {

            PhysicalCard leia = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE, TargetingReason.TO_BE_RELEASED, Filters.and(Filters.Leia, Filters.captive, Filters.with(self, Filters.R2D2)));

            if (leia != null) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Release "+ GameUtils.getFullName(leia));
                action.setActionMsg("Release " + GameUtils.getCardLink(leia));
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                action.appendEffect(
                        new ReleaseCaptiveEffect(action, leia));
                actions.add(action);
            }
        }

        return actions;
    }
}