package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Republic
 * Title: Yarua (V)
 */
public class Card216_049 extends AbstractRepublic {
    public Card216_049() {
        super(Side.LIGHT, 2, 4, 5, 2, 4, "Yarua", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setPolitics(2);
        setLore("Kashyyyk's senior Wookiee senator. Believes that a thorough taxation plan will assist funding of other worthwhile Republic programs. Despises the corruption around him.");
        setGameText("If drawn for destiny, each of your Wookiees is power +1 for remainder of turn. Agenda: taxation. At Kashyyyk sites where you have two Wookiees present and your total power > 10, your Force drains are +1.");
        setSpecies(Species.WOOKIEE);
        addKeywords(Keyword.SENATOR);
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.CORUSCANT, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AgendaModifier(self, Agenda.TAXATION));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredDrawnAsDestinyTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isDestinyCardMatchTo(game, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Your Wookiees are power +1");
            action.setActionMsg("Your Wookiees are power +1 for remainder of turn");
            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                    new PowerModifier(self, Filters.and(Filters.your(self), Filters.Wookiee), 1)
                    , "Your Wookiees are power +1 for remainder of turn"));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter locationFilter = Filters.and(Filters.Kashyyyk_site,
                Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.Wookiee, Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Wookiee)))),
                Filters.totalPowerGreaterThan(self, self.getOwner(), 10));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, locationFilter, 1, self.getOwner()));
        return modifiers;
    }
}
