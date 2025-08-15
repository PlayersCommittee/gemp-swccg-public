class Card {
    blueprintId = null;
    bareBlueprint = null;
    foil = null;
    alternateImage = null;
    horizontal = null;
    imageUrl = null;
    backSideImageUrl = null;
    testingText = null;
    backSideTestingText = null;

    zone = null;
    cardId = null;
    owner = null;
    attachedCards = null;
    locationIndex = null;
    inverted = false;
    upsideDown = false;
    sideways = false;
    onSide = false;
    inBattle = false;
    attackingInAttack = false;
    defendingInAttack = false;
    inDuelOrLightsaberCombat = false;
    incomplete = null;
    frozen = null;
    suspended = null;
    collapsed = null;
    
    static CardCache = {};
    static CardScale = 350 / 490;
    
    static StripBlueprintId(blueprintId) {
        var stripped = blueprintId;
        
        stripped = stripped.replace("*", "");
        stripped = stripped.replace("^", "");

        return stripped;
    }
    
    static GetFoil(bpid) {
       //At the very smallest, a card id can be 3 characters, i.e. 1_1
        //Thus we start searching at the 2nd character
        return bpid.includes("*", 2); 
    }
    
    static GetAlternateImage(bpid) {
        return bpid.includes("^", 2);
    }

    constructor (blueprintId, testingText, backSideTestingText, zone, cardId, owner, locationIndex, upsideDown, onSide, frozen, suspended, collapsed) {
        this.blueprintId = blueprintId;
        this.bareBlueprint = Card.StripBlueprintId(this.blueprintId);
        
        this.foil = Card.GetFoil(blueprintId);
        this.alternateImage = Card.GetAlternateImage(blueprintId);
        
        if (this.alternateImage) {
            if (fixedImages[this.bareBlueprint + "ai"] != null)
                this.bareBlueprint = this.bareBlueprint + "ai";
        }


        this.testingText = null;
        if (testingText !== undefined) {
            this.testingText = testingText;
        }
        this.backSideTestingText = null;
        if (backSideTestingText !== undefined) {
            this.backSideTestingText = backSideTestingText;
        }

        this.zone = zone;
        this.cardId = cardId;
        this.owner = owner;
        if (locationIndex !== undefined) {
            this.locationIndex = parseInt(locationIndex);
        }
        this.inverted = false;
        if (upsideDown !== undefined) {
            this.upsideDown = upsideDown;
        }
        this.sideways = false;
        if (onSide !== undefined) {
            this.onSide = onSide;
        }
        this.frozen = false;
        if (frozen !== undefined) {
            this.frozen = frozen;
        }
        this.suspended = false;
        if (suspended !== undefined) {
            this.suspended = suspended;
        }
        this.collapsed = false;
        if (collapsed !== undefined) {
            this.collapsed = collapsed;
        }
        this.attachedCards = new Array();
        if (this.bareBlueprint == "rules") {
            this.imageUrl = "https://res.starwarsccg.org/cards/rules.png";
            return;
        }


        this.horizontal = Card.isHorizontal(this.bareBlueprint, this.zone, this.alternateImage);

        if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2" && Card.CardCache[this.bareBlueprint] != null) {
            var cardFromCache = Card.CardCache[this.bareBlueprint];
            this.imageUrl = cardFromCache.imageUrl;
            this.backSideImageUrl = cardFromCache.backSideImageUrl;
            this.incomplete = cardFromCache.incomplete;
        } else {
            this.imageUrl = Card.getImageUrl(this.bareBlueprint);
            this.backSideImageUrl = Card.getBackSideUrl(this.bareBlueprint);
            this.incomplete = Card.isIncomplete(this.bareBlueprint);

            if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2") {
                Card.CardCache[this.bareBlueprint] = {
                    imageUrl:this.imageUrl,
                    backSideImageUrl:this.backSideImageUrl,
                    incomplete:this.incomplete
                };
            }
        }
    }

    flipOverCard() {
        this.bareBlueprint = Card.getBackSideBlueprintId(this.bareBlueprint);
        this.horizontal = Card.isHorizontal(this.bareBlueprint, this.zone, this.alternateImage);
        var tempText = this.testingText;
        this.testingText = this.backSideTestingText;
        this.backSideTestingText = tempText;

        if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2" && Card.CardCache[this.bareBlueprint] != null) {
            var cardFromCache = Card.CardCache[this.bareBlueprint];
            this.imageUrl = cardFromCache.imageUrl;
            this.backSideImageUrl = cardFromCache.backSideImageUrl;
            this.incomplete = cardFromCache.incomplete;
        } else {
            this.imageUrl = Card.getImageUrl(this.bareBlueprint);
            this.backSideImageUrl = Card.getBackSideUrl(this.bareBlueprint);
            this.incomplete = Card.isIncomplete(this.bareBlueprint);

            if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2") {
                Card.CardCache[this.bareBlueprint] = {
                    imageUrl:this.imageUrl,
                    backSideImageUrl:this.backSideImageUrl,
                    incomplete:this.incomplete
                };
            }
        }
        $(".card:cardId(" + this.cardId + ") > img").attr('src', this.imageUrl);
    }

    turnCardOver(tempBlueprintId) {
        this.bareBlueprint = tempBlueprintId;
        this.horizontal = Card.isHorizontal(this.bareBlueprint, this.zone, this.alternateImage);
        var tempText = this.testingText;
        this.testingText = this.backSideTestingText;
        this.backSideTestingText = tempText;

        if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2" && Card.CardCache[this.bareBlueprint] != null) {
            var cardFromCache = Card.CardCache[this.bareBlueprint];
            this.imageUrl = cardFromCache.imageUrl;
            this.backSideImageUrl = cardFromCache.backSideImageUrl;
            this.incomplete = cardFromCache.incomplete;
        } else {
            this.imageUrl = Card.getImageUrl(this.bareBlueprint);
            this.backSideImageUrl = Card.getBackSideUrl(this.bareBlueprint);
            this.incomplete = Card.isIncomplete(this.bareBlueprint);

            if (this.bareBlueprint != "-1_1" && this.bareBlueprint != "-1_2") {
                Card.CardCache[this.bareBlueprint] = {
                    imageUrl:this.imageUrl,
                    backSideImageUrl:this.backSideImageUrl,
                    incomplete:this.incomplete
                };
            }
        }
        $(".card:cardId(" + this.cardId + ") > img").attr('src', this.imageUrl);
    }

    isFoil() {
        return this.foil;
    }

    isPack() {
        return packBlueprints[this.blueprintId] != null;
    }
    
    effectivelyHorizontal() {
        return (this.zone.startsWith("TOP_OF") &&
            Card.isBlueprintHorizontal(this.blueprintId, this.alternateImage));
    }

    static isZoneNeverHorizontal(zone) {
        if (zone == "RESERVE_DECK" || zone == "FORCE_PILE"
                || zone == "USED_PILE" || zone == "LOST_PILE"
                || zone == "TOP_OF_RESERVE_DECK" || zone == "TOP_OF_FORCE_PILE"
                || zone == "TOP_OF_USED_PILE" || zone == "TOP_OF_LOST_PILE"
                || zone == "STACKED" || zone == "STACKED_FACE_DOWN") {
            return true;
        }

        return false;
    }

    static isBlueprintHorizontal(blueprintId, alternateImage) {
        var separator = blueprintId.indexOf("_");
        var setNo = parseInt(blueprintId.substr(0, separator));
        var cardNo = parseInt(blueprintId.substr(separator + 1));

        if (alternateImage) {
            // AIs that are horizontal and the non-AI is not
            if (blueprintId == "204_47ai"
                    || blueprintId == "200_41ai"
                    || blueprintId == "206_6ai") {
                return true;
            }
        }

        if (setNo == 0) {
           return (cardNo == 3 ||
                   cardNo == 6 ||
                   cardNo == 33 ||
                   cardNo == 43 ||
                   cardNo == 47);
        }
        if (setNo == 1) {
           return ((cardNo >= 123 && cardNo <= 125) ||
                   (cardNo >= 128 && cardNo <= 134) ||
                   (cardNo >= 136 && cardNo <= 139) ||
                   (cardNo >= 283 && cardNo <= 287) ||
                   (cardNo >= 290 && cardNo <= 295) ||
                   (cardNo >= 297 && cardNo <= 298));
        }
        if (setNo == 2) {
           return ((cardNo >= 62 && cardNo <= 63) ||
                   (cardNo >= 66 && cardNo <= 68) ||
                   (cardNo >= 144 && cardNo <= 145) ||
                   (cardNo >= 149 && cardNo <= 150));
        }
        if (setNo == 3) {
           return ((cardNo >= 56 && cardNo <= 63) ||
                   (cardNo >= 144 && cardNo <= 150));
        }
        if (setNo == 4) {
           return (cardNo == 83 ||
                   (cardNo >= 85 && cardNo <= 89) ||
                   (cardNo >= 157 && cardNo <= 163) ||
                   cardNo == 165 ||
                   cardNo == 167);
        }
        if (setNo == 5) {
           return ((cardNo >= 78 && cardNo <= 84) ||
                   (cardNo >= 166 && cardNo <= 173));
        }
        if (setNo == 6) {
           return ((cardNo >= 81 && cardNo <= 82) ||
                   (cardNo >= 85 && cardNo <= 86) ||
                   (cardNo >= 162 && cardNo <= 167) ||
                   (cardNo >= 169 && cardNo <= 171));
        }
        if (setNo == 7) {
           return ((cardNo >= 111 && cardNo <= 115) ||
                   (cardNo >= 118 && cardNo <= 122) ||
                   (cardNo >= 125 && cardNo <= 134) ||
                   (cardNo >= 269 && cardNo <= 274) ||
                   (cardNo >= 276 && cardNo <= 278) ||
                   (cardNo >= 280 && cardNo <= 282) ||
                   (cardNo >= 284 && cardNo <= 285) ||
                   (cardNo >= 288 && cardNo <= 294));
        }
        if (setNo == 8) {
           return ((cardNo >= 69 && cardNo <= 77) ||
                   (cardNo >= 158 && cardNo <= 166));
        }
        if (setNo == 9) {
           return ((cardNo >= 57 && cardNo <= 58) ||
                   (cardNo == 145) ||
                   (cardNo == 147) ||
                   (cardNo == 157));
        }
        if (setNo == 11) {
           return ((cardNo >= 42 && cardNo <= 46) ||
                   (cardNo >= 92 && cardNo <= 94));
        }
        if (setNo == 12) {
           return ((cardNo >= 74 && cardNo <= 76) ||
                   (cardNo >= 79 && cardNo <= 83) ||
                   (cardNo >= 85 && cardNo <= 87) ||
                   (cardNo == 164) ||
                   (cardNo >= 166 && cardNo <= 167) ||
                   (cardNo >= 170 && cardNo <= 174) ||
                   (cardNo >= 176 && cardNo <= 178));
        }
        if (setNo == 13) {
           return ((cardNo >= 31 && cardNo <= 32) ||
                   (cardNo == 57) ||
                   (cardNo >= 75 && cardNo <= 77));
        }
        if (setNo == 14) {
           return ((cardNo >= 48 && cardNo <= 51) ||
                   (cardNo >= 111 && cardNo <= 112));
        }
        if (setNo == 101) {
           return (cardNo == 1 ||
                   cardNo == 4);
        }
        if (setNo == 104) {
           return (cardNo == 4);
        }
        if (setNo == 106) {
           return (cardNo == 8 ||
                   cardNo == 18);
        }
        if (setNo == 112) {
           return (cardNo == 2 ||
                   cardNo == 9 ||
                   cardNo == 12 ||
                   cardNo == 20);
        }
        if (setNo == 200) {
            return (cardNo == 57 ||
                    cardNo == 126);
        }
        if (setNo == 201) {
            return (cardNo == 17 ||
                    cardNo == 38);
        }
        if (setNo == 203) {
            return (cardNo == 32 ||
                    cardNo == 34);
        }
        if (setNo == 204) {
            return (cardNo == 25 ||
                    (cardNo >= 27 && cardNo <= 31) ||
                    (cardNo >= 52 && cardNo <= 54));
        }
        if (setNo == 205) {
            return (cardNo == 6);
        }
        if (setNo == 207) {
            return (cardNo == 16);
        }
        if (setNo == 208) {
            return (cardNo >= 23 && cardNo <= 24) ||
                    (cardNo >= 48 && cardNo <= 49) ||
                    (cardNo >= 52 && cardNo <= 56);
        }
        if (setNo == 209) {
            return (cardNo >= 24 && cardNo <= 28) ||
                    (cardNo >= 49 && cardNo <= 51);
        }
        if (setNo == 210) {
            return (cardNo == 15 || cardNo == 1);

        }
        if (setNo == 211) {
            return (cardNo == 17) ||
                   (cardNo == 18) ||
                   (cardNo == 20) ||
                   (cardNo == 21) ||
                   (cardNo == 22) ||
                   (cardNo == 27) ||
                   (cardNo >= 38 && cardNo <= 42) ||
                   (cardNo >= 44 && cardNo <= 47);
        }
        if (setNo == 213) {
            return (cardNo >= 23 && cardNo <= 29) ||
                   (cardNo == 56);
        }
        if(setNo == 214) {
            return (cardNo == 4) ||
                   (cardNo == 19);
        }
        if(setNo == 215){
            return (cardNo >= 6 && cardNo <=9) ||
                   (cardNo == 13) ||
                   (cardNo == 14);
        }if(setNo == 216){
            return (cardNo == 4) ||
                   (cardNo == 9) ||
                   (cardNo >= 14 && cardNo <=17) ||
                   (cardNo == 25) ||
                   (cardNo == 26) ||
                   (cardNo >= 32 && cardNo <=33) ||
                   (cardNo == 43);
        }if(setNo == 217){
            return (cardNo == 12) ||
                   (cardNo == 15) ||
                   (cardNo == 27) ||
                   (cardNo == 34) ||
                   (cardNo == 39) ||
                   (cardNo == 44) ||
                   (cardNo == 47) ||
                   (cardNo == 48);
        }if(setNo == 218) {
            return (cardNo == 17) ||
                   (cardNo == 18) ||
                   (cardNo == 22) ||
                   (cardNo == 30);
        }if(setNo == 219) {
            return (cardNo >= 11 && cardNo <=14) ||
                   (cardNo == 31) ||
                   (cardNo >= 39 && cardNo <=42);
        }if(setNo == 220) {
            return (cardNo == 8);
        }if(setNo == 221){
            return (cardNo == 11) ||
                   (cardNo == 23) ||
                   (cardNo == 33) ||
                   (cardNo == 34) ||
                   (cardNo == 35) ||
                   (cardNo == 36) ||
                   (cardNo == 37) ||
                   (cardNo == 48) ||
                   (cardNo == 53) ||
                   (cardNo == 54) ||
                   (cardNo == 63) ||
                   (cardNo == 74) ||
                   (cardNo == 75);
        }if(setNo == 222){
            return (cardNo == 9) ||
                   (cardNo == 22);
        }if(setNo == 223){
            return (cardNo == 36) ||
                   (cardNo == 38) ||
                   (cardNo == 48);
        }if(setNo == 224){
            return (cardNo == 13) ||
                   (cardNo == 22);        
        }if(setNo == 225){
            return (cardNo == 16) ||
                   (cardNo == 17) ||
                   (cardNo == 28) ||
                   (cardNo == 29) ||
                   (cardNo == 30) ||
                   (cardNo == 40) ||
                   (cardNo == 41) ||
                   (cardNo == 44);        
        }if (setNo == 501) {
            return (cardNo == 6) ||
                   (cardNo == 7) ||
                   (cardNo == 8) ||
                   (cardNo == 13) ||
                   (cardNo == 21) ||
                   (cardNo == 24) ||
                   (cardNo == 29) ||
                   (cardNo == 36) ||
                   (cardNo == 38) ||
                   (cardNo == 39) ||
                   (cardNo == 164) ||
                   (cardNo == 171);
        }
        if (setNo == 601) {
             return (cardNo == 14) ||
                    (cardNo == 15) ||
                    (cardNo == 16) ||
                    (cardNo == 60) ||
                    (cardNo == 118) ||
                    (cardNo == 119) ||
                    (cardNo == 120) ||
                    (cardNo == 128) ||
                    (cardNo == 150) ||
                    (cardNo == 202) ||
                    (cardNo == 245) ||
                    (cardNo == 256) ||
                    (cardNo == 257) ||
                    (cardNo == 270) ||
                    (cardNo == 271) ||
                    (cardNo == 280) ||
                    (cardNo == 281) ||
                    (cardNo == 282) ||
                    (cardNo == 283) ||
                    (cardNo == 284);
       }

        return false;
    }

    static isHorizontal(blueprintId, zone, alternateImage) {

        // For some zones, never show the card as horizontal
        if (Card.isZoneNeverHorizontal(zone)) {
            return false;
        }

        return Card.isBlueprintHorizontal(blueprintId, alternateImage);
    }

    static isIncomplete(blueprintId) {
        var separator = blueprintId.indexOf("_");
        var setNo = parseInt(blueprintId.substr(0, separator));
        var cardNo = parseInt(blueprintId.substr(separator + 1));
        
        if (setNo >= 400 && setNo < 600) {
            return true;
        }

        return false;
    }

    static getImageUrl(blueprintId) {
        if (fixedImages[blueprintId] != null)
            return fixedImages[blueprintId];

        if (packBlueprints[blueprintId] != null)
            return packBlueprints[blueprintId];

        return null;
    }

    static getBackSideBlueprintId(blueprintId) {
        if (blueprintId.endsWith("_BACK")) {
            return blueprintId.substring(0, blueprintId.length - 5);
        }
        var backSideUrl = Card.getImageUrl(blueprintId.concat("_BACK"));
        if (backSideUrl != null) {
            return blueprintId.concat("_BACK");
        }
        var genericBackUrl = Card.getImageUrl(blueprintId);
        if (genericBackUrl != null) {
            if (Card.getImageUrl(blueprintId).includes("-Dark/"))
                    return "-1_2";
                else
                    return "-1_1";
        }
    }

    static getBackSideUrl(blueprintId) {
        return Card.getImageUrl(Card.getBackSideBlueprintId(blueprintId));
    }

    getHeightForWidth(width) {
        if (this.horizontal)
            return Math.floor(width * Card.CardScale);
        else
            return Math.floor(width / Card.CardScale);
    }

    getHeightForColumnWidth(columnWidth) {
        if (this.horizontal)
            return columnWidth;
        else
            return Math.floor(columnWidth / Card.CardScale);
    }

    getWidthForHeight(height) {
        if (this.horizontal)
            return Math.floor(height / Card.CardScale);
        else
            return Math.floor(height * Card.CardScale);
    }

    getWidthForMaxDimension(maxDimension) {
        if (this.horizontal)
            return maxDimension;
        else
            return Math.floor(maxDimension * Card.CardScale);
    }

    getHeightForMaxDimension(maxDimension) {
        if (this.horizontal)
            return Math.floor(maxDimension * Card.CardScale);
        else
            return maxDimension;
    }
    
    static CreateCardDiv(image, testingText, text, foil, tokens, noBorder, incomplete) {
        var cardDiv = $("<div class='card'><img src='" + image + "' width='100%' height='100%'>" + ((text != null) ? text : "") + "</div>");

        if (incomplete) {
            var incompleteDiv = $("<div class='incompleteOverlay'><img src='https://res.starwarsccg.org/gemp/incompleteCard.png' width='100%' height='100%'></div>");
            cardDiv.append(incompleteDiv);
        }

        if (foil) {
            var foilDiv = $("<div class='foilOverlay'><img src='https://res.starwarsccg.org/cards/holo.jpg' width='100%' height='100%'></div>");
            cardDiv.append(foilDiv);
        }

        var frozenDiv = $("<div class='frozenOverlay'><img src='https://res.starwarsccg.org/cards/carbonite.gif' width='100%' height='100%'></div>");
        cardDiv.append(frozenDiv);

        var suspendedDiv = $("<div class='suspendedOverlay'><img src='https://res.starwarsccg.org/gemp/gray.jpg' width='100%' height='100%'></div>");
        cardDiv.append(suspendedDiv);

        var collapsedDiv = $("<div class='collapsedOverlay'><img src='https://res.starwarsccg.org/gemp/collapsed.jpg' width='100%' height='100%'></div>");
        cardDiv.append(collapsedDiv);

        if (tokens === undefined || tokens) {
            var overlayDiv = $("<div class='tokenOverlay'></div>");
            cardDiv.append(overlayDiv);
        }

        if (testingText != null) {
            var testingTextDiv = $("<div class='testingTextOverlay'></div>");
            var firstPipe = testingText.indexOf('|');
            if (firstPipe !== -1) {
                testingTextDiv.html(testingText.substring(0, firstPipe));
            }
            else {
                testingTextDiv.html(testingText);
            }
            cardDiv.append(testingTextDiv);
        }

        var borderDiv = $("<div class='borderOverlay'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
        if (noBorder)
            borderDiv.addClass("noBorder");
        cardDiv.append(borderDiv);
        
        var cardPileCountDiv = $("<div class='cardPileCount'></div>");
        cardDiv.append(cardPileCountDiv);

        return cardDiv;
    }

    static CreateFullCardDiv(image, testingText, foil, horizontal, noBorder) {
        if (horizontal) {
            var cardDiv = $("<div style='position: relative;width:497px;height:357px;'></div>");
            cardDiv.append("<div class='fullcard' style='position:absolute'><img src='" + image + "' width='497' height='357'></div>");

            if (foil) {
                var foilDiv = $("<div class='foilOverlay' style='position:absolute;width:497px;height:357px'><img src='https://res.starwarsccg.org/cards/holo.jpg' width='100%' height='100%'></div>");
                cardDiv.append(foilDiv);
            }

            if (testingText != null) {
                var testingTextDiv = $("<div class='testingTextOverlay' style='position:absolute;left:25px;width:449px;top:18px;height:321px'></div>");
                testingTextDiv.html(testingText.replace(/\|/g, "<br/>"));
                cardDiv.append(testingTextDiv);
            }

            if (noBorder) {
                var borderDiv = $("<div class='borderOverlay,noBorder' style='position:absolute;width:497px;height:357px;border-width:0px'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
                cardDiv.append(borderDiv);
            } else {
                var borderDiv = $("<div class='borderOverlay' style='position:absolute;width:465px;height:325px;border-width:16px'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
                cardDiv.append(borderDiv);
            }

        } else {
            var cardDiv = $("<div style='position: relative;width:357px;height:497px;'></div>");
            cardDiv.append("<div class='fullcard' style='position:absolute'><img src='" + image + "' width='357' height='497'></div>");

            if (foil) {
                var foilDiv = $("<div class='foilOverlay' style='position:absolute;width:357px;height:497px'><img src='https://res.starwarsccg.org/cards/holo.jpg' width='100%' height='100%'></div>");
                cardDiv.append(foilDiv);
            }

            if (testingText != null) {
                var testingTextDiv = $("<div class='testingTextOverlay' style='position:absolute;left:36px;width:285px;top:50px;height:398px'></div>");
                testingTextDiv.html(testingText.replace(/\|/g, "<br/>"));
                cardDiv.append(testingTextDiv);
            }

            if (noBorder) {
                var borderDiv = $("<div class='borderOverlay,noBorder' style='position:absolute;width:357px;height:497px;border-width:0px'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
                cardDiv.append(borderDiv);
            } else {
                var borderDiv = $("<div class='borderOverlay' style='position:absolute;width:325px;height:465px;border-width:16px'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
                cardDiv.append(borderDiv);
            }
        }

        return cardDiv;
    }

    static CreateSimpleCardDiv(image, testingText, foil, incomplete, borderWidth) {
        var cardDiv = $("<div class='card'><img src='" + image + "' width='100%' height='100%'></div>");

        if (incomplete) {
            var incompleteDiv = $("<div class='incompleteOverlay' style='position:absolute;left:0px;top:0px;width:100%;height:100%'><img src='https://res.starwarsccg.org/gemp/incompleteCard.png' width='100%' height='100%'></div>");
            cardDiv.append(incompleteDiv);
        }

        if (foil) {
            var foilDiv = $("<div class='foilOverlay' style='position:absolute;left:0px;top:0px;width:100%;height:100%'><img src='https://res.starwarsccg.org/gemp/holo.jpg' width='100%' height='100%'></div>");
            cardDiv.append(foilDiv);
        }

        if (testingText != null) {
            var testingTextDiv = $("<div class='testingTextOverlay' style='position:absolute;left:5%;top:5%;width:90%;height:90%'></div>");
            testingTextDiv.html(testingText.replace(/\|/g, "<br/>"));
            cardDiv.append(testingTextDiv);
        }

        var borderDiv = $("<div class='borderOverlay' style='position:absolute;left:0px;top:0px;width:100%;height:100%;border-width:" + borderWidth + "px;box-sizing:border-box'><img class='actionArea' src='https://res.starwarsccg.org/gemp/pixel.png' width='100%' height='100%'></div>");
        cardDiv.append(borderDiv);

        return cardDiv;
    }
}
