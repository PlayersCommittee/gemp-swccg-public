var deliveryDialogs = {};
var deliveryGroups = {};

function deliveryService(xml) {
    log("Delivered a package:");
    log(xml);

    var root = xml.documentElement;
    if (root.tagName == "delivery") {
        var collections = root.getElementsByTagName("collectionType");
        for (var i = 0; i < collections.length; i++) {
            var collection = collections[i];

            var collectionName = collection.getAttribute("name");
            var deliveryDialogResize = (function (name) {
                return function () {
                    var width = deliveryDialogs[name].width() + 10;
                    var height = deliveryDialogs[name].height() + 10;
                    deliveryGroups[name].setBounds(2, 2, width - 2 * 2, height - 2 * 2);
                };
            })(collectionName);

            if (deliveryDialogs[collectionName] == null) {
                deliveryDialogs[collectionName] = $("<div></div>").dialog({
                    title:"New items - " + collectionName,
                    autoOpen:false,
                    closeOnEscape:false,
                    resizable:true,
                    width:250,
                    height:150
                });

                deliveryGroups[collectionName] = new NormalCardGroup(deliveryDialogs[collectionName], function (card) {
                    return true;
                }, false);

                deliveryDialogs[collectionName].bind("dialogresize", deliveryDialogResize);
                deliveryDialogs[collectionName].bind("dialogclose",
                    function () {
                        deliveryDialogs[collectionName].html("");
                    });
            }

            var packs = collection.getElementsByTagName("pack");
            for (var j = 0; j < packs.length; j++) {
                var packElem = packs[j];
                var blueprintId = packElem.getAttribute("blueprintId");
                var count = packElem.getAttribute("count");
                var card = new Card(blueprintId, null, null, "delivery", "deliveryPack" + i, "player");
                card.tokens = {"count":count};
                var cardDiv = createCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), false, true, card.incomplete);
                cardDiv.data("card", card);
                deliveryDialogs[collectionName].append(cardDiv);
            }

            var cards = collection.getElementsByTagName("card");
            for (var j = 0; j < cards.length; j++) {
                var cardElem = cards[j];
                var blueprintId = cardElem.getAttribute("blueprintId");
                var count = cardElem.getAttribute("count");
                var card = new Card(blueprintId, null, null, "delivery", "deliveryCard" + i, "player");
                card.tokens = {"count":count};
                var cardDiv = createCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), false, false, card.incomplete);
                cardDiv.data("card", card);
                deliveryDialogs[collectionName].append(cardDiv);
            }

            openSizeDialog(deliveryDialogs[collectionName]);
            deliveryDialogResize();
        }
    }
}