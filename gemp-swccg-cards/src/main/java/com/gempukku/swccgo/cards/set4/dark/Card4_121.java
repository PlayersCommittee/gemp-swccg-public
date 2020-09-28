package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.LeaderModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Field Promotion
 */
public class Card4_121 extends AbstractNormalEffect {
    public Card4_121() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Field_Promotion);
        setLore("Imperial officers are fiercely competitive, especially on the Executor. Lord Vader's flagship is a place where devious political calculation prospers. The naive are doomed to failure.");
        setGameText("Deploy on an Imperial of ability < 5 present with Vader, Emperor or one of your admirals, generals, or moffs. Imperial gains leader skill, is power +1, and is immune to Demotion, Report to Lord Vader, and What is Thy Bidding, My Master? (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Imperial, Filters.abilityLessThan(5),
                Filters.presentWith(self, Filters.or(Filters.Vader, Filters.Emperor,
                        Filters.and(Filters.your(self), Filters.or(Filters.admiral, Filters.general, Filters.moff)))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LeaderModifier(self, hasAttached));
        modifiers.add(new PowerModifier(self, hasAttached, 1));
        modifiers.add(new ImmuneToTitleModifier(self, hasAttached, Title.Demotion));
        modifiers.add(new ImmuneToTitleModifier(self, hasAttached, Title.Report_To_Lord_Vader));
        modifiers.add(new ImmuneToTitleModifier(self, hasAttached, Title.What_Is_Thy_Bidding_My_Master));
        return modifiers;
    }
}