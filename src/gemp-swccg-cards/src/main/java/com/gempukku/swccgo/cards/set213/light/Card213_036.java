package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractAlienRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien/Rebel
 * Title: Chewbacca (V)
 */
public class Card213_036 extends AbstractAlienRebel {
    public Card213_036() {
        super(Side.LIGHT, 1, 4, 7, 2, 6, "Chewbacca", Uniqueness.UNIQUE, ExpansionSet.SET_13, Rarity.V);
        setLore("Wookiee smuggler from Kashyyyk. Over 200 years old. Top-notch mechanic and pilot. Jabba has large bounty on this 'walking carpet.' Friends call him Chewie...or Fuzzball.");
        setGameText("Adds 2 to power of anything he pilots (3 if Falcon). May deploy -1 as a 'react' (-2 to same location as Han). If you have completed a Kessel Run, opponent's Force retrieval is canceled. During battle here (or at a holosite), your dejariks are destiny +2.");
        addPersona(Persona.CHEWIE);
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.WOOKIEE);
        setArmor(4);
        setMatchingStarshipFilter(Filters.Falcon);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.not(Filters.sameLocationAs(self, Filters.Han)), -1));
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.sameLocationAs(self, Filters.Han), -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new ConditionEvaluator(2, 3, new PilotingCondition(self, Filters.Falcon))));
        modifiers.add(new DestinyModifier(self, Filters.and(Filters.your(self.getOwner()), Filters.dejarik), new DuringBattleAtCondition(Filters.or(Filters.here(self), Filters.holosite)), 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToRetrieveForce(game, effectResult, game.getOpponent(self.getOwner()))
                && GameConditions.hasCompletedUtinniEffect(game, self.getOwner(), Filters.Kessel_Run)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel retrieval");
            action.setActionMsg("Force retrieval is canceled");
            action.appendEffect(
                    new CancelForceRetrievalEffect(action)
            );
            return Collections.singletonList(action);
        }

        return null;
    }
}
