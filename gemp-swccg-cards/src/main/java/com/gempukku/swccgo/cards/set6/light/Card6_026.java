package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Leslomy Tacema
 */
public class Card6_026 extends AbstractAlien {
    public Card6_026() {
        super(Side.LIGHT, 3, 2, 1, 2, 2, "Leslomy Tacema", Uniqueness.UNIQUE);
        setLore("Female Duros. Expert pilot. Helps run the docking facilities in Mos Eisley. Friends with Ellorrs Madak. Approves all cargo manifests. Forgiving of minor transgressions.");
        setGameText("Adds 3 to power of anything she pilots. While at Audience Chamber, adds 2 to the power bonus provided by Ellorrs Madak.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.DUROS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Ellorrs_Madak, new AtCondition(self, Filters.Audience_Chamber),
                ModifyGameTextType.ELLORRS_MADAK__ADDITIONAL_2_TO_POWER_BONUS));
        return modifiers;
    }
}
