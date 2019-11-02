package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

/**
 * Set: PT Set 12
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, Rebel Scout (V)
 */

public class Card501_007 extends AbstractRebel {
    public Card501_007() {
        super(Side.LIGHT, 1, 6, 6, 6, 9, "Luke Skywalker, Rebel Scout", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Resourceful scout and member of the Rebel infiltration team assembled to destroy the shield generator. Surrendered to his father so that he would not endanger the mission.");
        setGameText("[Pilot] 2. If drawn for destiny, may take into hand to cancel and redraw that destiny. Power +1 for each Dark Jedi here. Luke's weapon destiny draws are +1. At same site, opponent must first use 1 Force to fire a weapon. Immune to attrition < 6.");
        addPersona(Persona.LUKE);
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR, Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_12);
        addKeywords(Keyword.SCOUT);
        setTestingText("Luke Skywalker, Rebel Scout (V) errata");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.Dark_Jedi)));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1));
        modifiers.add(new ExtraForceCostToFireWeaponModifier(self, Filters.and(Filters.opponents(self), Filters.atSameSite(self)), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalDrawnAsDestinyTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (GameConditions.isDestinyCardMatchTo(game, self)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take into hand and cause re-draw");
            action.setActionMsg("Cancel destiny and cause re-draw");
            // Pay cost(s)
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}