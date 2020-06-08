package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Orrimaarko
 */
public class Card8_023 extends AbstractRebel {
    public Card8_023() {
        super(Side.LIGHT, 1, 5, 4, 4, 4, "Orrimaarko", Uniqueness.UNIQUE);
        setLore("Dresselian Scout and resistance leader. Worked tirelessly to combat the subjugation of his homeworld before Bothans brought him into contact with the Alliance.");
        setGameText("When Orrimaarko is in a battle you just won against an Imperial, opponent must lose 2 Force. Immune to attrition < 3 (or < 4 when present at a forest, jungle or exterior Endor site).");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        setSpecies(Species.DRESSELIAN);
        addKeywords(Keyword.SCOUT, Keyword.LEADER);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.wonBattle(game, effectResult, playerId)
                && GameConditions.isInBattleWith(game, self, Filters.and(Filters.opponents(self), Filters.Imperial))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerId), 2));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new CardMatchesEvaluator(3, 4,
                Filters.presentAt(Filters.or(Filters.forest, Filters.jungle, Filters.exterior_Endor_site)))));
        return modifiers;
    }
}
