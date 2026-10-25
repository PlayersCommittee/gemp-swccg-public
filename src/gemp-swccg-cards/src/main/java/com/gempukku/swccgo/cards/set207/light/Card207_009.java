package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataNotSetCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayFireWeaponFiredByForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 7
 * Type: Character
 * Subtype: Rebel
 * Title: Sabine Wren
 */
public class Card207_009 extends AbstractRebel {
    public Card207_009() {
        super(Side.LIGHT, 2, 3, 4, 2, 6, Title.Sabine, Uniqueness.UNIQUE, ExpansionSet.SET_7, Rarity.V);
        setArmor(5);
        setLore("Female Mandalorian. Scout.");
        setGameText("Once per turn, when firing a rifle or blaster, may target for free and add 2 to total weapon destiny. While with an Imperial (or two Rebels), whenever you win a battle here, opponent loses 2 Force. Immune to Hidden Weapons.");
        addPersona(Persona.SABINE);
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_7);
        addIcon(Icon.WARRIOR, 2);
        addKeywords(Keyword.FEMALE, Keyword.SCOUT);
        setSpecies(Species.MANDALORIAN);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (self.getWhileInPlayData() != null && TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }

        // Check condition(s)
        if (TriggerConditions.wonBattle(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)
                && (GameConditions.isInBattleWith(game, self, Filters.Imperial)
                || GameConditions.isInBattleWith(game, self, 2, Filters.Rebel))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setText("Make opponent lose 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerId), 2));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayFireWeaponFiredByForFreeModifier(self, new InPlayDataNotSetCondition(self), Filters.or(Filters.rifle, Filters.blaster)));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Hidden_Weapons));
        return modifiers;
    }
}


