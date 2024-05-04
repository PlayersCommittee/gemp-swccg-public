package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ReplaceAbility1PermanentPilotsModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Incom Corporation
 */
public class Card7_066 extends AbstractNormalEffect {
    public Card7_066() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Incom Corporation", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Incom's core design team defected to the Alliance with the plans for the famed X-wing fighter. Once with the Alliance, many former Incom test pilots flew for the Rebellion.");
        setGameText("Deploy on a Rebel Base interior site. Your ability-1 permanent pilots aboard T-16s, T-47s, Z-95s and X-wings (except squadrons) are replaced by ability-2 permanent pilots who add 1 to power. Canceled if opponent controls this site. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Rebel_Base_site, Filters.interior_site);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.hasPermanentPilot,
                Filters.or(Filters.T_16, Filters.T_47, Filters.Z_95, Filters.X_wing), Filters.except(Filters.squadron));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ReplaceAbility1PermanentPilotsModifier(self, filter, 2));
        modifiers.add(new PowerModifier(self, Filters.and(filter, Filters.isAbility1PermanentPilotReplaced), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && GameConditions.controls(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.sameSite(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
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