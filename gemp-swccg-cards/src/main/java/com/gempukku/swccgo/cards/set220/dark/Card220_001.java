package com.gempukku.swccgo.cards.set220.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 20
 * Type: Character
 * Subtype: Alien
 * Title: Bib Fortuna, Heir To The Palace
 */
public class Card220_001 extends AbstractAlien {
    public Card220_001() {
        super(Side.DARK, 1, 4, 2, 1, 3, "Bib Fortuna, Heir To The Palace", Uniqueness.UNIQUE);
        setLore("Twi'lek gangster. Leader. Plotted to kill Jabba.");
        setGameText("Jabba's game text is canceled. While with two aliens, adds one destiny to attrition. While at Audience Chamber, " +
                    "your Force drains at other Tatooine battlegrounds are +1 and, if opponent just deployed a character here, " +
                    "may activate 1 Force.");
        addPersona(Persona.BIB);
        setSpecies(Species.TWILEK);
        addKeywords(Keyword.LEADER, Keyword.GANGSTER);
        addIcons(Icon.VIRTUAL_SET_20);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.Jabba));
        modifiers.add(new AddsDestinyToAttritionModifier(self, new WithCondition(self, 2, Filters.alien), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.not(Filters.here(self)), Filters.Tatooine_location, Filters.battleground), new AtCondition(self, Filters.Audience_Chamber), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if(TriggerConditions.justDeployedToLocation(game, effectResult, game.getOpponent(playerId), Filters.character, Filters.here(self))
            && GameConditions.isAtLocation(game, self, Filters.Audience_Chamber)
            && GameConditions.canActivateForce(game, playerId)){

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);

            action.setText("Activate 1 Force");
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
