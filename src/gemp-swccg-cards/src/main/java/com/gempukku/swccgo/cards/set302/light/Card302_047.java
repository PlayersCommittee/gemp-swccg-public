package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.DuringSabaccCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.SabaccWinnerDeterminedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Location
 * Subtype: Site
 * Title: Dandoran: Garganta Galleria Casino Hotel
 */
public class Card302_047 extends AbstractSite {
    public Card302_047() {
        super(Side.LIGHT, Title.Garganta_Galleria_Casino_Hotel, Title.Dandoran, Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLocationDarkSideGameText("Unless you have a gambler here, you may not use wild cards in Garganta Galleria Sabacc.");
        setLocationLightSideGameText("Whenever your gambler present here wins Garganta Galleria Sabacc, retrieve 3 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, new AndCondition(new DuringSabaccCondition(Filters.Garganta_Galleria_Sabacc),
                new UnlessCondition(new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.gambler)))),
                ModifierFlag.MAY_NOT_USE_WILD_CARDS_IN_SABACC, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.wonGargantaGalleriaSabacc(game, effectResult, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.gambler, Filters.present(self)))) {
            final PhysicalCard winner = ((SabaccWinnerDeterminedResult) effectResult).getWinningCharacter();
            int amountToRetrieve = Filters.Lando.accepts(game, winner) ? 3 : 3;

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve " + amountToRetrieve + " Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerOnLightSideOfLocation, amountToRetrieve) {
                        @Override
                        public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                            return Collections.singletonList(winner);
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}