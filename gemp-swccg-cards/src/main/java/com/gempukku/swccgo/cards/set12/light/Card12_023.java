package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayBePlacedOnOwnersPoliticalEffectModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Queen Amidala, Ruler Of Naboo (AI)
 */
public class Card12_023 extends AbstractRepublic {
    public Card12_023() {
        super(Side.LIGHT, 3, 3, 2, 4, 6, "Queen Amidala, Ruler Of Naboo", Uniqueness.UNIQUE);
        setAlternateImageSuffix(true);
        setPolitics(2);
        setLore("Naboo leader. Frustrated by the Trade Federation's control of her planet, Amidala came to the Senate to plead her case in person.");
        setGameText("Agendas: justice, peace. While in a senate majority, once during your control phase, may retrieve 1 Force for each Naboo site you control. May be placed on your Political Effect instead of a senator. Immune to attrition < 3.");
        addPersona(Persona.AMIDALA);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.JUSTICE, Agenda.PEACE));
        modifiers.add(new MayBePlacedOnOwnersPoliticalEffectModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.isInSenateMajority(game, self)) {
            int numToRetrieve = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Naboo_site, Filters.controlsWith(playerId, self, Filters.mayContributeToForceRetrieval)));
            if (numToRetrieve > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Retrieve " + numToRetrieve + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, numToRetrieve));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
