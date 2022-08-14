package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 5
 * Type: Character
 * Subtype: Jedi Master
 * Title: Mace Windu (V)
 */
public class Card601_163 extends AbstractJediMaster {
    public Card601_163() {
        super(Side.LIGHT, 1, 7, 6, 7, 8, "Mace Windu", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Senior Jedi Council member who maintains rigorous adherence to the Code. Sent Qui-Gon to Naboo to accompany the Queen and learn more about the mysterious 'dark warrior'.");
        setGameText("Mace's game text may not be canceled (except at Senate) and he may fire one weapon twice per battle. Boba Fett may not add battle destiny draws here. Immune to attrition.");
        addPersona(Persona.MACE);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR, Icon.LEGACY_BLOCK_5);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, new NotCondition(new AtCondition(self, Filters.Galactic_Senate))));
        modifiers.add(new MayFireOneWeaponTwicePerBattleModifier(self, new DuringBattleCondition(), Filters.weapon));
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.and(Filters.Boba_Fett, Filters.here(self))));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }
}
