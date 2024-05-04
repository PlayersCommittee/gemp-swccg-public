package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: Colonel Feyn Gospic
 */
public class Card7_011 extends AbstractRebel {
    public Card7_011() {
        super(Side.LIGHT, 2, 3, 2, 3, 4, "Colonel Feyn Gospic", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("One of General Dodonna's chief strategic advisors. Planned the Rebel fleet's approach to the Death Star.");
        setGameText("Adds 2 to power of anything he pilots. Power +2 on Yavin 4. When at Massassi War Room, doubles Rebel Tech bonus to Attack Run. While present with Rebel Planners, that Effect applies separately to every system and immune to Alter.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new OnCondition(self, Title.Yavin_4), 2));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Rebel_Tech, new AtCondition(self, Filters.Massassi_War_Room),
                ModifyGameTextType.REBEL_TECH__DOUBLE_BONUS_TO_ATTACK_RUN));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Rebel_Planners, new PresentWithCondition(self, Filters.Rebel_Planners),
                ModifyGameTextType.REBEL_PLANNERS__APPLIES_TO_EVERY_SYSTEM));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Rebel_Planners, new PresentWithCondition(self, Filters.Rebel_Planners), Title.Alter));
        return modifiers;
    }
}
