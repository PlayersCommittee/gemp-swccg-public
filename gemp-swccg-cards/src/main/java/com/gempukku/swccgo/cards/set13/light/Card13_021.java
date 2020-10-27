package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Starship
 * Subtype: Starfighter
 * Title: Han, Chewie, And The Falcon
 */
public class Card13_021 extends AbstractStarfighter {
    public Card13_021() {
        super(Side.LIGHT, 2, 6, 4, null, 6, 7, 8, "Han, Chewie, And The Falcon", Uniqueness.UNIQUE);
        setComboCard(true);
        setLore("Although temperamental, this trusty hunk of junk always seems to perform for its proud owner and his Wookiee co-pilot when needed the most.");
        setGameText("Permanent pilots are •Han and •Chewie: provide ability of 5, add one battle destiny, and add 5 to power. Immune to attrition < 6, Come With Me, and Lateral Damage. End of your turn: Use 3 Force to maintain OR Place out of play.");
        addPersonas(Persona.FALCON,Persona.HAN,Persona.CHEWIE);
        addIcons(Icon.REFLECTIONS_III, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.MAINTENANCE);
        addIcon(Icon.PILOT, 2);
        addModelType(ModelType.HEAVILY_MODIFIED_LIGHT_FREIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(Persona.HAN, 3) {
            @Override
            public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                List<Modifier> modifiers = new LinkedList<Modifier>();
                modifiers.add(new PowerModifier(self, 5));
                modifiers.add(new AddsBattleDestinyModifier(self, 1));
                return modifiers;
            }});
        permanentsAboard.add(new AbstractPermanentPilot(Persona.CHEWIE, 2) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Come_With_Me));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Lateral_Damage));
        return modifiers;
    }

    @Override
    protected StandardEffect getGameTextMaintenanceMaintainCost(Action action, final String playerId) {
        return new UseForceEffect(action, playerId, 3);
    }
}
