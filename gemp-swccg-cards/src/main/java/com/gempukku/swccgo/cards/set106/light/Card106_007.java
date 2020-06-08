package com.gempukku.swccgo.cards.set106.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red Squadron X-wing
 */
public class Card106_007 extends AbstractStarfighter {
    public Card106_007() {
        super(Side.LIGHT, 2, 5, 3, null, 4, 5, 4, "Red Squadron X-wing", Uniqueness.RESTRICTED_3);
        setLore("Most Red Squadron pilots trained under Garven Dreis. Flew top cover during the Battle of Yavin. Became famous for Red 5's historic attack run.");
        setGameText("Deploy -2 at Yavin 4 or to same location as Red Leader. Permanent pilot provides ability of 2 and adds 2 to power. Proton Torpedoes deploy and fire free aboard.");
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.RED_SQUADRON);
        addModelType(ModelType.X_WING);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.or(Filters.Deploys_at_Yavin_4, Filters.sameLocationAs(self, Filters.Red_Leader))));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 2));
                        return modifiers;
                    }
                });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Proton_Torpedoes, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.Proton_Torpedoes, Filters.attachedTo(self))));
        return modifiers;
    }
}
