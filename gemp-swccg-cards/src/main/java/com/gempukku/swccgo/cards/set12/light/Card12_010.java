package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Ki-Adi-Mundi
 */
public class Card12_010 extends AbstractRepublic {
    public Card12_010() {
        super(Side.LIGHT, 2, 5, 5, 6, 6, "Ki-Adi-Mundi", Uniqueness.UNIQUE);
        setLore("Cerean Jedi trained by Yoda since the age of four. Only Jedi Council member who is a Jedi Knight. Freed his homeworld from a group of rogues without any bloodshed.");
        setGameText("Deploys +3 if not to Jedi Council Chamber. While at a battleground site, if opponent just initiated a battle at same or adjacent site, opponent must lose top card of their Reserve Deck (if possible). Immune to attrition < 5 while at Jedi Council Chamber.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
        setSpecies(Species.CEREAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, 3, Filters.not(Filters.Jedi_Council_Chamber)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.sameOrAdjacentSite(self))
                && GameConditions.isAtLocation(game, self, Filters.battleground)
                && GameConditions.hasReserveDeck(game, opponent)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose top card of Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new LoseTopCardOfReserveDeckEffect(action, opponent));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new AtCondition(self, Filters.Jedi_Council_Chamber), 5));
        return modifiers;
    }
}
