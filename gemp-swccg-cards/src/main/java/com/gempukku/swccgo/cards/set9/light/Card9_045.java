package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CompleteJediTestEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetLandspeedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Twilight Is Upon Me
 */
public class Card9_045 extends AbstractNormalEffect {
    public Card9_045() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Way Of Things", Uniqueness.UNIQUE);
        setLore("'When nine hundred years old you reach, look as good you will not. Hmmm?'");
        setGameText("Deploy on table. If Yoda is on Dagobah and Jedi Test #3 is present with target apprentice, it is completed. Also, you may not play Surprise Assault. Each apprentice on Dagobah is landspeed = 2 and may not be targeted by Set For Stun. (Immune To Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            PhysicalCard jediTest3 = Filters.findFirstActive(game, self, Filters.and(Filters.uncompleted_Jedi_Test, Filters.Jedi_Test_3));
            if (jediTest3 != null
                    && Filters.presentWith(self, Filters.apprenticeTargetedByJediTest(jediTest3)).accepts(game, jediTest3)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Yoda, Filters.on(Title.Dagobah)))) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Complete " + GameUtils.getFullName(jediTest3));
                action.setActionMsg("Complete " + GameUtils.getCardLink(jediTest3));
                // Perform result(s)
                action.appendEffect(
                        new CompleteJediTestEffect(action, jediTest3));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter apprenticeOnDagobah = Filters.and(Filters.apprentice, Filters.on(Title.Dagobah));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.Surprise_Assault, playerId));
        modifiers.add(new ResetLandspeedModifier(self, apprenticeOnDagobah, 2));
        modifiers.add(new MayNotBeTargetedByModifier(self, apprenticeOnDagobah, Filters.Set_For_Stun));
        return modifiers;
    }
}