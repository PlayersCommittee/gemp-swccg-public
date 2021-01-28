package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Objective
 * Title: Wookiee Slaving Operations / Indentured To The Empire
 */
public class Card601_029_BACK extends AbstractObjective {
    public Card601_029_BACK() {
        super(Side.DARK, 7, "Indentured To The Empire");
        setGameText("While this side up, whenever you 'enslave' a character, opponent must choose to use 2 Force or lose 1 Force. Once per turn, during battle may add or subtract up to X from your just drawn destiny, where X = the number of Kashyyyk locations you control with a slaver. Once per turn, if you just retrieved Force during battle, may take a slaver, starship, or vehicle retrieved into hand.\n" +
                "Flip this card if opponent controls two Kashyyyk battlegrounds.");
        addIcons(Icon.CLOUD_CITY, Icon.BLOCK_8);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        //your Trandoshans are slavers. TODO get rid of snowtrooper
        modifiers.add(new KeywordModifier(self, Filters.or(Filters.species(Species.TRANDOSHAN), Filters.snowtrooper), Keyword.SLAVER));
        //Scum And Villainy may deploy on Slaving Camp Headquarters and
        modifiers.add(new ModifyGameTextModifier(self, Filters.Scum_And_Villainy, ModifyGameTextType.LEGACY__SCUM_AND_VILLAINY__MAY_DEPLOY_ON_SLAVING_CAMP_HEADQUARTERS));
        //may not be canceled while you occupy that site.
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.Scum_And_Villainy, new OccupiesCondition(self.getOwner(), Filters.title("Kashyyyk: Slaving Camp Headquarters"))));
        //While you have < 13 cards in hand, your non-unique slavers are immune to Grimtaash.
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.slaver),
                new CardsInHandFewerThanCondition(self.getOwner(), 13), Title.Grimtaash));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        //Flip this card if your slavers control two Kashyyyk battlegrounds and opponent controls no Kashyyyk locations.

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controls(game, opponent, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Kashyyyk_location, Filters.battleground))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}