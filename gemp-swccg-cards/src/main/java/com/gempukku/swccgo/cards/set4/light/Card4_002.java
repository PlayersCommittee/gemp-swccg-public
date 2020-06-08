package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda
 */
public class Card4_002 extends AbstractJediMaster {
    public Card4_002() {
        super(Side.LIGHT, 1, 5, 2, 7, 9, "Yoda", Uniqueness.UNIQUE);
        setLore("Help you I can, yes. For 800 years have I trained Jedi. Judge me by my size do you? Mm? And well you should not! For my ally is the Force... and a powerful ally it is.");
        setGameText("Deploy on Dagobah I must, but move elsewhere I may, yes. When the mentor, 1 to training destiny I add. Where present am I, battles and attacks happen not unless present a Dark Side character of ability > 3 there is. To attrition, immune am I.");
        addIcons(Icon.DAGOBAH);
        addPersona(Persona.YODA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Dagobah;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToDagobahLocationModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition unlessDarkSideCharacterOfAbilityMoreThanThreePresent = new UnlessCondition(new PresentCondition(self,
                Filters.and(Filters.Dark_Side, Filters.character, Filters.abilityMoreThan(3))));
        Filter wherePresent = Filters.wherePresent(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalTrainingDestinyModifier(self, Filters.jediTestTargetingMentor(Filters.sameCardId(self)), 1));
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, wherePresent, unlessDarkSideCharacterOfAbilityMoreThanThreePresent));
        modifiers.add(new MayNotInitiateAttacksAtLocationModifier(self, wherePresent, unlessDarkSideCharacterOfAbilityMoreThanThreePresent));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }
}
