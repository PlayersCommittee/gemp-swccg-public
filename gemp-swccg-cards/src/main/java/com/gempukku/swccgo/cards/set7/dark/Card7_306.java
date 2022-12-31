package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Starfighter
 * Title: OS-72-2 In Obsidian 2
 */
public class Card7_306 extends AbstractStarfighter {
    public Card7_306() {
        super(Side.DARK, 3, 4, 1, null, 3, null, 2, "OS-72-2 In Obsidian 2", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Piloted by Dodson Makraven, wingman of Cive Rashon. Experienced TIE pilot with many kills in atmospheric combat. Nicknamed 'Night Beast' for his many curfew violations.");
        setGameText("Deploy -1 and power +3 at a cloud sector. Permanent pilot is •OS-72-2, who provides ability of 2, adds 2 to power and, at a cloud sector, may draw one battle destiny if not able to otherwise.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.OBSIDIAN_SQUADRON);
        addModelType(ModelType.TIE_LN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.cloud_sector));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.cloud_sector), 3));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.OS_72_2, 2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 2));
                        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AtCondition(self, Filters.cloud_sector), 1));
                        return modifiers;
                    }
                });
    }
}
