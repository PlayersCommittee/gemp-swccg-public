package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Creature
 * Title: Krayt Dragon
 */
public class Card7_211 extends AbstractCreature {
    public Card7_211() {
        super(Side.DARK, 1, 7, null, 5, 0, "Krayt Dragon", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Krayt dragons have beautiful and valuable pearls inside them as a gizzard to grind up food. Their fierceness is legendary. Up to 10 meters tall and 30 meters long.");
        setGameText("Ferocity = 6 + two destiny. Habitat: deserts and Tatooine exterior sites. May not deploy to an occupied site. If defeated, opponent may draw destiny and retrieve Force equal to destiny draw.");
        addModelType(ModelType.DESERT);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.desert, Filters.exterior_Tatooine_site);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.unoccupied;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, 6, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDefeatedBy(game, effectResult, self, Filters.any)
                && GameConditions.canDrawDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Draw destiny to retrieve Force");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny == null) {
                                return;
                            }
                            action.appendEffect(
                                    new RetrieveForceEffect(action, playerId, totalDestiny));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
