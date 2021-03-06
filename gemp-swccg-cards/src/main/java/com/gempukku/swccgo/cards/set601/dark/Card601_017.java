package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block  8
 * Type: Character
 * Subtype: Alien
 * Title: Ket Maliss, Shadow Killer
 */
public class Card601_017 extends AbstractAlien {
    public Card601_017() {
        super(Side.DARK, 3, 3, 4, 2, 4, "Ket Maliss, Shadow Killer", Uniqueness.UNIQUE);
        setLore("Assassins are highly valued by Jabba the Hutt and other gangsters. Ket Maliss, Prince Xizor's 'shadow killer,' has unknown but undoubtedly lethal business in Mos Eisley. Assassin.");
        setGameText("Adds 2 to power of anything he pilots. While with opponent's character of ability > 2, Ket Maliss is power +1 and defense value + 2 and draws one battle destiny if unable to otherwise. Whenever opponent's character was lost from same site, opponent loses 1 Force.");
        addIcons(Icon.A_NEW_HOPE, Icon.BLOCK_8, Icon.PILOT, Icon.WARRIOR);
        addKeyword(Keyword.ASSASSIN);
        setAsLegacy(true);
    }

    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    @Override
    public final float getSpecialDefenseValue() {
        return 3;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        Condition withCharacterCondition = new WithCondition(self, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityMoreThanOrEqualTo(2)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        //technically these should add to "Ket Maliss" instead of self
        modifiers.add(new PowerModifier(self, withCharacterCondition, 1));
        modifiers.add(new DefenseValueModifier(self, withCharacterCondition, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, withCharacterCondition, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.sameSite(self))) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 Force");
            action.setActionMsg(opponent+" loses 1 Force");
            // Perform result(s)
            action.appendEffect(new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
