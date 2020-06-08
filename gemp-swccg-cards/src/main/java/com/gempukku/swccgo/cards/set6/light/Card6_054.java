package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Effect
 * Title: Bo Shuda
 */
public class Card6_054 extends AbstractNormalEffect {
    public Card6_054() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Bo_Shuda, Uniqueness.UNIQUE);
        setLore("In order to remain a successful crime lord, Jabba must ensure the safety of all those who seek to do business with him.");
        setGameText("Deploy on Audience Chamber. If you have an alien here, no battles or Force drains may take place here and your aliens cannot be targeted by Trap Door. Effect canceled if opponent occupies this site without an alien. (Immune to Alter.)");
        addIcons(Icon.JABBAS_PALACE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Audience_Chamber;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition yourHaveAnAlienHere = new HereCondition(self, Filters.and(Filters.your(self), Filters.alien));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, Filters.here(self), yourHaveAnAlienHere));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.here(self), yourHaveAnAlienHere));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.your(self), Filters.alien), Filters.Trap_Door));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && GameConditions.occupiesWithout(game, self, opponent, Filters.sameSite(self), SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.alien)) {

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