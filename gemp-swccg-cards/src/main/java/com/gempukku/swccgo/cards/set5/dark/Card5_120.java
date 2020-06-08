package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.CancelEffectsOfRevolutionModifier;
import com.gempukku.swccgo.logic.modifiers.CancelOpponentsForceDrainBonusesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Imperial Decree
 */
public class Card5_120 extends AbstractNormalEffect {
    public Card5_120() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Decree);
        setLore("To Imperial command personnel: The Rebellion must be crushed! Minor acts of sedition are to be ignored. The destruction of the Alliance is your primary goal.");
        setGameText("Deploy on your side of table. Whenever you control any two Rebel Base locations, or any one planet site and two systems, the effects of Revolution and all opponent's Force drain bonuses are canceled. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition gameTextModified = new GameTextModificationCondition(self, ModifyGameTextType.IMPERIAL_DECREE__DOES_NOT_COUNT_YAVIN_4_LOCATIONS);
        Condition conditionDefault = new OrCondition(new ControlsCondition(playerId, 2, Filters.Rebel_Base_location),
                new AndCondition(new ControlsCondition(playerId, Filters.planet_site), new ControlsCondition(playerId, 2, Filters.system)));
        Condition conditionModified = new OrCondition(new ControlsCondition(playerId, 2, Filters.and(Filters.Rebel_Base_location, Filters.not(Filters.Yavin_4_location))),
                new AndCondition(new ControlsCondition(playerId, Filters.and(Filters.planet_site, Filters.not(Filters.Yavin_4_location))),
                        new ControlsCondition(playerId, 2, Filters.and(Filters.system, Filters.not(Filters.Yavin_4_location)))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelEffectsOfRevolutionModifier(self, new AndCondition(new NotCondition(gameTextModified), conditionDefault)));
        modifiers.add(new CancelEffectsOfRevolutionModifier(self, new AndCondition(gameTextModified, conditionModified)));
        modifiers.add(new CancelOpponentsForceDrainBonusesModifier(self, new AndCondition(new NotCondition(gameTextModified), conditionDefault)));
        modifiers.add(new CancelOpponentsForceDrainBonusesModifier(self, new AndCondition(gameTextModified, conditionModified)));
        return modifiers;
    }
}