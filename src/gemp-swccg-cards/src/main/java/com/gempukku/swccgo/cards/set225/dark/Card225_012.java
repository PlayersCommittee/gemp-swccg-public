package com.gempukku.swccgo.cards.set225.dark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

/**
 * Set: Set 25
 * Type: Character
 * Subtype: Sith
 * Title: Baylan Skoll
 */
public class Card225_012 extends AbstractSith {
    public Card225_012() {
        super(Side.DARK, 1, 6, 6, 6, 7, "Baylan Skoll", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Mercenary.");
        setGameText("Once per game, may [download] a card with 'mercenary' in lore (or a lightsaber on Baylan). While in battle alone or with Shin, unless Sidious on table, opponent must have two characters (or Anakin or Yoda) in battle to draw battle destiny. Immune to attrition < 5.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_25);
        addKeyword(Keyword.MERCENARY);
        addPersona(Persona.BAYLAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BAYLAN_SKOLL__DOWNLOAD_CARD;
        
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter mercenaryLoreFilter = Filters.loreContains("mercenary");
            Filter cardFilter = Filters.or(mercenaryLoreFilter, Filters.lightsaber);
            Filter targetFilter = Filters.persona(Persona.BAYLAN);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a card with 'merenary' in lore (or a lightsaber on Baylan) from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, cardFilter, targetFilter, mercenaryLoreFilter, null, false, true));

            return Collections.singletonList(action);
        }        
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Condition inBattle = new InBattleCondition(self);
        Condition aloneOrWithShin = new OrCondition(new AloneCondition(self), new WithCondition(self, Filters.persona(Persona.SHIN)));
        Condition unlessSidiousOnTable = new UnlessCondition(new OnTableCondition(self, Filters.Sidious));
        Condition unlessTwoOpponentsCharacters = new UnlessCondition(new WithCondition(self, 2, Filters.and(Filters.opponents(self), Filters.character)));
        Condition unlessAnakinOrYoda = new UnlessCondition(new WithCondition(self, Filters.or(Filters.Anakin, Filters.Yoda)));

        // in battle AND aloneOrWithShin AND No Sidious AND Unless with 2 opponent characters AND Unless with Anakin or Yoda
        Condition masterCondition = new AndCondition(inBattle, aloneOrWithShin, unlessSidiousOnTable, unlessTwoOpponentsCharacters, unlessAnakinOrYoda);
        
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawBattleDestinyModifier(self, Filters.here(self), masterCondition, opponent));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
