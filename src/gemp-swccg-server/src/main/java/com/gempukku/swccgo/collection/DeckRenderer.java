package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.*;

public class DeckRenderer {

    private final SwccgCardBlueprintLibrary _bpLibrary;
    private final SwccgoFormatLibrary _formatLibrary;
    private final SortAndFilterCards _cardFilter;

    public DeckRenderer(SwccgCardBlueprintLibrary library, SwccgoFormatLibrary formatLibrary, SortAndFilterCards cardfilter) {
        _bpLibrary = library;
        _cardFilter = cardfilter;
        _formatLibrary = formatLibrary;
    }

    public String RenderDeck(SwccgDeck deck, String author) throws CardNotFoundException {
        return AddDeckReadoutHeaderAndFooter(convertDeckToHTMLFragment(deck, "", author));
    }

    public String RenderDeckPair(SwccgDeck lsDeck, SwccgDeck dsDeck, String author) throws CardNotFoundException {
        return AddDeckReadoutHeaderAndFooter(
                convertDeckToHTMLFragment(lsDeck, "", author),
                convertDeckToHTMLFragment(dsDeck, "", author));
    }

    public String AddDeckReadoutHeaderAndFooter(String fragment) {
        return AddDeckReadoutHeaderAndFooter(Collections.singletonList(fragment));
    }

    public String AddDeckReadoutHeaderAndFooter(String...fragments) {
        return AddDeckReadoutHeaderAndFooter(Arrays.stream(fragments).toList());
    }

    public String AddDeckReadoutHeaderAndFooter(List<String> fragments) {
        String preamble = """
<html>
    <style>
        body {
            margin:50;
        }
        
        .tooltip {
          border-bottom: 1px dotted black; /* If you want dots under the hoverable text */
          color:#0000FF;
        }
        
        .tooltip span, .tooltip title {
            display:none;
        }
        .tooltip:hover span:not(.click-disabled),.tooltip:active span:not(.click-disabled) {
            display:block;
            position:fixed;
            overflow:hidden;
            background-color: #FAEBD7;
            width:auto;
            z-index:9999;
            top:20%;
            left:350px;
        }
        /* This prevents tooltip images from automatically shrinking if they are near the window edge.*/
        .tooltip span > img {
            max-width: 700px;
            max-height: 700px;
        }
                        
    </style>
    <body>""";
        String divider = "<br/><hr><br/>";
        String postamble = """
    <script src="/gemp-swccg/js/gemp-016/cards/CardImages.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('.tooltip').forEach(function(el) {
                var bpid = el.getAttribute('data-bpid');
                var img = el.querySelector('.ttimage');
                if (img && fixedImages[bpid]) {
                    img.src = fixedImages[bpid];
                    img.onload = function() {
                        if (this.naturalWidth >= this.naturalHeight) {
                            this.style.width = '700px';
                            this.style.height = 'auto';
                        } else {
                            this.style.height = '700px';
                            this.style.width = 'auto';
                        }
                    };
                }
            });
        });
    </script>
    </body></html>""";
        return preamble + String.join(divider, fragments) + postamble;

    }

    public String convertDeckToHTMLFragment(SwccgDeck deck, String event, String author) throws CardNotFoundException {

        if (deck == null)
            return null;

        StringBuilder result = new StringBuilder();
        result.append("<div>")
                .append("<h1>").append(StringEscapeUtils.escapeHtml3(deck.getDeckName())).append("</h1>")
                .append("<h2>").append(StringEscapeUtils.escapeHtml3(event)).append("</h2>")
                .append("<h2>").append(deck.getSide(_bpLibrary).getHumanReadable()).append(" Side</h2>");

        if(author != null) {
            result.append("<h2>Author: ").append(StringEscapeUtils.escapeHtml3(author)).append("</h2>");
        }

        DefaultCardCollection deckCards = new DefaultCardCollection();
        for (String card : deck.getCards()) {
            deckCards.addItem(_bpLibrary.getBaseBlueprintId(card), 1);
        }

        DefaultCardCollection sideDeck = new DefaultCardCollection();
        for (String card : deck.getCardsOutsideDeck()) {
            sideDeck.addItem(_bpLibrary.getBaseBlueprintId(card), 1);
        }

        result.append("<h3>Deck:</h3>");

        CardCategory category = null;
        for (CardCollection.Item item : _cardFilter.process("sort:cardCategory,name", deckCards.getAll().values(), _bpLibrary, _formatLibrary, null)) {
            if (!_bpLibrary.getSwccgoCardBlueprint(item.getBlueprintId()).getCardCategory().equals(category)) {
                category = _bpLibrary.getSwccgoCardBlueprint(item.getBlueprintId()).getCardCategory();
                result.append("<h4/>").append(category.getHumanReadable().toUpperCase()).append(":<h4/>");
            }

            result.append(item.getCount()).append("x ").append(generateCardTooltip(item)).append("<br/>");
        }

        if(!sideDeck.getAll().isEmpty()) {
            result.append("<br/><h3>Outside Deck:</h3><br/>");
            category = null;
            for (CardCollection.Item item : _cardFilter.process("sort:cardCategory,name", sideDeck.getAll().values(), _bpLibrary, _formatLibrary, null)) {
                if (!_bpLibrary.getSwccgoCardBlueprint(item.getBlueprintId()).getCardCategory().equals(category)) {
                    category = _bpLibrary.getSwccgoCardBlueprint(item.getBlueprintId()).getCardCategory();
                    result.append("<h4/>").append(category.getHumanReadable().toUpperCase()).append(":<h4/>");
                }

                result.append(item.getCount()).append("x ").append(generateCardTooltip(item)).append("<br/>");
            }
        }

        result.append("</div>");

        return result.toString();
    }

    private String generateCardTooltip(CardCollection.Item item) throws CardNotFoundException {
        return generateCardTooltip(_bpLibrary.getSwccgoCardBlueprint(item.getBlueprintId()), item.getBlueprintId());
    }

    private String generateCardTooltip(String bpid) throws CardNotFoundException {
        return generateCardTooltip(_bpLibrary.getSwccgoCardBlueprint(bpid), bpid);
    }

    private String generateCardTooltip(SwccgCardBlueprint bp, String bpid) {
        String displayName = GameUtils.getFullName(bp);
        return "<span class=\"tooltip\" data-bpid=\"" + bpid + "\">" + displayName
                + "<span><img class=\"ttimage\" src=\"\" ></span></span>";
    }
}
