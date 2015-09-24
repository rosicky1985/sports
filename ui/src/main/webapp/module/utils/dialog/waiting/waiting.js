import waiting_html from './waiting.html!text'
import 'bootstrap';

var myApp;
myApp = myApp || (function () {
    var pleaseWaitDiv = $(waiting_html);
    pleaseWaitDiv.hide();
    return {
        show: function() {
            pleaseWaitDiv.modal('show');
        },
        hide: function () {
            pleaseWaitDiv.modal('hide');
        },

    };
})();

export default myApp;