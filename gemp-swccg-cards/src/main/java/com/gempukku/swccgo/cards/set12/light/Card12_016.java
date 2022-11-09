package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Jedi Master
 * Title: Master Qui-Gon
 */
public class Card12_016 extends AbstractJediMaster {
    public Card12_016() {
        super(Side.LIGHT, 1, 8, 6, 7, 9, "Master Qui-Gon", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Jedi Master currently not on the Council. Although he serves the Council well, there have been times when he has defied their wishes to pursue a path he believes is right.");
        setGameText("Deploys -2 on Coruscant. If opponent's Dark Jedi on table, during your move phase may use 4 Force to return Qui-Gon (and all cards on him) to owner's hand. Immune to attrition and You Are Beaten.");
        addPersona(Persona.QUIGON);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_on_Coruscant));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.You_Are_Beaten));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.canUseForce(game, playerId, 4)
                && GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.Dark_Jedi))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Return to hand");
            action.setActionMsg("Return " + GameUtils.getCardLink(self) + " (and all cards on him) to hand");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 4));
            // Perform result(s)
            action.appendEffect(
                    new ReturnCardToHandFromTableEffect(action, self, Zone.HAND));
            return Collections.singletonList(action);
        }
        return null;
    }
}
