package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractCreatureVehicle;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.ImmuneToUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAwayAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Vehicle
 * Subtype: Creature
 * Title: Terrence
 */
public class Card304_037 extends AbstractCreatureVehicle {
    public Card304_037() {
        super(Side.DARK, 4, 1, 2, null, 2, 2, 3, "Terrence", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Ngyapin Ngyapin");
	setGameText("May add 1 'rider' (passenger). Ability = 1/4. May move as a 'react' from a battle. Adds 1 to power of each of your [CSP Icon] and other Tauntauns present. When ridden by Thran, adds one battle destiny.");
        addIcons(Icon.CSP);
        addKeywords(Keyword.TAUNTAUN);
        setPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextAbilityModifier(self, 0.25));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAwayAsReactModifier(self, new InBattleCondition(self)));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.CSP,
                Filters.and(Filters.other(self), Filters.tauntaun)), Filters.present(self)), 1));
        modifiers.add(new AddsBattleDestinyModifier(self, new HasAboardCondition(self, Filters.Thran), 1));
		return modifiers;
    }
}
