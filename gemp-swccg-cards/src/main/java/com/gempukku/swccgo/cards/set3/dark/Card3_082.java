package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Ozzel
 */
public class Card3_082 extends AbstractImperial {
    public Card3_082() {
        super(Side.DARK, 0, 0, 3, 2, 4, Title.Ozzel, Uniqueness.UNIQUE);
        setLore("Leader of the Emperor's Death Squadron assigned to hunt down and crush the Rebellion. As clumsy as he is stupid. Has just failed Darth Vader for the next-to-last time.");
        setGameText("Adds 2 to the power of anything he pilots. Subtracts 1 from deploy cost of each of your capital starships at same system. Lost if Vader on table and opponent 'reacts' to same location as Ozzel.");
        addIcons(Icon.HOTH, Icon.PILOT);
        addPersona(Persona.OZZEL);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.capital_starship),
                -1, Filters.sameSystem(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.reactedToLocation(game, effectResult, opponent, Filters.sameLocation(self))
                && GameConditions.canSpot(game, self, Filters.Vader)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
