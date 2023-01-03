package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Character
 * Subtype: Alien
 * Title: Sebulba AI (V)
 */
public class Card211_061 extends AbstractAlien {
    public Card211_061() {
        super(Side.DARK, 2, 3, 4, 2, 5, Title.Sebulba, Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setLore("Bad tempered Dug from Pixelito. He was about to turn Jar Jar into orange goo, until Anakin intervened.");
        setGameText("[Pilot] 2. Deploys free to Mos Espa. While on Tatooine, attrition against opponent is +1 here and your Force generation is +1. When You're A Slave? places a card in your Used Pile, may draw top card of your Reserve Deck.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.TATOOINE, Icon.EPISODE_I, Icon.PILOT);
        setSpecies(Species.DUG);
        setVirtualSuffix(true);
        setAlternateImageSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Mos_Espa));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition onTatooineCondition = new OnCondition(self, Title.Tatooine);
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AttritionModifier(self, Filters.here(self), onTatooineCondition, 1, game.getOpponent(self.getOwner())));
        modifiers.add(new TotalForceGenerationModifier(self, onTatooineCondition, 1, self.getOwner()));
        modifiers.add(new ModifyGameTextModifier(self, Filters.title(Title.Youre_A_Slave), ModifyGameTextType.YOURE_A_SLAVE__DRAW_TOP_CARD_OF_RESERVE_DECK_WHEN_PLACING_A_CARD_IN_USED_PILE));
        return modifiers;
    }
}