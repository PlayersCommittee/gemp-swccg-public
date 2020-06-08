package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Tessek
 */
public class Card6_044 extends AbstractAlien {
    public Card6_044() {
        super(Side.LIGHT, 1, 3, 1, 2, 3, Title.Tessek, Uniqueness.UNIQUE);
        setLore("Quarren accountant. Embezzling from Jabba. Leader. Escaped Mon Calamari after its subjugation by the Empire. Plotting to kill Jabba and free the Hutt's captives.");
        setGameText("Whenever your opponent deploys a character of destiny 1, you may activate 1 Force. While at Audience Chamber, adds 1 to your Force drains where you have a non-unique alien and all your non-unique aliens are deploy -1.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.QUARREN);
        addKeywords(Keyword.ACCOUNTANT, Keyword.LEADER);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(Filters.opponents(self), Filters.character, Filters.destinyEqualTo(1)))
                && GameConditions.canActivateForce(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Activate 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourNonuniqueAlien = Filters.and(Filters.your(self), Filters.non_unique, Filters.alien);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.sameLocationAs(self, yourNonuniqueAlien), atAudienceChamber, 1, self.getOwner()));
        modifiers.add(new DeployCostModifier(self, yourNonuniqueAlien, atAudienceChamber, -1));
        return modifiers;
    }
}
