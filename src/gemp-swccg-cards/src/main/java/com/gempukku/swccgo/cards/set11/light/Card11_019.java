package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Subtype: Immediate
 * Title: I Can't Believe He's Gone
 */
public class Card11_019 extends AbstractImmediateEffect {
    public Card11_019() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.I_Cant_Believe_Hes_Gone, Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.C);
        setLore("Even though Luke felt the pain of losing his mentor, Obi-Wan continued to give him strength and guidance through the Force.");
        setGameText("If Obi-Wan was just placed out of play, use 1 Force to deploy Immediate Effect on table. If a battle was just initiated at a site, may use 1 Force to increase your total power by 5. (Immune to Control.)");
        addIcons(Icon.TATOOINE);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justPlacedOutOfPlayFromTable(game, effectResult, Filters.ObiWan)) {

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        Filter locationFilter = Filters.site;

        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.I_CANT_BELIEVE_HES_GONE__ONLY_EFFECTS_BATTLES_WITH_LUKE_OR_LEIA)) {
            locationFilter = Filters.and(locationFilter, Filters.sameLocationAs(self, Filters.or(Filters.Luke, Filters.Leia)));
        }

        if (TriggerConditions.battleInitiatedAt(game, effectResult, locationFilter)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 5 to total power");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new ModifyTotalPowerUntilEndOfBattleEffect(action, 5, playerId, "Adds 5 to total power"));
            return Collections.singletonList(action);
        }
        return null;
    }
}