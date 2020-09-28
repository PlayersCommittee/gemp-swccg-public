package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: Slimy Piece Of Worm-Ridden Filth!
 */
public class Card501_028 extends AbstractNormalEffect {
    public Card501_028() {
        super(Side.LIGHT, 7, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Slimy Piece Of Worm-Ridden Filth!", Uniqueness.UNIQUE);
        setLore("'Aacccck!'");
        setGameText("Deploy on table; may immediately take into hand a [Premium Expansion] Effect; reshuffle. Your [Premium Expansion] Effects are immune to Alter. [Jabba's Palace Expansion] Leia may deploy as an escorted captive of any warrior at Audience Chamber. Your aliens are immune to Sniper and Dr. Evazan. Immune to Alter.");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_13);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("Slimy Piece Of Worm-Ridden Filth!");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self.getOwner()), Icon.PREMIUM, CardType.EFFECT), Title.Alter));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self.getOwner()), Filters.alien), Title.Sniper));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self.getOwner()), Filters.alien), Title.Dr_Evazan));
        //Broken Link (JP leia still needs to be coded)
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.Leia, Icon.JABBAS_PALACE), ModifyGameTextType.LEIA_JABBAS_PALACE__DEPLOY_AS_ESCORTED_CAPTIVE_TO_NON_BOUNTY_HUNTERS));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SLIMY_PIECE_OF_WORM_RIDDEN_FILTH__UPLOAD_PREMIUM_EFFECT;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take effect into hand from Reserve Deck");
            action.setActionMsg("Take effect into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Icon.PREMIUM, CardType.EFFECT), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
