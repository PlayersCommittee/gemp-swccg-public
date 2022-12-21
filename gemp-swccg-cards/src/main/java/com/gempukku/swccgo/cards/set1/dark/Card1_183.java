package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Kitik Keed'kak
 */
public class Card1_183 extends AbstractAlien {
    public Card1_183() {
        super(Side.DARK, 2, 6, 8, 1, 2, "Kitik Keed'kak", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("A female insectoid Yam'rii. Very strong and easily angered. Known for stealth, but also has good technological aptitude. Meat eater. Loves eggs.");
        setGameText("Immediately lost if at same site as overprotective mother, Kal'Faln C'ndros. Cannot board starfighters or enclosed vehicles.");
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.YAMRII);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.sameSiteAs(self, Filters.KalFalnl_Cndros))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.or(Filters.starfighter, Filters.enclosed_vehicle)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtTargetModifier(self, Filters.or(Filters.starfighter, Filters.enclosed_vehicle)));
        return modifiers;
    }
}
