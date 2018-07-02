package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Republic
 * Title: Obi-Wan Kenobi, Padawan Learner (AI)
 */
public class Card11_007 extends AbstractRepublic {
    public Card11_007() {
        super(Side.LIGHT, 1, 6, 6, 5, 8, "Obi-Wan Kenobi, Padawan Learner", Uniqueness.UNIQUE);
        setAlternateImageSuffix(true);
        setLore("Qui-Gon Jinn's Padawan. Stayed behind to protect Queen Amidala when Qui-Gon left to explore Mos Espa, but was in constant communication should he be needed.");
        setGameText("Deploys -2 to Tatooine. If opponent just initiated a battle at a related Tatooine site, may use 2 Force to relocate Obi-Wan to that site. Immune to attrition < 4.");
        addPersona(Persona.OBIWAN);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.PADAWAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_at_Tatooine));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, game.getOpponent(playerId), Filters.and(Filters.Tatooine_site, Filters.relatedSite(self)))) {
            PhysicalCard battleLocation = game.getGameState().getBattleLocation();
            if (GameConditions.canUseForceToRelocateCard(game, self, 2, battleLocation)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Relocate to battle location");
                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(battleLocation));
                // Pay cost(s)
                action.appendCost(
                        new PayRelocateBetweenLocationsCostEffect(action, playerId, self, battleLocation, 2));
                // Perform result(s)
                action.appendEffect(
                        new RelocateBetweenLocationsEffect(action, self, battleLocation));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
