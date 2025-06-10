package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AllAbilityInBattleProvidedByCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Title: Ewok Celebration
 */
public class Card13_019 extends AbstractNormalEffect {
    public Card13_019() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ewok Celebration", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("'Na na, beecha na noooooooooooooooowa.'");
        setGameText("Deploy on table. At Endor sites where you have an Ewok, your characters are forfeit and defense value +1 (+3 if you have two Ewoks at that site.) In battles at Endor sites where all your ability is provided by Ewoks, add one battle destiny. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_III);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourCharactersAtSameEndorSiteAsEwok = Filters.and(Filters.your(self), Filters.character,
                Filters.at(Filters.and(Filters.Endor_site, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Ewok)))));
        final int permCardId = self.getPermanentCardId();
        Evaluator evaluator = new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                return Filters.canSpot(game, self, 2, Filters.and(Filters.your(self), Filters.Ewok, Filters.atSameSite(cardAffected))) ? 3 : 1;
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, yourCharactersAtSameEndorSiteAsEwok, evaluator));
        modifiers.add(new DefenseValueModifier(self, yourCharactersAtSameEndorSiteAsEwok, evaluator));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new DuringBattleAtCondition(Filters.Endor_site),
                new AllAbilityInBattleProvidedByCondition(playerId, Filters.Ewok)), 1));
        return modifiers;
    }
}