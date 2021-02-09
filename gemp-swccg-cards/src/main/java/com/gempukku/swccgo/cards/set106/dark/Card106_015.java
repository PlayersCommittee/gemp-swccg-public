package com.gempukku.swccgo.cards.set106.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
 * Title: Obsidian Squadron TIE
 */
public class Card106_015 extends AbstractStarfighter {
    public Card106_015() {
        super(Side.DARK, 2, 4, 1, null, 3, null, 4, "Obsidian Squadron TIE", Uniqueness.RESTRICTED_3);
        setLore("Modified TIE fighter. Specifically adapted for atmospheric engagement. Some are fitted with high-output solar panels to support improved weaponry.");
        setGameText("Deploy -1 at any cloud sector. Permanent pilot provides ability of 2 and adds 2 to power. Power +2 at non-unique cloud sectors. Boosted TIE Cannon may deploy aboard and fires free aboard.");
        addIcons(Icon.PREMIUM, Icon.PILOT);
        addModelType(ModelType.TIE_LN);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.OBSIDIAN_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.cloud_sector));
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
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, self, new AtCondition(self, Filters.and(Filters.non_unique, Filters.cloud_sector)), 2));
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.Boosted_TIE_Cannon, Filters.attachedTo(self))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Boosted_TIE_Cannon), self));
        return modifiers;
    }
}
