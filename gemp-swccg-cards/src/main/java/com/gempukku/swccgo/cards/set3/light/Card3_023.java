package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Toryn Farr
 */
public class Card3_023 extends AbstractRebel {
    public Card3_023() {
        super(Side.LIGHT, 4, 2, 1, 2, 3, "Toryn Farr", Uniqueness.UNIQUE);
        setLore("Chief Controller at Echo Command. Responsible for communicating orders to the troops. Personally gives firing orders to Ion Cannon Control.");
        setGameText("Adds 2 to power of anything she pilots. When at any war room, adds 1 to weapon destiny draws of your Planet Defender Ion Cannon on same planet.");
        addIcons(Icon.HOTH, Icon.PILOT);
        addKeywords(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.your(self), Filters.Planet_Defender_Ion_Cannon, Filters.onSamePlanet(self)),
                new AtCondition(self, Filters.war_room), 1));
        return modifiers;
    }
}
