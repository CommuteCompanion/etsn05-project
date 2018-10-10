window.base = window.base || {};

window.base.myCommutesController = (() => {
    var tabToRemove = 'drive-container';
    var previousBtn = 'drive-btn';

    var rateModalElement = document.getElementById('rateModal');
    var editModalElement = document.getElementById('editModal');
    var rateModal = new Modal(rateModalElement,
                          { 
        content: '<div class="modal-body">Give it a rate!<div class="container"><div class="row"><div class="rating"><input type="button" id="star5" name="rating" value="5" /><label for="star5" title="Meh">5 stars</label><input type="button" id="star4" name="rating" value="4" /><label for="star4" title="Kinda bad">4 stars</label><input type="button" id="star3" name="rating" value="3" /><label for="star3" title="Kinda bad">3 stars</label><input type="button" id="star2" name="rating" value="2" /><label for="star2" title="Sucks big tim">2 stars</label><input type="button" id="star1" name="rating" value="1" /><label for="star1" title="Sucks big time">1 star</label></div></div></div></div>', // sets modal content
    });
    
    var editModal = new Modal(editModalElement,
                              {
        content: '<div class="modal-body">Edit</div>',
    });
    const controller = {
        load: () => {
            document.getElementById(previousBtn).style.background = '#fff'
            document.getElementById('trip-container').style.display = 'none';
            document.getElementById('archive-container').style.display = 'none';

            document.getElementById('drive-btn').onclick = function() {  
                document.getElementById(previousBtn).style.background = '#A8D0E6';
                previousBtn = 'drive-btn';
                document.getElementById(tabToRemove).style.display = 'none';
                document.getElementById('drive-container').style.display = 'block';
                document.getElementById(previousBtn).style.background = '#fff'
                tabToRemove = 'drive-container';
            };
            document.getElementById('trip-btn').onclick = function() {
                document.getElementById(previousBtn).style.background = '#A8D0E6';
                previousBtn = 'trip-btn';
                document.getElementById(tabToRemove).style.display = 'none';
                document.getElementById('trip-container').style.display = 'block';
                document.getElementById(previousBtn).style.background = '#fff'
                tabToRemove = 'trip-container';
            };
            document.getElementById('archive-btn').onclick = function() {
                document.getElementById(previousBtn).style.background = '#A8D0E6';
                previousBtn = 'archive-btn';
                document.getElementById(tabToRemove).style.display = 'none';    
                document.getElementById('archive-container').style.display = 'block';
                document.getElementById(previousBtn).style.background = '#fff'
                tabToRemove = 'archive-container';
            };

            document.getElementById('drive-edit-modalTrigger').onclick = controller.showModal;

            document.getElementById('drive-rate-modalTrigger').onclick = controller.showRateModal;

            document.getElementById('trip-edit-modalTrigger').onclick = controller.showModal;
            
            document.getElementById('trip-rate-modalTrigger').onclick = controller.showRateModal;
            
            document.getElementById('star1').onclick = controller.hideRateModal;
            document.getElementById('star2').onclick = controller.hideRateModal;
            document.getElementById('star3').onclick = controller.hideRateModal;
            document.getElementById('star4').onclick = controller.hideRateModal;
            document.getElementById('star5').onclick = controller.hideRateModal;
            
        },
        showModal: () => {
            editModal.show();
        },
        showRateModal: () => {
            rateModal.show();
        },
        hideEditModal: () => {
            editModal.hide();
        },
        hideRateModal: () => {
            rateModal.hide();
        }
    };

    return controller;
});