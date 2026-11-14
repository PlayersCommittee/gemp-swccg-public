package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextPowerModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Character
 * Subtype: Droid
 * Title: B1 Clanker
 */
public class Card227_007 extends AbstractDroid {
    public Card227_007() {
        super(Side.DARK, 0, 1, null, 3, "B1 Clanker", Uniqueness.RESTRICTED_6, ExpansionSet.SET_27, Rarity.V);
        setArmor(3);
        setLore("Manufactured by the Baktoid Combat Automata, battle droids are used by the Trade Federation throughout the galaxy in order to secure and protect sites of strategic importance.");
        setGameText("Infantry battle droid. * Power = number of [Separatist] systems on table. While alone, you may not Force drain here. Immune to Abyssin Ornament.");
        addIcons(Icon.SEPARATIST, Icon.EPISODE_I, Icon.PRESENCE, Icon.VIRTUAL_SET_27);
        addKeywords(Keyword.INFANTRY_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextPowerModifier(self, new OnTableEvaluator(self, Filters.and(Icon.SEPARATIST, Filters.system))));
        modifiers.add(new ImmuneToTitleModifier(self, "Abyssin Ornament"));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.sameLocation(self), new AloneCondition(self), playerId));
        return modifiers;
    }
}
