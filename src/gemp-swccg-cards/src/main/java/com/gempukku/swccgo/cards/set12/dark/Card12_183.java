package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CloakUntilEndOfNextTurnEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Starship
 * Subtype: Starfighter
 * Title: Maul's Sith Infiltrator (AI)
 */
public class Card12_183 extends AbstractStarfighter {
    public Card12_183() {
        super(Side.DARK, 6, 3, 4, null, 5, 6, 6, "Maul's Sith Infiltrator", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setAlternateImageSuffix(true);
        setLore("One of Sienar Advanced Projects Laboratories' prototype designs. Equipped with advanced weaponry and a full-effect stygium-based cloaking device for invisibility on command.");
        setGameText("May add 2 pilots. Maul deploys -3 aboard. While Maul piloting, immune to attrition and during your move phase, may lose 2 Force to 'cloak' (does not participate in battles) until end of next turn.");
        addPersona(Persona.MAULS_SITH_INFILTRATOR);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.INDEPENDENT, Icon.NAV_COMPUTER);
        addModelType(ModelType.SITH_INFILTRATOR);
        setPilotCapacity(2);
        setMatchingPilotFilter(Filters.Maul);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.Maul, -3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.Maul, -3, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, new HasPilotingCondition(self, Filters.Maul)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)
                && GameConditions.hasPiloting(game, self, Filters.Maul)
                && GameConditions.canCloak(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Cloak'");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 2, true));
            // Perform result(s)
            action.appendEffect(
                    new CloakUntilEndOfNextTurnEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
