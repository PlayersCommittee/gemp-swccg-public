package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Death Star Tractor Beam
 */
public class Card2_111 extends AbstractDevice {
    public Card2_111() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Death_Star_Tractor_Beam, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("'Why are we moving towards it?'");
        setGameText("Use 2 Force to deploy on Docking Bay 327. At the end of a battle at Death Star system, may target an opponent's starship present (except a Mon Calamari star cruiser) using 2 Force. Draw two destiny. Target captured if total destiny > defense value.");
        addKeywords(Keyword.TRACTOR_BEAM, Keyword.DEPLOYS_ON_SITE);
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
        return Filters.Docking_Bay_327;
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Docking_Bay_327;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.Docking_Bay_327;
    }

    @Override
    public TractorBeamAction getTractorBeamAction(SwccgGame game, PhysicalCard self) {
        return new TractorBeamAction(self, self, getTargetFilter(self), 2, false, 2, Statistic.DEFENSE_VALUE);
    }

    private Filter getTargetFilter(PhysicalCard self) {
        return Filters.and(Filters.opponents(self), Filters.starship, Filters.presentAt(Filters.Death_Star_system), Filters.not(Filters.Star_Cruiser), Filters.not(Filters.captured_starship));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleEndingAt(game, effectResult, Filters.Death_Star_system)
                && GameConditions.canSpot(game, self, getTargetFilter(self))
                && GameConditions.canUseForce(game, playerId, 2)
                && game.getDarkPlayer().equals(playerId)) {

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