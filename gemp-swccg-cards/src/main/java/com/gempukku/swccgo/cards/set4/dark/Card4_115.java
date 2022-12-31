package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Awwww, Cannot Get Your Ship Out
 */
public class Card4_115 extends AbstractNormalEffect {
    public Card4_115() {
        super(Side.DARK, 4, null, Title.Awwww_Cannot_Get_Your_Ship_Out, Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("'Listen, friend, we didn't mean to land in that puddle, and if we could get our ship out, we would, but we can't so why don't you just...'");
        setGameText("Deploy on a landed starship (not at a docking bay). Starship may not move. OR Deploy on a starship or vehicle on Dagobah. Starship or vehicle may not move. At the start of you next control phase, starship or vehicle 'sinks' to the Used Pile.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.ATTACHED, "Deploy on a landed starship"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.ATTACHED, "Deploy on starship or vehicle on Dagobah"));
        return playCardOptions;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_1)
            return Filters.and(Filters.landed, Filters.starship, Filters.not(Filters.at(Filters.docking_bay)));
        else if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_2)
            return Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.on(Title.Dagobah));
        else
            return Filters.none;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        if (self.getPlayCardOptionId() == PlayCardOptionId.PLAY_CARD_OPTION_1)
            return Filters.starship;
        else
            return Filters.or(Filters.starship, Filters.vehicle);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveModifier(self, Filters.hasAttached(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isPlayCardOption(game, self, PlayCardOptionId.PLAY_CARD_OPTION_2)) {
            PhysicalCard starshipOrVehicle = self.getAttachedTo();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(starshipOrVehicle) + " 'sink'");
            action.setActionMsg("Make "+ GameUtils.getCardLink(starshipOrVehicle) + " 'sink' to Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, starshipOrVehicle));
            return Collections.singletonList(action);
        }
        return null;
    }
}