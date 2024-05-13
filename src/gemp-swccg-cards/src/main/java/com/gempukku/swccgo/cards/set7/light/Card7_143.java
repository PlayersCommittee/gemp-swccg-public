package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasMatchingPilotAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gold 6
 */
public class Card7_143 extends AbstractStarfighter {
    public Card7_143() {
        super(Side.LIGHT, 3, 1, 2, null, 3, 4, 3, Title.Gold_6, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("'Led an attack on an Imperial outpost at Kashyyyk. Disabled the outpost's shields with ion cannons. Flown by Hol Okand during the Battle of Yavin.");
        setGameText("If deployed to Kashyyyk, may retrieve 1 Force. May add 2 pilots or passengers. SW-4 Ion Cannon deploys and fires free aboard. Immune to attrition < 4 when matching pilot aboard.");
        addIcons(Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.GOLD_SQUADRON);
        addModelType(ModelType.Y_WING);
        setPilotOrPassengerCapacity(2);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployedToSystem(game, effectResult, self, Title.Kashyyyk)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.SW4_Ion_Cannon, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.SW4_Ion_Cannon, Filters.attachedTo(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasMatchingPilotAboardCondition(self), 4));
        return modifiers;
    }
}
