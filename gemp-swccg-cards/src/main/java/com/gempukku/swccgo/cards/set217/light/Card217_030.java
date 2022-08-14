package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * SubType: Alien
 * Title: Bo-Katan
 */
public class Card217_030 extends AbstractAlien {
    public Card217_030() {
        super(Side.LIGHT, 2, 3, 3, 3, 5, "Bo-Katan", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Female Mandalorian leader and scout.");
        setGameText("Your Mandalorians here are immune to Imperial Barrier and Stunning Leader. Blasters deploy and fire for free on Bo-Katan. If another Mandalorian (or your weapon) here, opponent's characters here may not have their forfeit value increased.");
        addKeywords(Keyword.FEMALE, Keyword.LEADER, Keyword.SCOUT);
        addPersona(Persona.BO_KATAN);
        setSpecies(Species.MANDALORIAN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.here(self), Filters.Mandalorian), Title.Imperial_Barrier));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.here(self), Filters.Mandalorian), Title.Stunning_Leader));
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.blaster, Filters.persona(Persona.BO_KATAN)));
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.blaster, Filters.attachedTo(Filters.persona(Persona.BO_KATAN)))));
        modifiers.add(new MayNotHaveForfeitValueIncreasedModifier(self, Filters.and(Filters.opponents(self), Filters.character, Filters.here(self)), new WithCondition(self, Filters.or(Filters.Mandalorian, Filters.and(Filters.your(self), Filters.weapon_or_character_with_permanent_weapon)))));
        return modifiers;
    }
}
