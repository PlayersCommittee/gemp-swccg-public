package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
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
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Character
 * Subtype: Droid
 * Title: OOM-9 (V)
 */
public class Card601_021 extends AbstractDroid {
    public Card601_021() {
        super(Side.DARK, 3, 2, 2, 4, "OOM-9", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setArmor(4);
        setLore("Officer battle droid who acted as a unit leader in the attack against the Gungans. Required advice from the Droid Control Ship in order to defeat the Gungan energy shield.");
        setGameText("Adds 2 to power of anything he pilots. Draws one battle destiny if unable to otherwise. While at a battleground, you lose no force to Amidala and your force generation is +1 for each of opponent's non-battlegrounds on table (and cards stacked on their Epic Events).");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT, Icon.PRESENCE, Icon.LEGACY_BLOCK_8);
        addKeywords(Keyword.LEADER, Keyword.OFFICER_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Amidala, new AtCondition(self, Filters.battleground), self.getOwner()));
        modifiers.add(new TotalForceGenerationModifier(self, new AtCondition(self, Filters.battleground),
                new AddEvaluator(new OnTableEvaluator(self, Filters.and(Filters.opponents(self), Filters.non_battleground_location)),
                        new StackedEvaluator(self, Filters.and(Filters.opponents(self), Filters.Epic_Event)))
                , self.getOwner()));
        return modifiers;
    }
}
