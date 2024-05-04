package com.gempukku.swccgo.cards.set8.dark;

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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoBattleDamageModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Security Precautions
 */
public class Card8_132 extends AbstractNormalEffect {
    public Card8_132() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Security_Precautions, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("'I think I got it.'");
        setGameText("Deploy on a location. Your Force generation is +1 here. Also, if you 'probe' opponent's 'Hidden Base' system, opponent loses X Force, where X = 15 - number of systems on table. You take no battle damage where you have a 'probe' card. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.location;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.here(self), 1, playerId));
        modifiers.add(new NoBattleDamageModifier(self, Filters.hasStacked(Filters.probeCard), playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.hiddenBaseProbed(game, effectResult)) {
            int valueOfX = Math.max(0, 15 - Filters.countTopLocationsOnTable(game, Filters.system));
            if (valueOfX > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " lose " + valueOfX + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, valueOfX));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}