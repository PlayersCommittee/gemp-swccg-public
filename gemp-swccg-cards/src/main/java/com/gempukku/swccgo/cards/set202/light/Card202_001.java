package com.gempukku.swccgo.cards.set202.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.InPlayDataNotSetCondition;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeChokedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 2
 * Type: Character
 * Subtype: Rebel
 * Title: Leia Organa (V)
 */
public class Card202_001 extends AbstractRebel {
    public Card202_001() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, "Leia Organa", Uniqueness.UNIQUE, ExpansionSet.SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Strong-willed princess from Alderaan. Youngest Imperial Senator ever. Used diplomatic immunity to spy for Rebels. Led relief effort on Ralltir. Natural leader.");
        setGameText("Agenda: rebellion. May not be 'choked'. Gains [Republic] at Galactic Senate. Draws one battle destiny if unable to otherwise (if with an Imperial leader, may add one destiny to total power or attrition instead). 'Diplomatic' immunity to attrition < 3.");
        addPersona(Persona.LEIA);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_2);
        addKeywords(Keyword.SENATOR, Keyword.SPY, Keyword.LEADER, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.REBELLION));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition condition = new InPlayDataNotSetCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeChokedModifier(self));
        modifiers.add(new IconModifier(self, new AtCondition(self, Filters.Galactic_Senate), Icon.REPUBLIC));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, condition, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattleWith(game, self, Filters.Imperial_leader)) {
            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Add one destiny to total power");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
                action.appendEffect(
                        new AddDestinyToTotalPowerEffect(action, 1));
                actions.add(action);
            }
            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Add one destiny to attrition");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
                action.appendEffect(
                        new AddDestinyToAttritionEffect(action, 1));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (self.getWhileInPlayData() != null && !GameConditions.isDuringBattle(game)) {
            self.setWhileInPlayData(null);
        }
        return null;
    }
}
