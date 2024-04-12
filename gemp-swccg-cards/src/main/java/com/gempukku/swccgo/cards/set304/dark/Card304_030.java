package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Kamjin Lap'lamiz, Proconsul
 */
public class Card304_030 extends AbstractImperial {
    public Card304_030() {
        super(Side.DARK, 1, 5, 4, 6, 7, "Kamjin Lap'lamiz, Proconsul", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Having returned from history, Kamjin has risen again to the position of Proconsul of Scholae Palatinae. He's slowly integrated himself with the Council as a Hand of Justice. No one knows his plans.");
		setGameText("Adds +3 to anything he pilots. While aboard a starship, it is immune to attrition < 5. During battle at same site (twice if with [CSP Icon]), may cumulatively subtract 2 from a just drawn destiny. Immune to Komilia Lap'lamiz, Exile and attrition < 5.");
        addPersona(Persona.KAMJIN);
        addIcons(Icon.CSP, Icon.WARRIOR, Icon.PILOT);
        addKeywords(Keyword.LEADER, Keyword.HAND);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.starship, Filters.hasAboard(self)), 5));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
		modifiers.add(new ImmuneToTitleModifier(self, Title.Komilia_Laplamiz_Exile));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.sameSite(self))) {
            int numTimes = GameConditions.isWith(game, self, Filters.and(Filters.your(self), Filters.CSP)) ? 2 : 1;
            if (GameConditions.isNumTimesPerBattle(game, self, playerId, numTimes, gameTextSourceCardId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setRepeatableTrigger(true);
                action.setText("Subtract 2 from destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new NumTimesPerBattleEffect(action, numTimes));
                // Perform result(s)
                action.appendEffect(
                        new ModifyDestinyEffect(action, -2, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
