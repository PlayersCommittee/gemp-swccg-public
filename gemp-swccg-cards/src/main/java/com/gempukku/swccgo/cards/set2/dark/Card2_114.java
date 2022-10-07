package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.MagneticSuctionTubeAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseMagneticSuctionTubeEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Device
 * Title: Magnetic Suction Tube
 */
public class Card2_114 extends AbstractDevice {
    public Card2_114() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Magnetic_Suction_Tube);
        setLore("'Slurp.'");
        setGameText("Deploy on your Sandcrawler. Once during each of your control phases, may target one character present. Draw destiny. If destiny > character's ability, 'suck up' character (relocate to related interior Sandcrawler site or owner's Used Pile).");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.sandcrawler);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.sandcrawler;
    }

    @Override
    public MagneticSuctionTubeAction getMagneticSuctionTubeAction(SwccgGame game, PhysicalCard self) {
        return new MagneticSuctionTubeAction(self, self, getTargetFilter(self));
    }

    private Filter getTargetFilter(PhysicalCard self) {
        return Filters.and(Filters.character, Filters.presentWith(self.getAttachedTo()));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canTarget(game, self, getTargetFilter(self))
                && GameConditions.canUseDevice(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("'Suck up' character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new UseMagneticSuctionTubeEffect(action, self)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}