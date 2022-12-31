package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PutUndercoverEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Undercover
 */
public class Card2_040 extends AbstractNormalEffect {
    public Card2_040() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Undercover, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U2);
        setLore("Alliance Intelligence expends considerable resources to infiltrate the Imperial military bureaucracy, but the ISB's security sweeps make these shadowy operations dangerous.");
        setGameText("Deploy on your spy at a site and cross spy to opponent's side.  Spy is now Undercover.  During your deploy phase, may voluntarily 'break cover' (lose Effect) if at a site. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.spy, Filters.at(Filters.site), Filters.not(Filters.undercover_spy));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return getRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    private List<RequiredGameTextTriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard attachedTo = self.getAttachedTo();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            action.setText("Put " + GameUtils.getFullName(attachedTo) + " Undercover");
            action.setActionMsg("Put " + GameUtils.getCardLink(attachedTo) + " Undercover");
            // Perform result(s)
            action.appendEffect(
                    new PutUndercoverEffect(action, attachedTo));
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.coverBroken(game, effectResult, Filters.hasAttached(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}