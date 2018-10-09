window.base = window.base || {};

window.base.myCommutesController = (() => {
    var tabToRemove = 'drive-container';
    var previousBtn = 'drive-btn';
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
        },
        tabViews: () => {

        }
    };

    return controller;
});