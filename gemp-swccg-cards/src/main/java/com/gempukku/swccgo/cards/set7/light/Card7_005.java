package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.TargetedByUtinniEffectCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Bothan Spy
 */
public class Card7_005 extends AbstractAlien {
    public Card7_005() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Bothan Spy");
        setLore("Bothans operate the most complex spy network in the galaxy. Discovered the location of the second Death Star. Ambitious. Resourceful. Furry. Tend to die in large numbers.");
        setGameText("May be targeted (instead of a droid) by Death Star Plans. When targeted by Death Star Plans, makes that Effect immune to Alter and adds one destiny to Force retrieved with Death Star Plans. May not be targeted by Nabrun Leids.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.SPY);
        setSpecies(Species.BOTHAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition targetedByUtinniEffectCondition = new TargetedByUtinniEffectCondition(self, Filters.Death_Star_Plans);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeTargetedByModifier(self, Title.Death_Star_Plans));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Death_Star_Plans, targetedByUtinniEffectCondition, Title.Alter));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Death_Star_Plans, targetedByUtinniEffectCondition,
                ModifyGameTextType.DEATH_STAR_PLANS__ADD_DESTINY_TO_FORCE_RETRIEVED));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.Nabrun_Leids));
        return modifiers;
    }
}
