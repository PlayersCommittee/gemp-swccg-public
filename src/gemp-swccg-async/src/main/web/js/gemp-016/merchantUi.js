var GempSwccgMerchantUI = Class.extend({
    comm:null,

    cardsDiv:null,
    cardsGroup:null,

    filterDiv:null,
    cardFilter:null,

    pocketDiv:null,
    hideMerchantDiv:null,
    countDiv:null,

    cardInfoDialog: null,
    questionDialog:null,

    currencyCount:null,
    ownedCompareSelect:'GREATER_THAN_OR_EQUAL_TO',
    ownedMin:0,
    hideMerchant:false,

    init:function (cardListElem, cardFilterElem) {
        var that = this;

        this.comm = new GempSwccgCommunication("/gemp-swccg-server", that.processError);

        this.cardFilter = new CardFilter(cardFilterElem, cardFilterElem,
                function (filter, start, count, callback) {
                    that.comm.getMerchant(filter, that.ownedCompareSelect, that.ownedMin, start, count, callback);
                },
                function (rootElem) {
                    that.clearList(rootElem);
                },
                function (elem, type, blueprintId, testingText, backSideTestingText, count) {
                    that.addCardToList(elem, type, blueprintId, testingText, backSideTestingText, count);
                },
                function () {
                    that.finishList();
                });

        this.cardsDiv = cardListElem;
        this.cardsGroup = new NormalCardGroup(this.cardsDiv, function (card) {
            return true;
        });

        this.filterDiv = cardFilterElem;

        this.pocketDiv = $("<div class='pocket'></div>");

        this.hideMerchantDiv = $("<div class='hideMerchant'><label for='hideMerchantCheck'>Hide merchant</label><input type='checkbox' id='hideMerchantCheck' value='hideMerchant'/></div>");

        this.countDiv = $("<div class='countDiv'>Owned <select id='ownedCompareSelect'><option value='LESS_THAN_OR_EQUAL_TO'><=</option><option value='EQUALS'>=</option><option value='GREATER_THAN_OR_EQUAL_TO' selected>>=</option></select> <select id='ownedMin'><option value='0'>0</option><option value='1'>1</option><option value='2'>2</option><option value='3'>3</option><option value='4'>4</option><option value='5'>5</option><option value='6'>6</option><option value='7'>7</option><option value='8'>8</option><option value='9'>9</option><option value='10'>10</option></select></div>");

        this.filterDiv.append(this.pocketDiv);
        this.filterDiv.append(this.hideMerchantDiv);
        this.filterDiv.append(this.countDiv);

        $("#ownedMin").change(
                function () {
                    that.ownedMin = $("#ownedMin option:selected").prop("value");
                    that.cardFilter.getCollection();
                });

        $("#ownedCompareSelect").change(
                function () {
                    that.ownedCompareSelect = $("#ownedCompareSelect option:selected").prop("value");
                    that.cardFilter.getCollection();
                });

        $("#hideMerchantCheck").change(
                function () {
                    that.hideMerchant = $("#hideMerchantCheck").prop("checked");
                    that.cardFilter.getCollection();
                });

        this.cardInfoDialog = new CardInfoDialog(window.innerWidth, window.innerHeight);

        this.questionDialog = $("<div></div>")
                .dialog({
            autoOpen:false,
            closeOnEscape:true,
            resizable:false,
            modal:true,
            title:"Merchant operation"
        });

        $("body").click(
                function (event) {
                    return that.clickCardFunction(event);
                });
        $("body")[0].addEventListener("contextmenu",
            function (event) {
                if(!that.clickCardFunction(event))
                {
                    event.preventDefault();
                    return false;
                }
                return true;
            });
        $("body").mousedown(
                function (event) {
                    return that.dragStartCardFunction(event);
                });
        $("body").mouseup(
                function (event) {
                    return that.dragStopCardFunction(event);
                });

        this.cardFilter.getCollection();
    },

    dragCardData:null,
    dragStartX:null,
    dragStartY:null,
    successfulDrag:null,

    dragStartCardFunction:function (event) {
        this.successfulDrag = false;
        var tar = $(event.target);
        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (event.which == 1) {
                this.dragCardData = selectedCardElem.data("card");
                this.dragStartX = event.clientX;
                this.dragStartY = event.clientY;
                return false;
            }
        }
        return true;
    },

    dragStopCardFunction:function (event) {
        if (this.dragCardData != null) {
            if (this.dragStartY - event.clientY >= 20) {
                this.cardInfoDialog.showCard(this.dragCardData);
                this.successfulDrag = true;
            }
            this.dragCardData = null;
            this.dragStartX = null;
            this.dragStartY = null;
            return false;
        }
        return true;
    },

    clickCardFunction:function (event) {
        var that = this;

        var tar = $(event.target);
        if (tar.length == 1 && tar[0].tagName == "A")
            return true;

        if (!this.successfulDrag && this.cardInfoDialog.isOpen()) {
            this.cardInfoDialog.mouseUp();
            event.stopPropagation();
            return false;
        }

        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (event.which >= 1) {
                if (!this.successfulDrag) {
                    if (event.shiftKey || event.which > 1) {
                        this.cardInfoDialog.showCard(selectedCardElem.data("card"));
                        return false;
                    }
                    event.stopPropagation();
                }
            }
            return false;
        }
        return true;
    },

    clearList:function (rootElem) {
        $(".card", this.cardsDiv).remove();
        this.currencyCount = rootElem.getAttribute("currency");
        this.pocketDiv.html(formatPrice(this.currencyCount));
    },

    addCardToList:function (elem, type, blueprintId, testingText, backSideTestingText, count) {
        var buyPrice = elem.getAttribute("buyPrice");
        var sellPrice = elem.getAttribute("sellPrice");
        var tradeFoil = elem.getAttribute("tradeFoil");

        var sizeListeners = new Array();
        sizeListeners[0] = {
            sizeChanged:function (cardElem, width, height) {
                $(".owned", cardElem).css({position:"absolute", left:5, top:height - 60, width:30, height:30});
                $(".buyPrice", cardElem).css({position:"absolute", left:40, top:height - 80, width:width - 45, height:25});
                $(".sellPrice", cardElem).css({position:"absolute", left:40, top:height - 50, width:width - 45, height:25});
                $(".tradeFoil", cardElem).css({position:"absolute", left:40, top:height - 20, width:width - 45, height:15});
                $(".sellAll", cardElem).css({position:"absolute", left:40, top:height - 100, width:width - 45, height:15});
            }
        };

        var cardDiv = null;
        var card = null;

        if (type == "pack") {
            card = new Card(blueprintId, null, null, "merchant", "collection", "player");
            cardDiv = Card.CreateCardDiv(card.imageUrl, card.testingText, null, false, true, true, card.incomplete, null);
            cardDiv.data("card", card);
            cardDiv.data("sizeListeners", sizeListeners);
            this.cardsDiv.append(cardDiv);
        } else if (type == "card") {
            card = new Card(blueprintId, testingText, backSideTestingText, "merchant", "collection", "player");
            cardDiv = Card.CreateCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), true, false, card.incomplete, null);
            cardDiv.data("card", card);
            cardDiv.data("sizeListeners", sizeListeners);
            this.cardsDiv.append(cardDiv);
        }

        if (cardDiv != null) {
            var that = this;
            cardDiv.append("<div class='owned'>" + count + "</div>");
            if (!this.hideMerchant) {
                if (buyPrice != null) {
                    var formattedBuyPrice = formatPrice(buyPrice);
                    var buyBut = $("<div class='buyPrice'>Sell for<br/>" + formattedBuyPrice + "</div>").button();
                    buyBut.click(
                            function () {
                                that.displayMerchantAction(card, "Do you want to sell this card for " + formattedBuyPrice + "?",
                                        function () {
                                            that.comm.sellItem(blueprintId, buyPrice, function () {
                                                that.cardFilter.getCollection();
                                            });
                                        });
                            });
                    cardDiv.append(buyBut);

                    if (count > 1) {
                        var sellAllBut = $("<div class='sellAll'>Sell all</div>").button();
                        sellAllBut.click(
                                function() {
                                    that.displayMerchantAction(card, "Do you want to sell all of your copies of this card?",
                                            function () {
                                                that.comm.sellAll(blueprintId, buyPrice, function () {
                                                    that.cardFilter.getCollection();
                                                })
                                            }
                                    )
                                }
                        );
                        cardDiv.append(sellAllBut);
                    }
                }
                if (sellPrice != null) {
                    var formattedSellPrice = formatPrice(sellPrice);
                    var sellBut = $("<div class='sellPrice'>Buy for<br/>" + formattedSellPrice + "</div>").button();
                    sellBut.click(
                            function () {
                                that.displayMerchantAction(card, "If you buy this item, it will be added to your 'My Cards' collection, which can be accessed via the Deck Builder.<br/><br/>Do you want to buy this item for " + formattedSellPrice + "?",
                                        function () {
                                            that.comm.buyItem(blueprintId, sellPrice, function () {
                                                that.cardFilter.getCollection();
                                            });
                                        });
                            });
                    if (parseInt(sellPrice) > parseInt(this.currencyCount)) {
                        sellBut.button({disabled:true});
                        sellBut.css({color:"#ff0000"});
                    }
                    cardDiv.append(sellBut);
                }
                if (tradeFoil == "true") {
                    var tradeFoilBut = $("<div class='tradeFoil'>Trade 4 for foil</div>").button();
                    tradeFoilBut.click(
                            function () {
                                that.displayMerchantAction(card, "Do you want to trade 4 of this card and 5<img src='https://res.starwarsccg.org/gemp/gold.png'/> in currency for a foil version of the card?",
                                        function () {
                                            that.comm.tradeInFoil(blueprintId, function () {
                                                that.cardFilter.getCollection();
                                            });
                                        });
                            });
                    cardDiv.append(tradeFoilBut);
                }
            }
        }
    },

    displayMerchantAction:function (card, text, yesFunc) {
        var that = this;
        this.questionDialog.html("");
        this.questionDialog.html("<div style='scroll: auto'></div>");
        var floatCardDiv = $("<div style='float: left;'></div>");
        floatCardDiv.append(Card.CreateFullCardDiv(card.imageUrl, null, card.foil, card.horizontal, card.isPack()));
        this.questionDialog.append(floatCardDiv);
        var questionDiv = $("<div id='cardEffects'>" + text + "</div>");
        questionDiv.append("<br/>");
        questionDiv.append($("<button>Yes</button>").button().click(
                function () {
                    that.questionDialog.dialog("close");
                    yesFunc();
                }));
        questionDiv.append($("<button>No</button>").button().click(
                function () {
                    that.questionDialog.dialog("close");
                }));
        this.questionDialog.append(questionDiv);

        var windowWidth = $(window).width();
        var windowHeight = $(window).height();

        var horSpace = 230;
        var vertSpace = 45;

        if (card.horizontal) {
            // 500x360
            this.questionDialog.dialog({width:Math.min(500 + horSpace, windowWidth), height:Math.min(380 + vertSpace, windowHeight)});
        } else {
            // 360x500
            this.questionDialog.dialog({width:Math.min(360 + horSpace, windowWidth), height:Math.min(520 + vertSpace, windowHeight)});
        }
        this.questionDialog.dialog("open");
    },

    finishList:function () {
        this.cardsGroup.layoutCards();
    },

    layoutUI:function () {
        var filterWidth = $(this.filterDiv).width();
        var filterHeight = $(this.filterDiv).height();
        this.cardFilter.layoutUi(0, 0, filterWidth, filterHeight);

        var cardsGroupWidth = $(this.cardsDiv).width();
        var cardsGroupHeight = $(this.cardsDiv).height();
        this.cardsGroup.setBounds(0, 0, cardsGroupWidth, cardsGroupHeight);

        this.pocketDiv.css({position:"absolute", left:filterWidth - 60, top:35, width:60, height:18});
        this.hideMerchantDiv.css({position:"absolute", left:filterWidth - 100, top:62, width:100, height:18});
        this.countDiv.css({position:"absolute", left:filterWidth - 175, top:80, width:175, height:20});
    },

    processError:function (xhr, ajaxOptions, thrownError) {
        if (thrownError != "abort")
            alert("There was a problem during communication with server");
    }
});
