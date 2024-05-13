package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtRandomCardInOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.CalculationVariableModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Device
 * Title: Probe Antennae
 */
public class Card3_097 extends AbstractCharacterDevice {
    public Card3_097() {
        super(Side.DARK, 4, "Probe Antennae", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U2);
        setLore("A probe droid encodes and scrambles messages before using its telescoping antennae to transmit information through hyperspace.");
        setGameText("Deploy on your Probe Droid. Adds 2 to X for that droid. OR Use 1 Force to deploy on one of your other droids. When at a site you control, once during each of your control phases, you may peek at one card randomly selected from opponent's hand.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 1, Filters.and(Filters.droid, Filters.not(Filters.probe_droid))));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.droid);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.droid;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CalculationVariableModifier(self, Filters.and(Filters.hasAttached(self), Filters.Probe_Droid), 2, Variable.X));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.isAttachedTo(game, self, Filters.not(Filters.Probe_Droid))
                && GameConditions.hasHand(game, opponent)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.isAtLocation(game, self, Filters.and(Filters.site, Filters.controls(playerId)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Peek at one random card in opponents hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtRandomCardInOpponentsHandEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}