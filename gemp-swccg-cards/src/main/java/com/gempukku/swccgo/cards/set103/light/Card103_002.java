package com.gempukku.swccgo.cards.set103.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Rebel Leader Pack)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red Leader In Red 1
 */
public class Card103_002 extends AbstractStarfighter {
    public Card103_002() {
        super(Side.LIGHT, 2, 6, 3, null, 4, 5, 5, "Red Leader In Red 1", Uniqueness.UNIQUE, ExpansionSet.REBEL_LEADER_PACK, Rarity.PM);
        setLore("Called 'Boss' or 'Chief' by his squadron, Garven Dreis was the first pilot to fire proton torpedoes at the Death Star's exhaust port during the Battle of Yavin.");
        setGameText("Permanent pilot aboard is •Red Leader, who provides ability of 2, adds 2 to power and may draw one battle destiny if not able to otherwise.");
        addPersonas(Persona.RED_1);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.NAV_COMPUTER);
        addModelType(ModelType.X_WING);
        addKeywords(Keyword.RED_SQUADRON);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.RED_LEADER, 2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 2));
                        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
                        return modifiers;
                    }
                });
    }
}
