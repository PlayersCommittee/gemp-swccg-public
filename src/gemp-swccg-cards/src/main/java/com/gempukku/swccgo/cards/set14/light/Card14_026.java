package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Queen Amidala (AI)
 */
public class Card14_026 extends AbstractRepublic {
    public Card14_026() {
        super(Side.LIGHT, 2, 4, 3, 4, 7, "Queen Amidala", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setAlternateImageSuffix(true);
        setLore("Leader. Amidala was only twelve when she was elected Princess of Theed. Now at age fourteen, she is Naboo's Queen, and the savior of her planet.");
        setGameText("Deploys -1 to Naboo. Amidala and your other Republic characters present are defense value +2, and immune to You Are Beaten, Sniper, and attrition < 3.");
        addPersona(Persona.AMIDALA);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_at_Naboo));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter amidalaAndOtherCharacters = Filters.or(self, Filters.and(Filters.your(self), Filters.other(self),
                Filters.Republic, Filters.character, Filters.present(self)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, amidalaAndOtherCharacters, 2));
        modifiers.add(new ImmuneToTitleModifier(self, amidalaAndOtherCharacters, Title.You_Are_Beaten));
        modifiers.add(new ImmuneToTitleModifier(self, amidalaAndOtherCharacters, Title.Sniper));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, amidalaAndOtherCharacters, 3));
        return modifiers;
    }
}
