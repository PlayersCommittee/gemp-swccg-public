package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Sergeant Doallyn
 */
public class Card6_038 extends AbstractAlien {
    public Card6_038() {
        super(Side.LIGHT, 4, 3, 3, 2, 4, Title.Doallyn, Uniqueness.UNIQUE);
        setLore("Humanoid mercenary. Gambler. Blackmailed into working for Jabba. Friend of Yarna d'al' Gargan. Expert in breathing apparatus. Needs Hydron 3 cartridges to breathe.");
        setGameText("When Doallyn is on Tatooine, Tusken Breath Mask may target one of your characters on Tatooine, is immune to Alter, is not unique, doubles its power and forfeit bonuses and provides protection from Gravel Storm and Sandwhirl.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeywords(Keyword.GAMBLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onTatooine = new OnCondition(self, Filters.title(Title.Doallyn), Title.Tatooine);
        Filter tuskenBreathMask = Filters.Tusken_Breath_Mask;

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, tuskenBreathMask, onTatooine, Title.Alter));
        modifiers.add(new NotUniqueModifier(self, tuskenBreathMask, onTatooine));
        modifiers.add(new ModifyGameTextModifier(self, tuskenBreathMask, onTatooine, ModifyGameTextType.TUSKEN_BREATH_MASK__MODIFIED_BY_SERGEANT_DOALLYN));
        return modifiers;
    }
}
