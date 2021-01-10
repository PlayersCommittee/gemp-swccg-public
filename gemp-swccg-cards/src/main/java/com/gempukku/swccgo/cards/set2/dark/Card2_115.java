package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TractorBeamAction;
import com.gempukku.swccgo.logic.effects.UseTractorBeamEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Device
 * Title: Tractor Beam
 */
public class Card2_115 extends AbstractDevice {
    public Card2_115() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Tractor Beam");
        setLore("Captures enemy vessels for boarding. When used in conjunction with an ion cannon, tractor beams can ensnare even the most maneuverable starships.");
        setGameText("Use 2 Force to deploy on your Star Destroyer. At the end of a battle at same system or sector, may target an opponent's starship present (except Mon Calamari Star Cruiser) using 2 Force. Draw destiny. Target captured if destiny > defense value.");
        addKeyword(Keyword.TRACTOR_BEAM);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Star_Destroyer);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Star_Destroyer;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.Star_Destroyer;
    }

    @Override
    public TractorBeamAction getTractorBeamAction(SwccgGame game, PhysicalCard self) {
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.starship, Filters.present(self), Filters.not(Filters.Star_Cruiser), Filters.not(Filters.captured_starship));

        return new TractorBeamAction(self, self, targetFilter, 2, false, 1, Statistic.DEFENSE_VALUE);
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleEndingAt(game, effectResult, Filters.here(self))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Use tractor beam");
            // Perform result(s)

            // use the tractor beam here
            action.appendEffect(new UseTractorBeamEffect(action, self, false));

            return Collections.singletonList(action);
        }
        return null;
    }
}