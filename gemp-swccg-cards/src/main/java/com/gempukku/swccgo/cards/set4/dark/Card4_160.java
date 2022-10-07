package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovedOnlyByOpponentModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Executor: Control Station
 */
public class Card4_160 extends AbstractUniqueStarshipSite {
    public Card4_160() {
        super(Side.DARK, "Executor: Control Station", Persona.EXECUTOR);
        setLocationDarkSideGameText("If an Imperial leader here, Executor is power +1 for each Executor site on table.");
        setLocationLightSideGameText("If you control, only you may move Executor during opponent's move phase.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.Executor, new HereCondition(self, Filters.Imperial_leader), new OnTableEvaluator(self, Filters.Executor_site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovedOnlyByOpponentModifier(self, Filters.Executor,
                new AndCondition(
                        new ControlsCondition(playerOnLightSideOfLocation, self),
                        new PhaseCondition(Phase.MOVE, game.getOpponent(playerOnLightSideOfLocation)))));
        return modifiers;
    }
}