package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ShuttledResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Subtype: Immediate
 * Title: Alert My Star Destroyer!
 */
public class Card7_220 extends AbstractImmediateEffect {
    public Card7_220() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Alert My Star Destroyer!", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("For important Imperial dignitaries, an individual Star Destroyer is placed at their disposal.");
        setGameText("If your admiral, moff or Dark Jedi just shuttled aboard your Star Destroyer, deploy on that character. Star Destroyer is immune to attrition and Flagship deploys free on that starship. Immediate Effect lost if character not aboard that starship.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter yourStarDestroyer = Filters.and(Filters.your(self), Filters.Star_Destroyer);

        // Check condition(s)
        if (TriggerConditions.justShuttledTo(game, effectResult, Filters.and(Filters.your(self), Filters.or(Filters.admiral, Filters.moff, Filters.Dark_Jedi)),
                Filters.or(yourStarDestroyer, Filters.and(Filters.starship_site, Filters.relatedSiteTo(self, yourStarDestroyer))))) {
            Collection<PhysicalCard> cardsShuttled = Filters.filter(((ShuttledResult) effectResult).getMovedCards(), game, Filters.and(Filters.your(self), Filters.or(Filters.admiral, Filters.moff, Filters.Dark_Jedi)));
            if (!cardsShuttled.isEmpty()) {

                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.in(cardsShuttled), null);
                if (action != null) {

                    // Remember the Star Destroyer (by card id or persona)
                    PhysicalCard shuttledTo = ((ShuttledResult) effectResult).getMovedTo();
                    WhileInPlayData whileInPlayData;

                    if (Filters.Star_Destroyer.accepts(game, shuttledTo)) {
                        if (!shuttledTo.getBlueprint().getPersonas().isEmpty()) {
                            whileInPlayData = new WhileInPlayData(true, shuttledTo.getBlueprint().getPersonas().iterator().next());
                        } else {
                            whileInPlayData = new WhileInPlayData(false, shuttledTo);
                        }
                    } else if (Filters.starship_site.accepts(game, shuttledTo)) {
                        if (shuttledTo.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
                            whileInPlayData = new WhileInPlayData(true, shuttledTo.getBlueprint().getRelatedStarshipOrVehiclePersona());
                        } else {
                            whileInPlayData = new WhileInPlayData(false, shuttledTo.getRelatedStarshipOrVehicle());
                        }
                    } else {
                        return null;
                    }
                    action.appendBeforeCost(
                            new SetWhileInPlayDataEffect(action, self, whileInPlayData));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.or(Filters.admiral, Filters.moff, Filters.Dark_Jedi);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard character = self.getAttachedTo();
        WhileInPlayData whileInPlayData = self.getWhileInPlayData();

        // Check condition(s)
        if (character != null && whileInPlayData != null
                && TriggerConditions.isTableChanged(game, effectResult)) {
            boolean checkByPersona = whileInPlayData.getBooleanValue();
            if ((checkByPersona && !GameConditions.isAboard(game, character, whileInPlayData.getPersona()))
                    || (!checkByPersona && !GameConditions.isAboard(game, character, Filters.sameCardId(whileInPlayData.getPhysicalCard())))) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        WhileInPlayData whileInPlayData = self.getWhileInPlayData();
        Filter starDestroyerFilter = whileInPlayData.getBooleanValue() ? Filters.persona(whileInPlayData.getPersona()) : Filters.sameCardId(whileInPlayData.getPhysicalCard());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, starDestroyerFilter));
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Flagship, starDestroyerFilter));
        return modifiers;
    }
}