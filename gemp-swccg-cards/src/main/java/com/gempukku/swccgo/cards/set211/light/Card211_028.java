package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentAstromech;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11
 * Type: Starship
 * Subtype: Starfighter
 * Title: BB-8 In Black One
 */
public class Card211_028 extends AbstractStarfighter {
    public Card211_028() {
        super(Side.LIGHT, (float)(Math.PI), 3, 4, null, 5, 6, 5, "BB-8 In Black One", Uniqueness.UNIQUE);
        setGameText("May add 1 pilot. Permanent astromech aboard is •BB-8. If Poe piloting, may lose 1 Force to cancel a just drawn weapon destiny targeting this starship. Immune to attrition < 4.");
        addPersona(Persona.BLACK_1); // BB-8 Persona added in AbstractPermanentAboard later
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Poe);
        addModelType(ModelType.X_WING);
        addIcons(Icon.RESISTANCE, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentAstromech(Persona.BB8) {});
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)

        // Weapons draw from anything (Filters.any) targeting self,
        //  and Poe is piloting self,
        //  and we are allowed to cancel the destiny.
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, self)
                && GameConditions.hasPiloting(game, self, Filters.Poe)
                && GameConditions.canCancelDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel weapon destiny");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
