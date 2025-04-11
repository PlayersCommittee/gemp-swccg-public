package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Rebel
 * Title: Teikhos Ta'var, Jedi Defender
 */
public class Card305_040 extends AbstractRebel {
    public Card305_040() {
        super(Side.LIGHT, 1, 7, 5, 6, 8, "Teikhos Ta'var, Jedi Defender", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setLore("Zelton Jedi. Husband, father, and teacher of Jedi. Takes the pacifism of Jedi philosophy more seriously than most. Padawans are warned not to talk with him about the Praxeum gardens.");
        setGameText("[Pilot] 1. Deploys -2 to Quermia. Power +2 if a Sith present. If opponent just initiated a battle at a related Quermia site, may use 2 force to relocate Teikhos to that site. Immune to Sniper and attrition < 5.");
        addPersona(Persona.TEIKHOS);
        addIcons(Icon.ABT, Icon.COU, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.MALE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_at_Quermia));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, game.getOpponent(playerId), Filters.and(Filters.Quermia_site, Filters.relatedSite(self)))) {
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
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new PowerModifier(self, new PresentCondition(self, Filters.Sith), 2));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sniper));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
