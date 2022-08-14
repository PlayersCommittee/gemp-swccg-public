package com.gempukku.swccgo.cards.set3.light;

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
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Title: Echo Base Operations
 */
public class Card3_034 extends AbstractNormalEffect {
    public Card3_034() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, Title.Echo_Base_Operations, Uniqueness.UNIQUE);
        setLore("Following the Battle of Yavin, the Alliance garnered the support of systems ready to oppose the Empire. Echo Base provides a command center for focusing that support.");
        setGameText("Deploy on Main Power Generators if you occupy at least three Echo sites. At every system location, you deploy is -1, your Force drains are +2 and your total power is +3 in battles. Effect canceled if opponent occupies five Hoth sites. (Immune to Alter.).");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.HOTH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Main_Power_Generators;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.occupies(game, playerId, 3, Filters.Echo_site);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.your(self), -1, Filters.system));
        modifiers.add(new ForceDrainModifier(self, Filters.system, 2, playerId));
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.system, Filters.battleLocation), 3, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && GameConditions.occupies(game, opponent, getSiteCountToCancelEffect(game, self), SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Hoth_site)) {

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

    private int getSiteCountToCancelEffect(SwccgGame game, PhysicalCard self) {
        return GameConditions.hasGameTextModification(game, self, ModifyGameTextType.EBO__ADDITIONAL_SITE_TO_CANCEL) ? 6 : 5;
    }
}