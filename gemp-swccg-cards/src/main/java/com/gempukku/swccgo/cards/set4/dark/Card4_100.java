package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.ResetForfeitEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Alien
 * Title: Dengar
 */
public class Card4_100 extends AbstractAlien {
    public Card4_100() {
        super(Side.DARK, 1, 4, 2, 2, 3, "Dengar", Uniqueness.UNIQUE);
        setLore("Corellian bounty hunter. Assassin trained by the Empire. Has reflex-enhancing cyber-implants. Gravely injured during a swoop race in the crystal swamp of Agrilat. Blames Han Solo.");
        setGameText("Adds 2 to power of anything he pilots. Power +1 for each opponent's character present. While present, may reduce Han's forfeit to zero.");
        addPersona(Persona.DENGAR);
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.ASSASSIN);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new PresentEvaluator(self, Filters.and(Filters.opponents(self), Filters.character))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isPresent(game, self)) {
            final PhysicalCard han = Filters.findFirstActive(game, self, Filters.and(Filters.Han, Filters.atSameLocation(self)));
            if (han != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Reduce " + GameUtils.getFullName(han) + "'s forfeit to 0");
                action.setActionMsg("Reduce " + GameUtils.getCardLink(han) + "'s forfeit to 0");
                // Allow response(s)
                action.appendEffect(
                        new ResetForfeitEffect(action, han, 0, new NotCondition(new PresentAtCondition(self, Filters.sameLocation(han)))));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
