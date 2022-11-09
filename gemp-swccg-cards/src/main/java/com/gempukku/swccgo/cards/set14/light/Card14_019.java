package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Jedi Master
 * Title: Mace Windu, Jedi Master (AI)
 */
public class Card14_019 extends AbstractJediMaster {
    public Card14_019() {
        super(Side.LIGHT, 1, 8, 6, 7, 8, "Mace Windu, Jedi Master", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setAlternateImageSuffix(true);
        setLore("Jedi Council member who is known to be one of the strongest members of the Council. Has come to Naboo to investigate the death of the mysterious 'dark warrior.'");
        setGameText("Deploys -2 on Naboo. While on Naboo, once during your control phase may use 1 Force to cancel Maul's immunity to attrition for remainder of turn. Immune to attrition < 3 (< 5 while on Naboo).");
        addPersona(Persona.MACE);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_on_Naboo));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.isOnSystem(game, self, Title.Naboo)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Maul, Filters.hasAnyImmunityToAttrition))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Cancel Maul's immunity to attrition");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, Filters.Maul, "Cancels Maul's immunity to attrition"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 5, new OnCondition(self, Title.Naboo))));
        return modifiers;
    }
}
