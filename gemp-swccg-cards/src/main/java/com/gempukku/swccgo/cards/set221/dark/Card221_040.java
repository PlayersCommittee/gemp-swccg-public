package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Vader's Malediction
 */
public class Card221_040 extends AbstractNormalEffect {
    public Card221_040() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Vaders_Malediction, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("'... And together, we can rule the galaxy as father and son!'");
        setGameText("If Vader is your apprentice, deploy on table. Vader's game text may not be canceled. While Vader alone, armed with a [Death Star II] lightsaber, and present at a site, your Force drains there are +1. If you just drew Vader for destiny, may take him into hand. [Immune to Alter.]");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        // If Vader is your 'apprentice'
        PhysicalCard rots = Filters.findFirstActive(game, self, Filters.Revenge_Of_The_Sith);
        if (rots != null
                && GameConditions.cardHasWhileInPlayDataSet(rots)
                && rots.getWhileInPlayData().getTextValue() != null) {
            return "Vader".equals(rots.getWhileInPlayData().getTextValue());
        }
        return false;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, Filters.Vader));
        Filter dsIILightsaber = Filters.and(Icon.DEATH_STAR_II, Filters.lightsaber);
        Filter vaderArmedWithDsIILightsaberAloneAndPresentAtASite = Filters.and(Filters.Vader, Filters.alone, Filters.armedWith(dsIILightsaber), Filters.presentAt(Filters.site));
        modifiers.add(new ForceDrainModifier(self, Filters.sameSiteAs(self, vaderArmedWithDsIILightsaberAloneAndPresentAtASite), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.Vader)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take into hand");

            // Perform result(s)
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}