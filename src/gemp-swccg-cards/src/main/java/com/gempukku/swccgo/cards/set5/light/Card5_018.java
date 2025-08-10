package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Beldon's Eye
 */
public class Card5_018 extends AbstractNormalEffect {
    public Card5_018() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, Title.Beldons_Eye, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Symbol for the Cloud City Miner's Guild (not affiliated with the Galactic Miner's Guild). Named after the beldons, giant creatures who generate Tibanna gas.");
        setGameText("Deploy on Cloud City. Your Tibanna Gas Miners deploy free to Cloud City sites and double the Force they activate.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Bespin_Cloud_City;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourTibannaGasMiners = Filters.and(Filters.your(self), Filters.Tibanna_Gas_Miner);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, yourTibannaGasMiners, Filters.Cloud_City_site));
        modifiers.add(new ModifyGameTextModifier(self, yourTibannaGasMiners, ModifyGameTextType.TIBANNA_GAS_MINER__DOUBLE_FORCE_ACTIVATED));
        return modifiers;
    }
}